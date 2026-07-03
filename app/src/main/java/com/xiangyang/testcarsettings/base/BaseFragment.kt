package com.xiangyang.testcarsettings.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB: ViewBinding, VM: ViewModel>: Fragment() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!
    protected lateinit var viewModel: VM
    // 标记是否已经初始化过数据
    private var isDataInitiated = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = initViewModel()
        initView()
        // 如果 Fragment 是一开始就可见的（比如默认拥有的第一个Tab），直接加载数据
        if (!isHidden) {
            internalLazyLoad()
        }
    }

    // 关键：处理 show/hide 切换时的生命周期
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            internalLazyLoad()
        }
    }
    private fun internalLazyLoad() {
        if (!isDataInitiated) {
            initData()
            startObserve()
            isDataInitiated = true
        } else {
            onFragmentResume()
        }
    }

    /** 懒加载数据，只会执行一次 */
    open fun initData() {}
    /** 订阅 LiveData / Flow */
    open fun startObserve() {}
    /** 每次 Fragment 切换到前台可见时触发 */
    open fun onFragmentResume() {

    }

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB?
    abstract fun initViewModel(): VM
    abstract fun initView()
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}