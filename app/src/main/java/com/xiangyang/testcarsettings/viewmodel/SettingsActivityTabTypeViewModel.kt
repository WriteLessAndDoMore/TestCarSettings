package com.xiangyang.testcarsettings.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xiangyang.testcarsettings.base.BaseViewModel

class SettingsActivityTabTypeViewModel(application: Application): BaseViewModel(application) {
    private var _currentTab = MutableLiveData<TabType>(TabType.NETWORK)
    val currentTab: LiveData<TabType> = _currentTab
    enum class TabType {
        NETWORK,
        SOUND,
        DISPLAY,
        VEHICLE,
        ADAS,
        SYSTEM,
        TEST
    }
    fun switchTab(tabType: TabType) {
        if (_currentTab.value != tabType) {
            _currentTab.value = tabType
        }
    }
}