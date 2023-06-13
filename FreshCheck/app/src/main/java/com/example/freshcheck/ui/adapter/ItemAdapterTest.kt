package com.example.freshcheck.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcheck.databinding.ShopItemBinding

class ItemAdapterTest(private val items: List<String>) :
    RecyclerView.Adapter<ItemAdapterTest.ItemViewHolder>() {

    class ItemViewHolder(binding: ShopItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvItemName = binding.tvItemName
        val tvItemStore = binding.tvStoreName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ShopItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.apply {
            tvItemName.text = item
            tvItemStore.text = item
        }
    }

    override fun getItemCount(): Int = items.size
}
