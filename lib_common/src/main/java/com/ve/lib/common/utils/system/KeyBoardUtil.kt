package com.ve.lib.common.utils.system

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.ve.lib.common.utils.ui.ActivityController

/**
 * @author chenxz
 * @date 2018/11/19
 * @desc KeyBoardUtil
 */
object KeyBoardUtil {

    /**
     * 是否落在 EditText 区域
     */
    fun isHideKeyboard(view: View?, event: MotionEvent): Boolean {
        if (view != null && view is EditText) {
            val location = intArrayOf(0, 0)
            view.getLocationInWindow(location)
            //获取现在拥有焦点的控件view的位置，即EditText
            val left = location[0]
            val top = location[1]
            val bottom = top + view.height
            val right = left + view.width
            //判断我们手指点击的区域是否落在EditText上面，如果不是，则返回true，否则返回false
            val isInEt = (event.x > left && event.x < right && event.y > top && event.y < bottom)
            return !isInEt
        }
        return false
    }


    /**
     * 弹出软键盘
     */
    fun showSoftKeyboard(view: View) {
        val inputManger = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManger.showSoftInput(view, InputMethodManager.SHOW_FORCED)
        inputManger.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    /**
     * 关闭软键盘
     */
    fun closeSoftKeyboard(view: View) {
        val inputManger = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManger.hideSoftInputFromWindow(view.windowToken, 0)
    }

}