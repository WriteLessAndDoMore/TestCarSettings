package com.xiangyang.testcarsettings.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xiangyang.testcarsettings.R
import com.xiangyang.testcarsettings.bean.WifiItemData
import com.xiangyang.testcarsettings.databinding.ItemNearbyWifiInfoBinding

class WifiItemAdapter(private val onItemClick: (WifiItemData) -> Unit) :
    ListAdapter<WifiItemData, WifiItemAdapter.WifiItemViewHolder>(WifiDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WifiItemViewHolder {
        val binding =
            ItemNearbyWifiInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WifiItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        viewHolder: WifiItemViewHolder,
        position: Int
    ) {
        val wifiItem = getItem(position)
        viewHolder.bind(wifiItem, onItemClick)
    }

    class WifiItemViewHolder(val binding: ItemNearbyWifiInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(wifiItem: WifiItemData, onItemClick: (WifiItemData) -> Unit) {
            binding.tvWifiName.text = wifiItem.ssid
            binding.ivWifiLevelIcon.setImageLevel(wifiItem.level)

            if (wifiItem.isEncrypted) {
                binding.ivWifiLockIcon.visibility = View.VISIBLE
                binding.ivWifiLockIcon.setImageResource(R.drawable.ic_wifi_lock_black)
            } else {
                binding.ivWifiLockIcon.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                onItemClick(wifiItem)
            }
        }
    }
    // 差分器：用来智能比对新旧两条热点列表数据，决定谁刷新，避免整个表刷新闪烁
    class WifiDiffCallback: DiffUtil.ItemCallback<WifiItemData>() {
        override fun areItemsTheSame(
            oldItem: WifiItemData,
            newItem: WifiItemData
        ): Boolean {
            // SSID 相同说明是同一个热点
            return oldItem.ssid == newItem.ssid
        }

        override fun areContentsTheSame(
            oldItem: WifiItemData,
            newItem: WifiItemData
        ): Boolean {
            return oldItem.level == newItem.level && oldItem.capabilities == newItem.capabilities
        }

    }

}