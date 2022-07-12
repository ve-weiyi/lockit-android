package com.ve.module.locker.ui.page.privacy.pass

import android.text.Editable
import android.text.TextWatcher
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ve.lib.common.base.view.list.BaseVmListFragment
import com.ve.lib.common.vutils.LogUtil
import com.ve.module.locker.databinding.LockerFragmentListPrivacySearchBinding
import com.ve.module.locker.respository.database.entity.PrivacyPass
import com.ve.module.locker.ui.adapter.PrivacyInfoPassAdapter
import com.ve.module.locker.ui.viewmodel.LockerPrivacyPassViewModel

/**
 * @Author  weiyi
 * @Date 2022/4/18
 * @Description  current project locker-android
 */
class LockerPassSearchFragment :
    BaseVmListFragment<LockerFragmentListPrivacySearchBinding, LockerPrivacyPassViewModel, PrivacyPass>() {

    override fun attachAdapter(): BaseQuickAdapter<PrivacyPass, *> {
        return PrivacyInfoPassAdapter()
    }

    override fun attachViewBinding(): LockerFragmentListPrivacySearchBinding {
        return LockerFragmentListPrivacySearchBinding.inflate(layoutInflater)
    }

    override fun attachViewModelClass(): Class<LockerPrivacyPassViewModel> {
        return LockerPrivacyPassViewModel::class.java
    }

    override fun initListView() {
        mLayoutStatusView = mBinding.fragmentRefreshLayout.multipleStatusView
        mRecyclerView = mBinding.fragmentRefreshLayout.recyclerView
        mSwipeRefreshLayout = mBinding.fragmentRefreshLayout.swipeRefreshLayout

    }

    override fun initListener() {
        super.initListener()
        mBinding.tvSearchText.addTextChangedListener(textWatcher)
    }

    override fun initWebData() {
        super.initWebData()
        mViewModel.getPrivacyPassList()
    }

    var mKeywords = ""
    val mAdapter by lazy { mListAdapter as PrivacyInfoPassAdapter }

    override fun initObserver() {
        super.initObserver()
        mViewModel.getPrivacyPassListResult.observe(this) {
            showAtAdapter(true, it)
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            LogUtil.msg("before " + s.toString())
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            LogUtil.msg("on " + s.toString())
        }

        override fun afterTextChanged(s: Editable?) {
            LogUtil.msg("after " + s.toString())
            mAdapter.setKeywords(s.toString())
            mViewModel.searchPrivacyPassList(keyWords = s.toString())
        }
    }
}