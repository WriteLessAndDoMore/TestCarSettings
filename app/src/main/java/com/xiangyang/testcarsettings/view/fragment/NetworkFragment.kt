package com.xiangyang.testcarsettings.view.fragment

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.permissionx.guolindev.PermissionX
import com.xiangyang.testcarsettings.R
import com.xiangyang.testcarsettings.adapter.WifiItemAdapter
import com.xiangyang.testcarsettings.base.BaseFragment
import com.xiangyang.testcarsettings.databinding.FragmentNetworkBinding
import com.xiangyang.testcarsettings.util.LogUtils
import com.xiangyang.testcarsettings.viewmodel.fragmentviewmodel.NetworkFragmentViewModel

class NetworkFragment : BaseFragment<FragmentNetworkBinding, NetworkFragmentViewModel>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNetworkBinding.inflate(inflater, container, false)

    private companion object {
        private const val TAG = "NetworkFragment"
    }

    private var mWifiItemAdapter: WifiItemAdapter? = null
    private val locationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION

    // 声明一个全局的属性动画对象
    private var mRefreshAnimator: ObjectAnimator? = null

    override fun initViewModel() = ViewModelProvider(this)[NetworkFragmentViewModel::class.java]

    override fun initView() {
        binding.ivWifiSwitch.setOnClickListener {
            val currentState = viewModel.switchUiState.value
            Log.d(TAG, "initView: $currentState")
            if (currentState == NetworkFragmentViewModel.SwitchUIState.OFF) {
                val isGranted = PermissionX.isGranted(
                    requireContext(),
                    locationPermission
                )
                if (isGranted) {
                    viewModel.toggleWifiSwitch(true)
                } else {
                    // 没有权限，拉起系统弹窗让用户点击“允许”
                    PermissionX.init(this).permissions(locationPermission)
                        .request { allGranted, _, _ ->
                            if (allGranted) {
                                // 用户在弹窗里点了“允许”
                                viewModel.toggleWifiSwitch(true)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "未授予位置权限，无法展示网络列表",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }

            } else if (currentState == NetworkFragmentViewModel.SwitchUIState.ON) {
                viewModel.toggleWifiSwitch(false)
            }
        }
        mWifiItemAdapter = WifiItemAdapter { wifiItemData ->
            LogUtils.d("WIFI 信息：$wifiItemData")
        }
        binding.rvWifi.apply {
            adapter = mWifiItemAdapter
        }
        // 初始化刷新动画
        initRefreshAnimator()
        binding.tvRefresh.setOnClickListener {
            binding.tvRefresh.visibility = View.GONE
            binding.ivRefresh.visibility = View.VISIBLE
            if (mRefreshAnimator?.isRunning == false) {
                mRefreshAnimator?.start()
                viewModel.wifiRefresh()
            }
        }
        // 删除并断开以及链接wifi
        binding.viewConnectedWifi.root.setOnClickListener {
            viewModel.removeCurrentConnectedWifi()
        }
    }

    override fun startObserve() {
        // 开关监听
        viewModel.switchUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                NetworkFragmentViewModel.SwitchUIState.OFF -> {
                    binding.ivWifiSwitch.isSelected = false
                    binding.ivWifiSwitch.isEnabled = true

                    binding.llListTitle.visibility = View.GONE
                    binding.rvWifi.visibility = View.GONE
                    binding.viewConnectedWifi.root.visibility = View.GONE
                    // wifi关闭结束自动刷新
                    viewModel.stopAutoRefresh()
                }

                NetworkFragmentViewModel.SwitchUIState.ON -> {
                    binding.ivWifiSwitch.isSelected = true
                    binding.ivWifiSwitch.isEnabled = true

                    binding.llListTitle.visibility = View.VISIBLE
                    binding.rvWifi.visibility = View.VISIBLE
                    // wifi开启开始自动刷新
                    viewModel.startAutoRefresh()
                }

                NetworkFragmentViewModel.SwitchUIState.PENDING -> {
                    binding.ivWifiSwitch.isEnabled = false
                }
            }
        }
        // 列表监听
        viewModel.wifiList.observe(viewLifecycleOwner) { wifiList ->
            // 停止刷新
            binding.tvRefresh.visibility = View.VISIBLE
            binding.ivRefresh.visibility = View.GONE
            stopRefreshAnimator()
            // ListAdapter 独有的异步数据提交方法，它会在后台自动比对数据的变动
            mWifiItemAdapter?.submitList(wifiList)
        }
        // 已连接wifi
        viewModel.wifiConnectedItem.observe(viewLifecycleOwner) { wifiItemData ->
            if (wifiItemData != null) {
                binding.viewConnectedWifi.root.visibility = View.VISIBLE
                binding.viewConnectedWifi.tvWifiName.text = wifiItemData.ssid
                binding.viewConnectedWifi.ivWifiLevelIcon.setImageLevel(wifiItemData.level)
                if (wifiItemData.isEncrypted) {
                    binding.viewConnectedWifi.ivWifiLockIcon.visibility = View.VISIBLE
                    binding.viewConnectedWifi.ivWifiLockIcon.setImageResource(R.drawable.ic_wifi_lock_white)
                } else {
                    binding.viewConnectedWifi.ivWifiLockIcon.visibility = View.GONE
                }
            } else {
                binding.viewConnectedWifi.root.visibility = View.GONE
            }

        }
    }

    /**
     * 刷新按钮无限旋转动画
     */
    private fun initRefreshAnimator() {
        mRefreshAnimator = ObjectAnimator.ofFloat(binding.ivRefresh, "rotation", 0f, 360f).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE // 无线循环
            repeatMode = ValueAnimator.RESTART// 循环模式：重新开始
            interpolator = LinearInterpolator() // 匀速插值器
        }
    }

    /**
     * 刷新按钮停止
     */
    private fun stopRefreshAnimator() {
        mRefreshAnimator?.let {
            if (it.isRunning) {
                it.cancel()
                // 让刷新按钮保持原理位置
                binding.ivRefresh.rotation = 0f
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            viewModel.stopAutoRefresh()
        } else {
            if (viewModel.switchUiState.value == NetworkFragmentViewModel.SwitchUIState.ON) {
                viewModel.startAutoRefresh()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mRefreshAnimator?.cancel()
        mRefreshAnimator = null
    }

}