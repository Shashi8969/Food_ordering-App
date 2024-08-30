package com.example.foodordring.adaptar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodordring.databinding.PopularItemBinding

class PopularAdapter(private val items:List<String>,private val image:List<Int>,private val price:List<String>) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): PopularAdapter.PopularViewHolder {
        return PopularViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
    override fun onBindViewHolder(holder: PopularAdapter.PopularViewHolder, position: Int) {
        val item = items[position]
        val images = image[position]
        val price = price[position]
        holder.bind(item,images,price)
    }
    override fun getItemCount(): Int {
        return items.size
    }
    class PopularViewHolder (private val binding: PopularItemBinding) : RecyclerView.ViewHolder(binding.root)   {
        val images = binding.imageView5
        fun bind(item: String, images: Int, price: String) {
            binding.foodNamePopular.text = item
            binding.imageView5.setImageResource(images)
            binding.pricePopular.text = price
        }
    }
}
