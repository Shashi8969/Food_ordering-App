package com.example.foodordring.adaptar

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodordring.DetailsActivity
import com.example.foodordring.databinding.MenuItemBinding

class MenuAdapter(private val menuItem: MutableList<String>, private val menuItemPrice: MutableList<String>, private val menuItemImage: MutableList<Int>, private val requrireContext: Context): RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private val itemClickListener :OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount(): Int = menuItem.size

    inner class MenuViewHolder(private val binding: MenuItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }
                //setOnClickListener to open food details activity
                val intent = Intent(requrireContext, DetailsActivity::class.java)
                intent.putExtra("MenuItemName", menuItem.get(position))
                intent.putExtra("MenuItemImage", menuItemImage.get(position))
                requrireContext.startActivity(intent)
            }
        }

        fun bind(position: Int) {
            binding.apply {
                menuImage.setImageResource(menuItemImage[position])
                menuFoodName.text = menuItem[position]
                priceMenu.text = menuItemPrice[position]


            }

        }

    }
    interface OnClickListener{
        fun onItemClick(position: Int)
    }

}

