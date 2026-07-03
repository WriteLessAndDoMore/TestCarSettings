package com.xiangyang.testcarsettings.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xiangyang.testcarsettings.bean.TabData
import com.xiangyang.testcarsettings.databinding.ItemSettingTabBinding
import com.xiangyang.testcarsettings.viewmodel.SettingsActivityTabTypeViewModel.*

class SettingTabAdapter(
    private val mData: List<TabData>,
    private val onItemClick: (TabType) -> Unit
) : RecyclerView.Adapter<SettingTabAdapter.ViewHolder>() {
    // 记录上一次被选中的位置，用于做局部刷新的性能优化
    private var lastSelectedPosition = 0

    class ViewHolder(val binding: ItemSettingTabBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemSettingTabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(
        viewHolder: ViewHolder,
        position: Int
    ) {
        val tabData = mData[position]
        viewHolder.binding.ivTabIcon.setImageResource(tabData.iconRes)
        viewHolder.binding.tvTabTitle.text = tabData.title
        viewHolder.binding.llTabItem.isSelected = tabData.isSelected
        if (tabData.isSelected) {
            lastSelectedPosition = position
        }
        viewHolder.binding.llTabItem.setOnClickListener {
            onItemClick(tabData.tabType)
        }
    }

    override fun getItemCount() = mData.size

    /**
     * 当外部监听到 Tab 切换时，调用此方法精准刷新左侧高亮
     */
    fun updateSelection(targetType: TabType) {
        // 先取消上一个选中的高亮
        if (lastSelectedPosition in mData.indices) {
            mData[lastSelectedPosition].isSelected = false
            notifyItemChanged(lastSelectedPosition)
        }
        // 找到最新要高亮的索引
        val newPosition = mData.indexOfFirst { it.tabType == targetType }
        if (newPosition != -1) {
            mData[newPosition].isSelected = true
            notifyItemChanged(newPosition)
            lastSelectedPosition = newPosition
        }
    }
}