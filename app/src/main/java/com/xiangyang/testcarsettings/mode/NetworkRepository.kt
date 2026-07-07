package com.xiangyang.testcarsettings.mode

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xiangyang.testcarsettings.bean.WifiItemData
import com.xiangyang.testcarsettings.constant.Constants
import com.xiangyang.testcarsettings.util.LogUtils
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NetworkRepository(private val context: Context) {
    // 获取wifi manager
    private val mWifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    // 引入协程作用域
    private val repositoryScope =
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)

    // WIFI 开关
    private val _wifiHardwareStatus = MutableLiveData<Int>()
    val wifiHardwareStatus: LiveData<Int> = _wifiHardwareStatus

    // 扫描结果
    private val _wifiListLiveData = MutableLiveData<List<WifiItemData>>()
    val wifiListLiveData: LiveData<List<WifiItemData>> = _wifiListLiveData

    // 已连接结果
    private val _wifiConnectedListLiveData = MutableLiveData<WifiItemData>()
    val wifiConnectedListLiveData: LiveData<WifiItemData> = _wifiConnectedListLiveData

    // 监听wifi广播
    private val mWifiReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            // 监听 Wi-Fi 硬件开关状态的切换
            if (WifiManager.WIFI_STATE_CHANGED_ACTION == intent?.action) {
                val wifiState =
                    intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
                LogUtils.d("Wi-Fi 当前开启状态: $wifiState")
                _wifiHardwareStatus.postValue(wifiState)
                if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                    LogUtils.d("Wi-Fi 已完全开启，开始主动触发第一次扫描...")
                    repositoryScope.launch {
                        kotlinx.coroutines.delay(500)
                        startWifiScan(Constants.MANUAL_REFRESH)
                    }
                }
            }
            // 监听附近热点扫描完成
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION == intent?.action) {
                val isSuccess = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, true)
                LogUtils.d("Wi-Fi 扫描完成, 是否成功更新: $isSuccess")
                // ture:扫描成功，获取最新wifi数据，false:扫描失败，获取缓存wifi数据
                repositoryScope.launch {
                    // 获取扫描结果，加入子线程
                    getScanResults()
                }
                if (!isSuccess) {
                    LogUtils.w("本次扫描由于系统限流或硬件忙碌未真正更新，当前展示为系统缓存热点")
                }

            }
        }

    }

    init {
        _wifiHardwareStatus.value = mWifiManager.wifiState
        val filter = IntentFilter().apply {
            // 监听 Wi-Fi 硬件开关状态的切换
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
            // 监听附近热点扫描完成
            addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        }
        context.registerReceiver(mWifiReceiver, filter)
    }

    /**
     * wifi 向下发送开关
     * @param enabled 发送状态
     */
    fun setWifiEnabled(enabled: Boolean) {
        try {
            mWifiManager.isWifiEnabled = enabled
            LogUtils.d("setWifiEnabled $enabled")
        } catch (e: Exception) {
            LogUtils.e("setWifiEnabled", tr = e)
        }

    }

    /**
     * 获取wifi扫描结果
     */
    @SuppressLint("MissingPermission", "NewApi")
    private suspend fun getScanResults() {
        try {
            val scanResults = mWifiManager.scanResults ?: return
            val currentConnectedBssid = getConnectedBssid()
            LogUtils.i("getScanResults currentConnectedBssid:$currentConnectedBssid")
            // 定义比较器
            val wifiComparator = compareByDescending<WifiItemData> {
                it.isConnected
            }.thenByDescending { it.level }
            val wifiItemList = scanResults.filter { it.SSID.isNotEmpty() }.map { result ->
                val level = mWifiManager.calculateSignalLevel(result.level)
                val isEncrypted = checkIsEncrypted(result.capabilities)
                val isConnected =
                    (currentConnectedBssid != null && currentConnectedBssid == result.BSSID)
                LogUtils.i("getScanResults isConnected:$isConnected ssid: ${result.toString()}")

                WifiItemData(
                    result.SSID,
                    result.level,
                    level,
                    result.capabilities,
                    isEncrypted,
                    isConnected
                )
            }.sortedWith(wifiComparator).distinctBy { it.ssid }
            val connectedItem = wifiItemList.firstOrNull {
                LogUtils.i("getScanResults it.isConnected:${it.isConnected} it.ssid:${it.ssid}")
                it.isConnected
            }

            val unConnectedList = wifiItemList.filter { !it.isConnected }

            _wifiConnectedListLiveData.postValue(connectedItem)
            _wifiListLiveData.postValue(unConnectedList)

        } catch (e: Exception) {
            LogUtils.e("获取扫描结果异常", tr = e)
        }
    }

    /**
     * wifi扫描
     */
    fun startWifiScan(refreshState: Int) {
        try {
            val success = mWifiManager.startScan()
            LogUtils.d(
                if (refreshState == Constants.MANUAL_REFRESH) {
                    "手动触发 startScan 结果: $success"
                } else {
                    "自动触发 startScan 结果: $success"
                }
            )

        } catch (e: Exception) {
            LogUtils.e("触发扫描异常", tr = e)
        }

    }

    /**
     * wifi加密判断
     * @param capabilities 加密协议 capabilities=[WPA-PSK-TKIP+CCMP][WPA2-PSK-TKIP+CCMP][RSN-PSK-TKIP+CCMP][ESS]
     */
    private fun checkIsEncrypted(capabilities: String?): Boolean {
        if (capabilities.isNullOrEmpty()) return false
        val cap = capabilities.uppercase()
        return cap.contains("WPA") ||
                cap.contains("WPA2") ||
                cap.contains("RSN") ||
                cap.contains("PSK") ||
                cap.contains("SUITE_B")
    }

    /**
     * 获取已连接wifi的BSSID
     */
    private fun getConnectedBssid(): String? {
        val wifiInfo = mWifiManager.connectionInfo ?: return null
        val bssid = wifiInfo.bssid
        if (bssid == null || wifiInfo.supplicantState != SupplicantState.COMPLETED) {
            return null
        }
        return bssid
    }

    /**
     * 删除并断开已连接wifi
     */
    fun removeCurrentConnectedWifi() {
        val wifiInfo = mWifiManager.connectionInfo ?: return
        if (wifiInfo.networkId != -1) {
            val currentNetworkId = wifiInfo.networkId
            LogUtils.d("removeCurrentConnectedWifi currentNetworkId: $currentNetworkId")
            forgetWifi(currentNetworkId)
//            LogUtils.i("removeCurrentConnectedWifi 移除结果: $isRemoved, 保存结果: $isSaved")
        }
    }

    /**
     * 反射忘记当前连接网络
     */
    @SuppressLint("PrivateApi")
    private fun forgetWifi(currentNetworkId: Int) {
        try {
            val forgetMethod = mWifiManager.javaClass.getMethod(
                "forget",
                Int::class.javaPrimitiveType,
                Class.forName("android.net.wifi.WifiManager\$ActionListener")
            )
            forgetMethod.invoke(mWifiManager, currentNetworkId, null)
            LogUtils.i("forgetWifi: 通过系统隐藏 API 成功触发忘记网络")
            // 强制断开当前硬件连接
            mWifiManager.disconnect()
        } catch (e: Exception) {
            LogUtils.e("反射调用 WifiManager.forget 失败", tr = e)
        }

    }

    fun release() {
        // 主动斩断与全局系统服务的强引用链条
        try {
            context.unregisterReceiver(mWifiReceiver)
            repositoryScope.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}