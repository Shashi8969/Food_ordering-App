package com.example.foodordring.adaptar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodordring.R
import com.example.foodordring.databinding.BuyAgainItemBinding
import com.example.foodordring.model.RecentBuyModel

class BuyAgainAdapter(private val recentBuyItems: List<RecentBuyModel>) :
    RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>() {

    inner class BuyAgainViewHolder(private val binding: BuyAgainItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecentBuyModel) {
            binding.buyAgainFoodName.text = item.name
            binding.buyAgainFoodPrice.text = item.price?.toString() ?: "N/A"
            // Load image from URL using Glide
            Glide.with(binding.buyAgainFoodImage.context) // Use the ImageView's context
                .load(item.image)
                .placeholder(R.drawable.menu1) // Optional: placeholder image
                .error(R.drawable.menu2) // Optional: image if loading fails
                .into(binding.buyAgainFoodImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainViewHolder {
        val binding = BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BuyAgainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BuyAgainViewHolder, position: Int) {
        holder.bind(recentBuyItems[position])
    }

    override fun getItemCount(): Int = recentBuyItems.size
}