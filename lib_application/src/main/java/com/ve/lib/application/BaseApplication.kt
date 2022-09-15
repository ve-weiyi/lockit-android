package com.ve.lib.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.ve.lib.application.impl.ModuleApplication

/**
 * @Description
 *  基类application，只展示一些方法，不应做过多初始化操作
 * @Author  weiyi
 * @Date 2022/3/23
 */
abstract class BaseApplication : Application() {


    companion object {
        const val isDebug =true
        const val TAG = "app"
        lateinit var modulesClassName:List<String>

        //注释 忽略内存泄露警告
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    /**
     * 应用创建时回调
     */
    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
        context = applicationContext
        modulesApplicationInit()

    }


    open fun getModulesApplicationName():List<String>{
        return mutableListOf()
    }

    open fun modulesApplicationInit() {
        modulesClassName=getModulesApplicationName()
        for (moduleImpl in modulesClassName) {
            try {
                val clazz = Class.forName(moduleImpl)
                val obj = clazz.newInstance()
                if (obj is ModuleApplication) {
                    obj.onModuleCreate(this)
                    Log.e(TAG, "$moduleImpl is init")
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 应用终止时回调，不保证一定调用（如被系统回收时）
     */
    override fun onTerminate() {
        super.onTerminate()
    }

    /**
     * 内存不足时调用，在此方法中释放一些不必要的资源
     */
    override fun onLowMemory() {
        super.onLowMemory()
    }


    /**
     * 配置改变时触发，如屏幕旋转
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    /**
     * 注册对app内所有activity生命周期的监听，如获取栈顶activity对象，全局弹框；
     */
    override fun registerActivityLifecycleCallbacks(callback: ActivityLifecycleCallbacks?) {
        super.registerActivityLifecycleCallbacks(callback)
    }

}