package com.xiangyang.testcarsettings.view.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.xiangyang.testcarsettings.base.BaseFragment
import com.xiangyang.testcarsettings.databinding.FragmentDisplayBinding
import com.xiangyang.testcarsettings.viewmodel.fragmentviewmodel.DisplayFragmentViewModel


class DisplayFragment : BaseFragment<FragmentDisplayBinding, DisplayFragmentViewModel>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDisplayBinding.inflate(inflater, container, false)

    override fun initViewModel() = ViewModelProvider(this)[DisplayFragmentViewModel::class.java]

    override fun initView() {

    }

}