package com.example.foodordring.adaptar

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodordring.DetailsActivity
import com.example.foodordring.databinding.MenuItemBinding
import com.example.foodordring.model.MenuItem

class MenuAdapter(
    private val menuItem: List<MenuItem>,
    private val requrireContext: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private val itemClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItem.size

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    openDetailsActivity(position)
                }
            }
        }

        private fun openDetailsActivity(position: Int) {
            val menuItem = menuItem[position]

            //A intent to open details activity and pass data
            val intent = Intent(requrireContext, DetailsActivity::class.java).apply {
                putExtra("MenuItemName", menuItem.foodName)
                putExtra("MenuItemImage", menuItem.foodImage)
                putExtra("MenuItemPrice", menuItem.foodPrice)
                putExtra("MenuItemDescription", menuItem.foodDescription)
                putExtra("MenuItemIngredient", menuItem.foodIngredients)
            }
            //start the activity
            requrireContext.startActivity(intent)

        }
        //set data into recyclerview items name,price,image
        fun bind(position: Int) {
            val menuItem = menuItem[position]
            binding.apply {
                menuFoodName.text = menuItem.foodName
                priceMenu.text = menuItem.foodPrice
                val uri:Uri = Uri.parse(menuItem.foodImage)
                Glide.with(requrireContext).load(uri).into(menuImage)

            }

        }

    }

    interface OnClickListener {
        fun onItemClick(position: Int)
    }

}

