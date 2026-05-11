package com.example.hastakalashop.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hastakalashop.data.Sale
import com.example.hastakalashop.databinding.ItemSaleBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter : ListAdapter<Sale, HistoryAdapter.HistoryViewHolder>(SaleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemSaleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val sale = getItem(position)
        holder.bind(sale)
    }

    inner class HistoryViewHolder(private val binding: ItemSaleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

        fun bind(sale: Sale) {
            binding.txtSaleProductName.text = sale.productName
            binding.txtSaleAmount.text = "₹${String.format("%.2f", sale.totalPrice)}"
            binding.txtSaleQuantity.text = "Qty: ${sale.quantity}"
            binding.txtSaleDate.text = dateFormat.format(Date(sale.timestamp))
        }
    }

    class SaleDiffCallback : DiffUtil.ItemCallback<Sale>() {
        override fun areItemsTheSame(oldItem: Sale, newItem: Sale): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Sale, newItem: Sale): Boolean = oldItem == newItem
    }
}
