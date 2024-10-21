package com.example.foodordring.adaptar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodordring.databinding.MenuItemBinding

class MenuAdapter(private val menuItem: MutableList<String>, private val menuItemPrice: MutableList<String>, private val menuItemImage: MutableList<Int>): RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount(): Int = menuItem.size

    inner class MenuViewHolder(private val binding: MenuItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                menuImage.setImageResource(menuItemImage[position])
                menuFoodName.text = menuItem[position]
                priceMenu.text = menuItemPrice[position]
            }

        }

    }
}