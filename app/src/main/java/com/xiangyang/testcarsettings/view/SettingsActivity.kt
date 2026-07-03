package com.xiangyang.testcarsettings.view

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.xiangyang.testcarsettings.R
import com.xiangyang.testcarsettings.adapter.SettingTabAdapter
import com.xiangyang.testcarsettings.base.BaseActivity
import com.xiangyang.testcarsettings.viewmodel.SettingsActivityTabTypeViewModel.*
import com.xiangyang.testcarsettings.bean.TabData
import com.xiangyang.testcarsettings.databinding.ActivitySettingsBinding
import com.xiangyang.testcarsettings.view.fragment.AdasFragment
import com.xiangyang.testcarsettings.view.fragment.DisplayFragment
import com.xiangyang.testcarsettings.view.fragment.NetworkFragment
import com.xiangyang.testcarsettings.view.fragment.SoundFragment
import com.xiangyang.testcarsettings.view.fragment.SystemFragment
import com.xiangyang.testcarsettings.view.fragment.TestFragment
import com.xiangyang.testcarsettings.view.fragment.VehicleFragment
import com.xiangyang.testcarsettings.viewmodel.SettingsActivityTabTypeViewModel

class SettingsActivity : BaseActivity<ActivitySettingsBinding, SettingsActivityTabTypeViewModel>() {
    private val fragments = HashMap<TabType, Fragment>()

    // 记录当前屏幕上正在显示的那个 Fragment，用来在切换时去 hide 它
    private var currentFragment: Fragment? = null

    // 声明适配器引用
    private var tabAdapter: SettingTabAdapter? = null

    override fun inflateBinding(inflater: LayoutInflater) =
        ActivitySettingsBinding.inflate(inflater)

    override fun initViewModel(): SettingsActivityTabTypeViewModel
    = ViewModelProvider(this)[SettingsActivityTabTypeViewModel::class.java]

    override fun initView() {
        // 绑定左侧 Tab 点击事件
        val menuData = listOf<TabData>(
            TabData(TabType.NETWORK, "网络连接", R.drawable.ic_network),
            TabData(TabType.SOUND, "声音设置", R.drawable.ic_sound),
            TabData(TabType.DISPLAY, "显示与亮度", R.drawable.ic_display),
            TabData(TabType.VEHICLE, "车辆控制", R.drawable.ic_car),
            TabData(TabType.ADAS, "驾驶辅助", R.drawable.ic_adas),
            TabData(TabType.SYSTEM, "系统信息", R.drawable.ic_info),
            TabData(TabType.TEST, "测试", R.drawable.ic_test)
        )
        // 初始化适配器，点击时触发 ViewModel 的状态切换
        tabAdapter = SettingTabAdapter(menuData) { clickTabType ->
            viewModel.switchTab(clickTabType)
        }
        // 配置 RecyclerView
        binding.rvTab.apply {
            adapter = tabAdapter
            setHasFixedSize(true)
        }
    }

    override fun startObserve() {
        // 监听当前选中的Tab信号
        viewModel.currentTab.observe(this) { tabType ->
            // 右侧 Fragment 重绘/切换
            switchFragment(tabType)
            // 左侧 RecyclerView 重绘高亮
            updateTabUI(tabType)
        }

    }

    private fun switchFragment(tabType: TabType) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        // 隐藏当前可见的
        currentFragment?.let { fragmentTransaction.hide(it) }
        // 获取或创建目标
        var targetFragment = fragments[tabType]
        if (targetFragment == null) {
            targetFragment = when (tabType) {
                TabType.NETWORK -> NetworkFragment()
                TabType.SOUND -> SoundFragment()
                TabType.DISPLAY -> DisplayFragment()
                TabType.VEHICLE -> VehicleFragment()
                TabType.ADAS -> AdasFragment()
                TabType.SYSTEM -> SystemFragment()
                TabType.TEST -> TestFragment()
            }
            fragments[tabType] = targetFragment
            fragmentTransaction.add(R.id.fragment_container, targetFragment, tabType.name)
        } else {
            // 缓存命中的话，直接通知底层改VISIBLE状态，实现秒开重绘
            fragmentTransaction.show(targetFragment)
        }
        currentFragment = targetFragment
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun updateTabUI(tabType: TabType) {
        // 丢给适配器去精准刷新那两个改变了状态的 Item，底层的 View 就会走 draw 重新上色
        tabAdapter?.updateSelection(tabType)
    }
}