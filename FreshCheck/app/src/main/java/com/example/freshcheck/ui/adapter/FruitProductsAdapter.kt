package com.example.freshcheck.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshcheck.data.Product
import com.example.freshcheck.databinding.ShopItemBinding

class FruitProductsAdapter: RecyclerView.Adapter<FruitProductsAdapter.FruitProductsViewHolder>() {

    inner class FruitProductsViewHolder(private val binding: ShopItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(ivItem)
                tvItemName.text = product.name
                tvPrice.text = product.price.toString()
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FruitProductsViewHolder {
        return FruitProductsViewHolder(
            ShopItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: FruitProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }
}