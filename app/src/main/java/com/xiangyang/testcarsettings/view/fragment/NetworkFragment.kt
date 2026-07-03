package com.xiangyang.testcarsettings.view.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.xiangyang.testcarsettings.base.BaseFragment
import com.xiangyang.testcarsettings.databinding.FragmentNetworkBinding
import com.xiangyang.testcarsettings.viewmodel.fragmentviewmodel.NetworkFragmentViewModel

class NetworkFragment : BaseFragment<FragmentNetworkBinding, NetworkFragmentViewModel>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNetworkBinding.inflate(inflater, container, false)

    override fun initViewModel() = ViewModelProvider(this)[NetworkFragmentViewModel::class.java]

    override fun initView() {

    }

}