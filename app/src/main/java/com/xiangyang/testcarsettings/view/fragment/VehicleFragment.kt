package com.xiangyang.testcarsettings.view.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.xiangyang.testcarsettings.base.BaseFragment
import com.xiangyang.testcarsettings.databinding.FragmentVehicleBinding
import com.xiangyang.testcarsettings.viewmodel.fragmentviewmodel.VehicleFragmentViewModel

class VehicleFragment : BaseFragment<FragmentVehicleBinding, VehicleFragmentViewModel>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentVehicleBinding.inflate(inflater, container, false)

    override fun initViewModel() = ViewModelProvider(this)[VehicleFragmentViewModel::class.java]

    override fun initView() {

    }

}