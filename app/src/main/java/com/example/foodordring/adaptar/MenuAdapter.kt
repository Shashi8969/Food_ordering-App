package com.example.foodordering.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodordring.DetailsActivity
import com.example.foodordring.databinding.MenuItemBinding
import com.example.foodordring.model.MenuItem

class MenuAdapter(
    private val menuItems: List<MenuItem>,
    private val context: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)

    }

    override fun getItemCount(): Int = menuItems.size

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
            val menuItem = menuItems[position]

            val intent = Intent(context, DetailsActivity::class.java).apply {
                putExtra("MenuItemName", menuItem.foodName)
                putExtra("MenuItemImage", menuItem.foodImage)
                putExtra("MenuItemPrice", menuItem.foodPrice)
                putExtra("MenuItemDescription", menuItem.foodDescription)
                putExtra("MenuItemIngredient", menuItem.foodIngredients)
            }
            context.startActivity(intent)
        }

        fun bind(position: Int) {
            val menuItem = menuItems[position]
            binding.apply {
                menuFoodName.text = menuItem.foodName
                priceCurrent.text = buildString {
                    append("₹ ")
                    append(menuItem.foodPrice)
                }
                priceCurrent.paintFlags = android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                foodDescription.text = menuDesc(menuItem.foodDescription)
                val uri: Uri = Uri.parse(menuItem.foodImage)
                Glide.with(context).load(uri).into(menuImage)
                ratingText.text = menuItem.foodRating.toString()
                priceDiscounted.text = "₹ ${menuItem.foodDiscountPrice}"
                favoriteButton.setOnClickListener {
                    menuItem.isFavorite = !menuItem.isFavorite
                    if (menuItem.isFavorite) {
                        favoriteButton.setImageResource(com.example.foodordring.R.drawable.faviourate_filled_heart)
                    }
                    else {
                        favoriteButton.setImageResource(com.example.foodordring.R.drawable.faviourate_empity)
                    }
                }
            }
        }

        private fun menuDesc(description: String?): String {
            if (description != null) {
                if (description.length > 24) {
                    return buildString {
                        append(description.substring(0, 20))
                        append("...")
                    }
                }
                return description
            }
            return ""
        }
    }
}