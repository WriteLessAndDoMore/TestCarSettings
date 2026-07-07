package com.xiangyang.testcarsettings.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel

open class BaseViewModel(application: Application): AndroidViewModel(application) {
    /** 方便子类 ViewModel 快速拿到上下文，用来初始化车载各种 Manager */
    protected val context: Application get() = getApplication()

    /**
     * 当 Activity/Fragment 彻底销毁时，这个方法会被触发。
     * 可以在这里统一取消当前页面所有未完成的 Binder 异步挂起、协程或硬件服务监听
     */
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}