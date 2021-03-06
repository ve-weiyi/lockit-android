package com.ve.module.lockit.ui.page.privacy.pass

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ve.lib.common.base.view.list.BaseVmListFragment
import com.ve.lib.common.ext.setOnclickNoRepeatListener
import com.ve.lib.common.vutils.DialogUtil
import com.ve.lib.common.vutils.LogUtil
import com.ve.module.lockit.R
import com.ve.module.lockit.common.event.RefreshDataEvent
import com.ve.module.lockit.databinding.LockitFragmentListPrivacyBinding
import com.ve.module.lockit.respository.database.entity.PrivacyFolder
import com.ve.module.lockit.respository.database.entity.PrivacyPass
import com.ve.module.lockit.ui.adapter.PrivacyInfoPassAdapter
import com.ve.module.lockit.ui.page.container.LockitContainerActivity
import com.ve.module.lockit.ui.viewmodel.LockitPrivacyPassViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal

/**
 * @Description hello word!
 * @Author  weiyi
 * @Date 2022/4/8
 */
class LockitPassListFragment :
    BaseVmListFragment<LockitFragmentListPrivacyBinding, LockitPrivacyPassViewModel, PrivacyPass>() {

    override fun attachViewBinding(): LockitFragmentListPrivacyBinding {
        return LockitFragmentListPrivacyBinding.inflate(layoutInflater)
    }


    override fun attachAdapter(): BaseQuickAdapter<PrivacyPass, *> {
        return PrivacyInfoPassAdapter()
    }

    val mAdapter by lazy { mListAdapter as PrivacyInfoPassAdapter }

    lateinit var mFolderList: MutableList<PrivacyFolder>

    override fun attachViewModelClass(): Class<LockitPrivacyPassViewModel> {
        return LockitPrivacyPassViewModel::class.java
    }

    override fun initListView() {
        mLayoutStatusView = mBinding.fragmentRefreshLayout.multipleStatusView
        mRecyclerView = mBinding.fragmentRefreshLayout.recyclerView
        mSwipeRefreshLayout = mBinding.fragmentRefreshLayout.swipeRefreshLayout
        setHasOptionsMenu(true)
        mFolderList = LitePal.findAll(PrivacyFolder::class.java)
    }

    override fun initWebData() {
        super.initWebData()
        mViewModel.getPrivacyPassList()
    }

    override fun initListener() {
        super.initListener()
        mBinding.floatingActionBtnAdd.setOnclickNoRepeatListener {
            LockitContainerActivity.start(
                mContext,
                LockitPassEditFragment::class.java,
                "????????????"
            )
        }
        mBinding.tvSearchText.setOnClickListener {
            LockitContainerActivity.start(
                mContext, LockitPassSearchFragment::class.java,null,null)
        }
    }

    override fun initObserver() {
        super.initObserver()
        mViewModel.getPrivacyPassListResult.observe(this) {
            showPrivacyList(it)
        }
        mViewModel.deletePrivacyPassListResult.observe(this) {
            showMsg("$it ????????????????????????")
            showMenuMore(false)
            mViewModel.getPrivacyPassList()
        }
        mViewModel.movePrivacyPassListResult.observe(this) {
            showMsg("$it ????????????????????????")
            showMenuMore(false)
            mViewModel.getPrivacyPassList()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        if (!isShowMore) {
            inflater.inflate(R.menu.lockit_menu_swap, menu)
        } else {
            inflater.inflate(R.menu.lockit_menu_edit, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    var isShowMore = false
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_swap -> {
                showMenuMore(true)
                return true
            }
            R.id.action_cancel -> {
                showMenuMore(false)
                return true
            }
            R.id.action_move -> {
                val list = mAdapter.getSelectData()
                moveList(list)
            }
            R.id.action_all -> {
                mAdapter.changeAllState()
                mAdapter.notifyDataSetChanged()
                LogUtil.msg(mAdapter.getSelectData().toString())
            }
            R.id.action_delete -> {
                val list = mAdapter.getSelectData()
                deleteList(list)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteList(list: MutableList<PrivacyPass>) {
        if (listValid(list)) {
            DialogUtil.getConfirmDialog(
                mContext,
                "?????????????????? ${list.size} ??????????????????????????????????????????",
                onOKClickListener = { _, _ ->
                    mViewModel.deletePrivacyPassList(list)
                },
                onCancelClickListener = { _, _ ->
                    showMsg("??????")
                },
            ).show()
        }
    }

    private fun moveList(list: MutableList<PrivacyPass>) {
        if (listValid(list)) {
            val folderNameList = mFolderList.map { it.folderName }
            DialogUtil.getSelectDialog(
                mContext,
                folderNameList.toTypedArray(),
                onClickListener = { dialog, which ->
                    mViewModel.movePrivacyPassList(
                        mAdapter.getSelectData(), folder = mFolderList[which]
                    )
                }
            ).show()
        }
    }

    private fun showMenuMore(isShow: Boolean) {
        isShowMore = isShow
        mAdapter.isCheckMode = isShow
        mAdapter.notifyDataSetChanged()
        activity?.invalidateOptionsMenu()
    }

    private fun listValid(list: MutableList<PrivacyPass>): Boolean {
        LogUtil.msg(list.toString())
        if (list.size == 0) {
            showMsg("??????????????????????????????")
            showMenuMore(false)
            return false
        }
        return true
    }

    private fun showPrivacyList(privacyList: MutableList<PrivacyPass>) {
        showAtAdapter(true, sortAtInstitution(privacyList))
    }

    /**
     * Refresh Data Event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshDataEvent(event: RefreshDataEvent) {
        if (PrivacyPass::class.java.name == event.dataClassName) {
            LogUtil.d("$mViewName receiver event " + event.dataClassName)
            hasLoadData = false
        }
    }

    /**
     * ??????????????????????????????
     */
    private fun sortAtUrl(privacyList: MutableList<PrivacyPass>): MutableList<PrivacyPass> {
        //???url??????
        privacyList.sortWith(compareBy { it.url })
        //????????????
        val list = mutableListOf<PrivacyPass>()
        var bHeader = true
        privacyList.forEach { privacyPass ->
            bHeader = true
            //???????????????header?????????????????????????????????
            for (i in list.indices) {
                if (privacyPass.url.first().toString() == list[i].headerName) {
                    bHeader = false
                    break
                }
            }
            //???????????????header???????????????header ??? context ???????????????????????????????????????
            if (bHeader && privacyPass.url.isNotEmpty()) {
                list.add(PrivacyPass(headerName = privacyPass.url.first().toString()))
            }
            LogUtil.msg("isHeader $bHeader " + privacyPass.url.first())
            //??????context
            list.add(privacyPass)
        }
        return list
    }

    /**
     * ?????????????????????
     */
    private fun sortAtTime(privacyList: MutableList<PrivacyPass>): MutableList<PrivacyPass> {
        //???url??????
        privacyList.sortWith(compareBy { it.updateTime })
        //????????????
        val list = mutableListOf<PrivacyPass>()
        var bHeader = true
        privacyList.forEach { privacyPass ->
            bHeader = true
            //???????????????header?????????????????????????????????
            for (i in list.indices) {
                if (privacyPass.updateTime.startsWith(list[i].headerName)) {
                    bHeader = false
                    break
                }
            }
            //???????????????header???????????????header ??? context ??????????????????
            if (bHeader) {
                list.add(PrivacyPass(headerName = privacyPass.updateTime.substring(0, 10)))
            }
            //??????context
            list.add(privacyPass)
        }
        return list
    }

    /**
     * ?????????????????????
     */
    private fun sortAtInstitution(privacyList: MutableList<PrivacyPass>): MutableList<PrivacyPass> {
        //???url??????
        privacyList.sortWith(compareBy { it.name })
        //????????????
        val list = mutableListOf<PrivacyPass>()
        var bHeader = true
        privacyList.forEach { privacyPass ->
            bHeader = true
            //???????????????header?????????????????????????????????
            for (i in list.indices) {
                if (privacyPass.name == list[i].headerName) {
                    bHeader = false
                    break
                }
            }
            //???????????????header???????????????header ??? context ??????????????????
            if (bHeader) {
                list.add(PrivacyPass(headerName = privacyPass.name))
            }
            //??????context
            list.add(privacyPass)
        }
        return list
    }
}