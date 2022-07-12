package com.ve.module.locker.respository.database

/**
 * @Author weiyi
 * @Date 2022/7/10
 * @Description current project locker-android
 */
enum class PrivacyEnum(

    /**
     * 类型
     */
    val type: Int,


    /**
     * 描述
     */
    val desc: String

) {
    /**
     * 密码类
     */
    PASS(1,"密码"),

    CARD(2,"卡片"),

    FRIEND(3,"好友");

}