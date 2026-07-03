package com.xiangyang.testcarsettings.view.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.xiangyang.testcarsettings.base.BaseFragment
import com.xiangyang.testcarsettings.databinding.FragmentSystemBinding
import com.xiangyang.testcarsettings.viewmodel.fragmentviewmodel.SystemFragmentViewModel


class SystemFragment : BaseFragment<FragmentSystemBinding, SystemFragmentViewModel>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSystemBinding.inflate(inflater, container, false)

    override fun initViewModel() = ViewModelProvider(this)[SystemFragmentViewModel::class.java]

    override fun initView() {

    }

}