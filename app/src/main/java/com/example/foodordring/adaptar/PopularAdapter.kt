package com.example.foodordring.adaptar

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodordring.DetailsActivity
import com.example.foodordring.databinding.PopularItemBinding

class PopularAdapter(private val items:List<String>,private val image:List<Int>,private val price:List<String>,private val requrireContext: Context) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): PopularAdapter.PopularViewHolder {
        return PopularViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
    override fun onBindViewHolder(holder: PopularAdapter.PopularViewHolder, position: Int) {
        val item = items[position]
        val images = image[position]
        val price = price[position]
        holder.bind(item,images,price)

        holder.itemView.setOnClickListener {
            // Handle item click here
            val intent = Intent(requrireContext, DetailsActivity::class.java)
            intent.putExtra("MenuItemName", item)
            intent.putExtra("MenuItemImage", images)
            requrireContext.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return items.size
    }
    class PopularViewHolder (private val binding: PopularItemBinding) : RecyclerView.ViewHolder(binding.root)   {
        val images = binding.popularImage
        fun bind(item: String, images: Int, price: String) {
            binding.popularFoodName.text = item
            binding.popularImage.setImageResource(images)
            binding.pricePopular.text = price
        }

    }
}
