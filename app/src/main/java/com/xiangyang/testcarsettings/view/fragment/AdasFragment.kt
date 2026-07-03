package com.xiangyang.testcarsettings.view.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.xiangyang.testcarsettings.base.BaseFragment
import com.xiangyang.testcarsettings.databinding.FragmentAdasBinding
import com.xiangyang.testcarsettings.viewmodel.fragmentviewmodel.AdasFragmentViewModel

class AdasFragment : BaseFragment<FragmentAdasBinding, AdasFragmentViewModel>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAdasBinding.inflate(inflater, container, false)

    override fun initViewModel() = ViewModelProvider(this)[AdasFragmentViewModel::class.java]

    override fun initView() {

    }
}