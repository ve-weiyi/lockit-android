package com.ve.module.lockit.ui.page.key

import android.os.Bundle
import android.text.Html
import com.ve.lib.common.base.view.vm.BaseFragment
import com.ve.module.lockit.R
import com.ve.module.lockit.databinding.LockitFragmentKeyBinding
import com.ve.module.lockit.utils.PasswordUtils

/**
 * @Author  weiyi
 * @Date 2022/5/13
 * @Description  current project lockit-android
 */
class LockitKeyFragment:BaseFragment<LockitFragmentKeyBinding>() {
    override fun attachViewBinding(): LockitFragmentKeyBinding {
        return LockitFragmentKeyBinding.inflate(layoutInflater)
    }

    override fun initialize(saveInstanceState: Bundle?) {
        mBinding.tvKeyTip.apply {
            text= Html.fromHtml(getString(R.string.lockit_key_tip))
        }
        mBinding.tvKeyTip1.apply {
            text= Html.fromHtml(getString(R.string.lockit_key_tip_1))
        }
        mBinding.etKey.setText(PasswordUtils.generatePassword(32,true,true,true,true))
    }
}