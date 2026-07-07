package com.xiangyang.testcarsettings.bean

data class WifiItemData(
    val ssid: String, // WIFI名称
    val rssi: Int, // 原始信号强度
    val level: Int, // 转换后信号强度（格数）
    val capabilities: String, // 加密类型
    val isEncrypted: Boolean, // 是否加密
    val isConnected: Boolean
)
