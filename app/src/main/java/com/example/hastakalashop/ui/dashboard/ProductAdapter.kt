package com.example.hastakalashop.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hastakalashop.data.Product
import com.example.hastakalashop.databinding.ItemProductBinding

class ProductAdapter(
    private val onSellClick: (Product) -> Unit,
    private val onLongClick: (Product) -> Unit,
    private val onEditStockClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
        setAnimation(holder.itemView, position)
    }

    private var lastPosition = -1

    private fun setAnimation(viewToAnimate: android.view.View, position: Int) {
        if (position > lastPosition) {
            val animation = android.view.animation.AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.txtProductName.text = product.name
            binding.txtProductPrice.text = "₹${String.format("%.2f", product.price)}"
            binding.txtProductStock.text = "Stock: ${product.stock}"

            if (product.imageRes != 0) {
                binding.imgProduct.setImageResource(product.imageRes)
            } else {
                binding.imgProduct.setImageResource(com.example.hastakalashop.R.drawable.ic_shop_logo)
            }

            binding.btnSell.setOnClickListener {
                onSellClick(product)
            }

            binding.btnEditStock.setOnClickListener {
                onEditStockClick(product)
            }

            binding.root.setOnLongClickListener {
                onLongClick(product)
                true
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem == newItem
    }
}
