package com.xiangyang.testcarsettings.bean

import com.xiangyang.testcarsettings.viewmodel.SettingsActivityTabTypeViewModel

data class TabData(
    val tabType: SettingsActivityTabTypeViewModel.TabType,
    val title: String,
    val iconRes: Int,
    var isSelected: Boolean = false
)
