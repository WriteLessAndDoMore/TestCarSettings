package com.xiangyang.testcarsettings.view.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.xiangyang.testcarsettings.base.BaseFragment
import com.xiangyang.testcarsettings.databinding.FragmentSoundBinding
import com.xiangyang.testcarsettings.viewmodel.fragmentviewmodel.SoundFragmentViewModel

class SoundFragment : BaseFragment<FragmentSoundBinding, SoundFragmentViewModel>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSoundBinding.inflate(inflater, container, false)

    override fun initViewModel() = ViewModelProvider(this)[SoundFragmentViewModel::class.java]

    override fun initView() {

    }

}