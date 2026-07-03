package com.xiangyang.testcarsettings.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB: ViewBinding, VM: ViewModel>: AppCompatActivity() {
    protected lateinit var binding: VB
    protected  lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflateBinding(layoutInflater)
        setContentView(binding.root)

        // 初始化全局配置（如车载沉浸式状态栏等）
        initSystemBar()
        viewModel = initViewModel()
        initView()
        initData()
        startObserve()
    }
    abstract fun inflateBinding(inflater: LayoutInflater): VB
    abstract fun initViewModel(): VM
    abstract fun initView()
    open fun initData() {}
    open fun startObserve() {}
    open fun initSystemBar() {}
}