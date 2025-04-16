package com.example.foodordering.adapter

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
    private val menuItems: MutableList<MenuItem>
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        Log.d("MenuAdapter", "Binding item at position: $position")  // ADDED LOG
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
            val intent = Intent(itemView.context, DetailsActivity::class.java).apply { // Use itemView.context
                putExtra("MenuItemName", menuItem.foodName)
                putExtra("MenuItemImage", menuItem.foodImage)
                putExtra("MenuItemPrice", menuItem.foodPrice)
                putExtra("MenuItemDescription", menuItem.foodDescription)
                putExtra("MenuItemIngredient", menuItem.foodIngredients)
                putExtra("MenuItemDiscountPrice", menuItem.foodDiscountPrice)
                putExtra("itemId", menuItem.itemId)
            }
            itemView.context.startActivity(intent) // Use itemView.context
        }

        fun bind(position: Int) {
            val menuItem = menuItems[position]
            Log.d("MenuAdapter", "Binding item: ${menuItem.foodName}, isFavorite: ${menuItem.isFavorite}")
            binding.apply {
                menuFoodName.text = menuItem.foodName
                updateFavoriteIconwithdatabase(menuItem.isFavorite)
                priceCurrent.text = "₹ ${menuItem.foodPrice}"
                checkPrice(menuItem.foodPrice, menuItem.foodDiscountPrice)
                foodDescription.text = menuDesc(menuItem.foodDescription)
                Glide.with(menuImage.context).load(Uri.parse(menuItem.foodImage)).into(menuImage) // Use menuImage.context
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
        //Retrive favorite status from database
        private fun updateFavoriteIconwithdatabase(isFavorite: Boolean) {
            val favRef = database.getReference("users/$userId/favorites/${menuItems[adapterPosition].itemId}")
            favRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isFavoriteFromDatabase = snapshot.getValue(Boolean::class.java) ?: false
                    updateFavoriteIcon(isFavoriteFromDatabase)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MenuAdapter", "Error checking favorite status: ${error.message}")
                }
            })
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
            // NEW APPROACH:  Directly set the new value based on the current UI state.
            val newValue = !menuItem.isFavorite  // Invert the current UI state
            favRef.setValue(newValue)
                .addOnSuccessListener {
                    // Update UI and local data on successful write.
                    menuItem.isFavorite = newValue
                    updateFavoriteIcon(menuItem.isFavorite)
                    notifyItemChanged(position)
                    Log.d("MenuAdapter", "Favorite status toggled successfully for ${menuItem.foodName}: $newValue")
                }
                .addOnFailureListener { e ->
                    Log.e("MenuAdapter", "Error toggling favorite: ${e.message}")
                    // Handle error (e.g., show a toast)
                    // Consider reverting UI if needed, as the database update failed.
                }
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

    fun updateData(newMenuItems: MutableList<MenuItem>) {
        Log.d("MenuAdapter", "Updating data with ${newMenuItems.size} items")
        menuItems.clear()
        Log.d("MenuAdapter", "menuItems size after clear: ${menuItems.size}") // NEW LOG
        newMenuItems.forEach { menuItem -> // CHANGED - adding one by one
            Log.d("MenuAdapter", "Adding item: ${menuItem.foodName}") // NEW LOG
            menuItems.add(menuItem)
            Log.d("MenuAdapter", "menuItems size during add: ${menuItems.size}") // NEW LOG
        }
        Log.d("MenuAdapter", "menuItems size after addAll (replaced with loop): ${menuItems.size}") // MODIFIED LOG
        if (menuItems.isNotEmpty()) {
            Log.d("MenuAdapter", "First item in menuItems: ${menuItems[0].foodName}")
        }
        Log.d("MenuAdapter", "menuItems size just before notify: ${menuItems.size}")
        notifyDataSetChanged()
        Log.d("MenuAdapter", "notifyDataSetChanged() called")
    }
}