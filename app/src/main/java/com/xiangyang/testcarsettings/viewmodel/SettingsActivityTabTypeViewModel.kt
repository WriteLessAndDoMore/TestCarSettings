package com.xiangyang.testcarsettings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsActivityTabTypeViewModel: ViewModel() {
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