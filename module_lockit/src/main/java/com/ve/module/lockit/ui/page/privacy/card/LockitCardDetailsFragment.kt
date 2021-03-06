package com.ve.module.lockit.ui.page.privacy.card

import android.os.Bundle
import android.view.View
import com.ve.lib.common.base.view.vm.BaseVmFragment
import com.ve.lib.common.ext.setOnclickNoRepeatListener
import com.ve.lib.common.vutils.DialogUtil
import com.ve.lib.common.vutils.LogUtil
import com.ve.lib.common.vutils.ShareUtil
import com.ve.module.lockit.R
import com.ve.module.lockit.common.event.RefreshDataEvent
import com.ve.module.lockit.databinding.LockitFragmentDetailsCardBinding
import com.ve.module.lockit.respository.database.entity.PrivacyCard
import com.ve.module.lockit.ui.page.container.LockitContainerActivity
import com.ve.module.lockit.common.enums.EditTypeEnum
import com.ve.module.lockit.ui.viewmodel.LockitPrivacyCardViewModel
import com.ve.module.lockit.utils.StickUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @Author  weiyi
 * @Date 2022/4/11
 * @Description  current project lockit-android
 */
class LockitCardDetailsFragment :
    BaseVmFragment<LockitFragmentDetailsCardBinding, LockitPrivacyCardViewModel>(),
    View.OnClickListener {

    companion object {
        const val PRIVACY_DATA_KEY = "PrivacyInfo"
    }

    override fun attachViewBinding(): LockitFragmentDetailsCardBinding {
        return LockitFragmentDetailsCardBinding.inflate(layoutInflater)
    }

    override fun attachViewModelClass(): Class<LockitPrivacyCardViewModel> {
        return LockitPrivacyCardViewModel::class.java
    }

    var mPrivacyInfo: PrivacyCard? = null

    override fun initView(savedInstanceState: Bundle?) {

        mPrivacyInfo = arguments?.getSerializable(PRIVACY_DATA_KEY) as PrivacyCard
        LogUtil.msg(mPrivacyInfo.toString())
        LogUtil.msg(mPrivacyInfo!!.getPrivacyTags().toString())
        LogUtil.msg(mPrivacyInfo!!.getPrivacyFolder().toString())

        if (mPrivacyInfo != null) {
            showPrivacyInfo(mPrivacyInfo!!)
        }

    }

    override fun initListener() {
        super.initListener()
        mBinding.btnEdit.setOnclickNoRepeatListener(this)
        mBinding.btnCopy.setOnclickNoRepeatListener(this)
        mBinding.btnDelete.setOnclickNoRepeatListener(this)
        mBinding.ivBrowser.setOnclickNoRepeatListener(this)
    }

    override fun initObserver() {
        super.initObserver()

        mViewModel.deletePrivacyCardResult.observe(this) {
            if (it > 0) {
                showMsg("???????????????$it")
                EventBus.getDefault().post(RefreshDataEvent(PrivacyCard::class.java.name))
                activity?.finish()
            } else {
                showMsg("???????????????$it")
            }
        }
    }

    override fun onClick(v: View?) {
        val account = mBinding.etDetailAccount.text.toString()
        val password = mBinding.etDetailPassword.text.toString()
        val url = mBinding.etDetailOwner.text.toString()
        when (v?.id) {
            /**
             * ????????????
             */
            R.id.btn_edit -> {
                val bundle = Bundle()
                bundle.putInt(LockitCardEditFragment.FRAGMENT_TYPE_KEY, EditTypeEnum.EDIT_TAG_TYPE)
                bundle.putSerializable(LockitCardEditFragment.FRAGMENT_DATA_KEY, mPrivacyInfo)
                LockitContainerActivity.start(
                    mContext,
                    LockitCardEditFragment::class.java,
                    "??????:${mPrivacyInfo?.account}",
                    bundle
                )
            }
            R.id.btn_delete -> {
                DialogUtil.getConfirmDialog(
                    mContext,
                    "?????????????????????:$account",
                    onOKClickListener = { d, w ->
                        mViewModel.deletePrivacyCard(mPrivacyInfo!!)
                    },
                    onCancelClickListener = { d, w ->
                        showMsg("????????????")
                    }
                ).show()

            }
            R.id.btn_copy -> {
                StickUtils.copyToClipboard(mContext, "??????:$account\n??????:$password")
            }
            R.id.iv_browser -> {
                ShareUtil.goUrl(mContext,url)
            }
        }
    }

    /**
     * ??????????????????
     */
    private fun showPrivacyInfo(privacyInfo: PrivacyCard) {
        val folder = privacyInfo.getPrivacyFolder()
        val tags = privacyInfo.getPrivacyTags()
        val tagsName = tags.map { it.tagName }

        mBinding.apply {
            tvPrivacyName.text = privacyInfo.name
            tvPrivacyFolder.text = folder.folderName
            tvPrivacyTag.text = tagsName.toString()
            tvPrivacyUpdateTime.text = privacyInfo.updateTime
        }

        mBinding.tvAppName.text = privacyInfo.name

        mBinding.apply {
            etDetailAccount.setText(privacyInfo.account)
            etDetailPassword.setText(privacyInfo.password)
            etDetailOwner.setText(privacyInfo.owner)
            etDetailRemark.setText(privacyInfo.remark)
        }
    }



    /**
     * Refresh Data Event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshDataEvent(event: RefreshDataEvent) {
        if (PrivacyCard::class.java.name == event.dataClassName) {
            LogUtil.d("$mViewName receiver event " + event.dataClassName)
            if (event.data is PrivacyCard) {
                mPrivacyInfo = event.data
                showPrivacyInfo(mPrivacyInfo!!)
            }
            hasLoadData = false
        }
    }
}