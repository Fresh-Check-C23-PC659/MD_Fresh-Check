package com.example.freshcheck.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcheck.databinding.HistoryItemBinding

class ItemHistoryAdapterTest(private val items: List<String>) :
    RecyclerView.Adapter<ItemHistoryAdapterTest.ItemViewHolder>() {

    class ItemViewHolder(binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvItemName = binding.tvHistoryItemName
        val tvItemStore = binding.tvHistoryStoreName
        val tvItemPrice = binding.tvHistoryItemPrice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.apply {
            tvItemName.text = item
            tvItemStore.text = item
            tvItemPrice.text = item
        }
    }

    override fun getItemCount(): Int = items.size

}