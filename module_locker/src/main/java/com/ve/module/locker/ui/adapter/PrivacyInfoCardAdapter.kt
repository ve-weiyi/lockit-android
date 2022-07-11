package com.ve.module.locker.ui.adapter

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.module.UpFetchModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ve.lib.common.ext.spanText
import com.ve.lib.common.vutils.LogUtil
import com.ve.lib.common.vutils.ToastUtil
import com.ve.module.locker.R
import com.ve.module.locker.model.db.entity.PrivacyCard
import com.ve.module.locker.ui.page.container.LockerContainerActivity

import com.ve.module.locker.ui.page.privacy.card.LockerCardDetailsFragment
import com.ve.module.locker.utils.StickUtils

/**
 * @Description hello word!
 * @Author  weiyi
 * @Date 2022/4/10
 */
class PrivacyInfoCardAdapter :
    BaseSectionQuickAdapter<PrivacyCard, BaseViewHolder>(
        com.ve.lib.common.R.layout.item_sticky_header, R.layout.locker_item_privacy_card
    ), LoadMoreModule, DraggableModule, UpFetchModule {

    init {
        setOnItemLongClickListener { adapter, view, position ->
            ToastUtil.showCenter("已复制密码")
            StickUtils.copy(context, data[position].password)
            true
        }
    }

    var isCheckMode = false
    private var isAllCheck = false
    private var mSelectList = mutableListOf<PrivacyCard>()
    private var keywords = ""

    override fun convert(holder: BaseViewHolder, item: PrivacyCard) {
        if (keywords.isNotEmpty()) {
            LogUtil.msg("keywords= $keywords")
            holder.getView<TextView>(R.id.tv_privacy_name).apply {
                spanText(keywords, text.toString())
            }
        }

        holder.setText(R.id.tv_privacy_name, item.name)
        holder.setText(R.id.tv_privacy_remark, item.remark)
        /**
         * 账户只显示 部分
         */
        var account=item.account
        if(account.length>=10){
            holder.setText(R.id.tv_privacy_account, account.substring(0,2)+"****"+account.substring(account.length-4,account.length))
        }else if(account.length>=4){
            holder.setText(R.id.tv_privacy_account,"****"+account.substring(account.length-4,account.length))
        }else{
            holder.setText(R.id.tv_privacy_account,account)
        }

        val checkBox = holder.getView<CheckBox>(R.id.check_button)
        checkBox.setOnClickListener {
            checkBox.apply {
//                isChecked=!isChecked
                if (isChecked) {
                    //选择
                    if (!mSelectList.contains(item)) {
                        LogUtil.msg("add 11 +$item}")
                        mSelectList.add(item)
                    }
                } else {
                    //取消选择
                    if (mSelectList.contains(item)) {
                        LogUtil.msg("remove 1 +${item}")
                        mSelectList.remove(item)
                    }
                }
            }
        }

        if (isCheckMode) {
            checkBox.visibility = View.VISIBLE
            checkBox.isChecked = isAllCheck
        } else {
            checkBox.visibility = View.GONE
        }
    }

    fun setKeywords(key: String) {
        keywords = key
    }

    fun changeAllState() {
        isAllCheck = !isAllCheck
        if (isAllCheck) {
            data.forEach { pass ->
                if (!pass.isHeader && !mSelectList.contains(pass))
                    mSelectList.add(pass)
            }
        } else {
            mSelectList.removeAll(mSelectList)
        }
    }


    override fun convertHeader(helper: BaseViewHolder, item: PrivacyCard) {
        LogUtil.msg("head " + item.headerName)
        helper.setText(com.ve.lib.common.R.id.tv_header, item.headerName)
        helper.setEnabled(com.ve.lib.common.R.id.tv_header, false)
    }

    fun getSelectData(): MutableList<PrivacyCard> {
        return mSelectList
    }
    

    override fun setOnItemClick(v: View, position: Int) {
        super.setOnItemChildClick(v, position)
        if (data[position].isHeader)
            return
        if (!isCheckMode) {
            val privacyInfo = data[position]
            val bundle = Bundle()
            bundle.putSerializable(LockerCardDetailsFragment.PRIVACY_DATA_KEY, privacyInfo)

            LockerContainerActivity.start(
                context,
                LockerCardDetailsFragment::class.java.name,
                privacyInfo.account,
                bundle
            )
        } else {
            v.findViewById<CheckBox>(R.id.check_button).apply {
                isChecked = !isChecked
                if (isChecked) {
                    //选择
                    if (!mSelectList.contains(data[position])) {
                        LogUtil.msg("add +${data[position]}")
                        mSelectList.add(data[position])
                    }
                } else {
                    //取消选择
                    if (mSelectList.contains(data[position])) {
                        LogUtil.msg("remove +${data[position]}")
                        mSelectList.remove(data[position])
                    }
                }
            }
        }
    }

}