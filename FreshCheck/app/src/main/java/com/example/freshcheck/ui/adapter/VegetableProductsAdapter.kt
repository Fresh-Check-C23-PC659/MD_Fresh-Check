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
import java.text.DecimalFormat
import java.util.Locale

class VegetableProductsAdapter :
    RecyclerView.Adapter<VegetableProductsAdapter.VegetableProductsViewHolder>() {

    inner class VegetableProductsViewHolder(private val binding: ShopItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(ivItem)
                tvItemName.text = product.name
                tvPrice.text = formatPrice(product.price)
            }
        }
    }

    private fun formatPrice(price: Float): String {
        val format = DecimalFormat.getCurrencyInstance(Locale("id", "ID")) as DecimalFormat
        val symbols = format.decimalFormatSymbols
        symbols.currencySymbol = "Rp."
        format.decimalFormatSymbols = symbols
        return format.format(price.toDouble())
    }


    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VegetableProductsViewHolder {
        return VegetableProductsViewHolder(
            ShopItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: VegetableProductsViewHolder,
        position: Int,
    ) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}