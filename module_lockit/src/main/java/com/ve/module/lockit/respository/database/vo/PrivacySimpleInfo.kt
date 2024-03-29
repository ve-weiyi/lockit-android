package com.ve.module.lockit.respository.database.vo

import com.ve.lib.common.utils.color.ColorUtil
import com.ve.lib.common.utils.date.TimeUtil

/**
 * @Author  weiyi
 * @Date 2022/5/7
 * @Description  current project lockit-android
 */
data class PrivacySimpleInfo(
    //(varue = "隐私名", notes = "标签的备注名", example = "XX的QQ邮箱账号", position = 4)
    public var privacyName: String = "",

    //( varue = "隐私图标", notes = "标签的覆盖图标", example = "https://ve77.cn/blog/favicon.ico", position = 5 )
    public var privacyCover: String = ColorUtil.randomColor().toString(),

    //(varue = "隐私描述", notes = "标签描述", example = "床前明月光", position = 6)
    public var privacyDesc: String = "",

    //(varue = "更新时间", notes = "标签更新时间,不用填", position = 8)
    public var updateTime: String = TimeUtil.dateAndTime,
) {


}