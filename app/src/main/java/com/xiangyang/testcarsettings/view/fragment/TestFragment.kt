package com.xiangyang.testcarsettings.view.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.xiangyang.testcarsettings.base.BaseFragment
import com.xiangyang.testcarsettings.databinding.FragmentTestBinding
import com.xiangyang.testcarsettings.viewmodel.fragmentviewmodel.TestFragmentViewModel

class TestFragment : BaseFragment<FragmentTestBinding, TestFragmentViewModel>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTestBinding.inflate(inflater, container, false)

    override fun initViewModel() = ViewModelProvider(this)[TestFragmentViewModel::class.java]

    override fun initView() {

    }

}