package com.example.foodordering.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodordring.DetailsActivity
import com.example.foodordring.R
import com.example.foodordring.databinding.MenuItemBinding
import com.example.foodordring.model.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuAdapter(
    private val menuItems: MutableList<MenuItem>,
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

        private val database = FirebaseDatabase.getInstance()
        private val userId = FirebaseAuth.getInstance().currentUser?.uid

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
                putExtra("MenuItemDiscountPrice", menuItem.foodDiscountPrice)
                putExtra("itemId", menuItem.itemId)
            }
            context.startActivity(intent)
        }

        fun bind(position: Int) {
            val menuItem = menuItems[position]
            Log.d("MenuAdapter", "Binding item: ${menuItem.foodName}, isFavorite: ${menuItem.isFavorite}")
            binding.apply {
                menuFoodName.text = menuItem.foodName
                updateFavoriteIcon(menuItem.isFavorite)
                priceCurrent.text = "₹ ${menuItem.foodPrice}"
                checkPrice(menuItem.foodPrice, menuItem.foodDiscountPrice)
                foodDescription.text = menuDesc(menuItem.foodDescription)
                Glide.with(context).load(Uri.parse(menuItem.foodImage)).into(menuImage)
                ratingText.text = menuItem.foodRating?.toString() ?: "0.0"
                priceDiscounted.text = "₹ ${menuItem.foodDiscountPrice}"

                favoriteButton.setOnClickListener {
                    val currentPosition = adapterPosition
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        val currentMenuItem = menuItems[currentPosition]
                        toggleFavorite(currentMenuItem, currentPosition)
                    }
                }
            }
        }

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            val icon = if (isFavorite) R.drawable.faviourate_filled_heart else R.drawable.faviourate_empity
            binding.favoriteButton.setImageResource(icon)
        }

        private fun toggleFavorite(menuItem: MenuItem, position: Int) {
            val userId = this.userId ?: run {
                Log.w("MenuAdapter", "User not authenticated. Cannot update favorite.")
                // Handle unauthenticated state appropriately (e.g., show a message)
                return
            }
            val favRef = database.getReference("users/$userId/favorites/${menuItem.itemId}")
            val menuRef = database.getReference("Menu/${menuItem.itemId}") // Assuming your base menu path is "Menu"

            favRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isCurrentlyFavorite = snapshot.exists()
                    favRef.setValue(!isCurrentlyFavorite)  // Toggle favorite
                        .addOnSuccessListener {
                            menuItem.isFavorite = !isCurrentlyFavorite
                            updateFavoriteIcon(menuItem.isFavorite)
                            notifyItemChanged(position)
                        }
                        .addOnFailureListener { e ->
                            Log.e("MenuAdapter", "Error toggling favorite: ${e.message}")
                            // Handle error (e.g., show a toast)
                            // Consider reverting UI if needed.
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MenuAdapter", "Error checking existing favorite: ${error.message}")
                    // Handle error (e.g., show a toast)
                }
            })
        }

        private fun checkPrice(current: String?, discounted: String?) {
            binding.apply {
                priceDiscounted.visibility =
                    if (current != null && discounted != null && current.toDouble() > discounted.toDouble())
                        ViewGroup.VISIBLE
                    else
                        ViewGroup.GONE
                priceCurrent.paintFlags =
                    if (priceDiscounted.visibility == ViewGroup.VISIBLE)
                        android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                    else
                        0
                priceCurrent.visibility = if (current != null) ViewGroup.VISIBLE else ViewGroup.GONE
                priceDiscounted.visibility = if (discounted != null && priceDiscounted.visibility == ViewGroup.VISIBLE) ViewGroup.VISIBLE else ViewGroup.GONE
            }
        }

        private fun menuDesc(description: String?): String =
            description?.takeIf { it.length <= 24 } ?: "${description?.substring(0, 20)}..."
    }

    fun updateData(newItems: List<MenuItem>) {
        menuItems.clear()
        menuItems.addAll(newItems)
        notifyDataSetChanged()
    }
}