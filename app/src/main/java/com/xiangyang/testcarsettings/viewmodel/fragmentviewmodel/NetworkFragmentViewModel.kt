package com.xiangyang.testcarsettings.viewmodel.fragmentviewmodel

import android.app.Application
import android.net.wifi.WifiManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.xiangyang.testcarsettings.base.BaseViewModel
import com.xiangyang.testcarsettings.bean.WifiItemData
import com.xiangyang.testcarsettings.constant.Constants
import com.xiangyang.testcarsettings.mode.NetworkRepository
import com.xiangyang.testcarsettings.util.LogUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class NetworkFragmentViewModel(application: Application) : BaseViewModel(application) {
    enum class SwitchUIState {
        OFF,
        PENDING,
        ON
    }

    private val networkRepository = NetworkRepository(context)

    // 用来控制和取消定时任务的句柄
    private var autoRefreshJob: Job? = null

    // WIFI开发
    private val _switchUiState = MediatorLiveData<SwitchUIState>()
    val switchUiState: LiveData<SwitchUIState> = _switchUiState

    // WIFI扫描结果
    private val _wifiList = MediatorLiveData<List<WifiItemData>>()
    val wifiList: LiveData<List<WifiItemData>> = _wifiList

    // WIFI已连接结果
    private val _wifiConnectedItem = MediatorLiveData<WifiItemData>()
    val wifiConnectedItem: LiveData<WifiItemData> = _wifiConnectedItem

    init {
        _switchUiState.addSource(networkRepository.wifiHardwareStatus) { systemState ->
            when (systemState) {
                // WIFI关闭（1）
                WifiManager.WIFI_STATE_DISABLED -> {
                    _switchUiState.value = SwitchUIState.OFF
                    _wifiList.value = emptyList()
                }
                // WIFI 正在通电启动中（2） 或者是 正在断电关闭中（0）
                WifiManager.WIFI_STATE_ENABLING, WifiManager.WIFI_STATE_DISABLING -> {
                    _switchUiState.value = SwitchUIState.PENDING
                }
                // WIFI开启（3）
                WifiManager.WIFI_STATE_ENABLED -> {
                    _switchUiState.value = SwitchUIState.ON
                }
            }
        }
        _wifiList.addSource(networkRepository.wifiListLiveData) { wifiList ->
            if (_switchUiState.value == SwitchUIState.ON) {
                _wifiList.value = wifiList
            }
        }
        _wifiConnectedItem.addSource(networkRepository.wifiConnectedListLiveData) { wifiItemData ->
            if (_switchUiState.value == SwitchUIState.ON) {
                _wifiConnectedItem.value = wifiItemData
            }
        }
    }


    // wifi开关切换
    fun toggleWifiSwitch(isEnabled: Boolean) {
        networkRepository.setWifiEnabled(isEnabled)
    }

    // wifi手动刷新
    fun wifiRefresh() {
        networkRepository.startWifiScan(Constants.MANUAL_REFRESH)
    }

    // wifi自动刷新
    fun startAutoRefresh() {
        if (autoRefreshJob?.isActive == true) return
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                LogUtils.d("startAutoRefresh 自动刷新开始")
                networkRepository.startWifiScan(Constants.AUTO_REFRESH)
                delay(12000)
            }
        }
    }

    // 停止wifi自动刷新
    fun stopAutoRefresh() {
        LogUtils.d("startAutoRefresh 自动刷新停止")
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }

    // 删除并断开已连接wifi
    fun removeCurrentConnectedWifi() {
        networkRepository.removeCurrentConnectedWifi()
    }

    override fun onCleared() {
        super.onCleared()
        networkRepository.release()
        stopAutoRefresh()
    }

}