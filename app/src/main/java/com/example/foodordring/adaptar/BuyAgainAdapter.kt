package com.example.foodordring.adaptar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodordring.databinding.BuyAgainItemBinding

class BuyAgainAdapter(private val buyAgainFoodName: List<String>,private val buyAgainFoodPrice: List<String>,private val buyAgainFoodImage: List<Int>) :RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>() {
    class BuyAgainViewHolder(private val binding: BuyAgainItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(foodName: String, foodPrice: String, foodImage: Int)  {
            binding.buyAgainFoodName.text = foodName
            binding.buyAgainFoodPrice.text = foodPrice
            binding.buyAgainFoodImage.setImageResource(foodImage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): BuyAgainViewHolder {
        var binding = BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BuyAgainViewHolder(binding)

    }

    override fun onBindViewHolder(holder: BuyAgainViewHolder, position: Int) {
        holder.bind(buyAgainFoodName[position],buyAgainFoodPrice[position],buyAgainFoodImage[position])
    }

    override fun getItemCount(): Int = buyAgainFoodName.size

}