package com.example.foodordring.adaptar

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodordring.databinding.CartItemBinding
import com.example.foodordring.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<CartItems>,
    private val onQuantityChanged: (Int, Int) -> Unit,
    private val onRemoveItem: (Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var cartItemsReference: DatabaseReference

    init {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        cartItemsReference = database.reference.child("users").child(userId).child("CartItems")
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartItems.size


    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val item = cartItems[position]
            binding.apply {
                CartFoodName.text = item.foodName
                // Use discount price if available, otherwise use regular price
                val price = item.foodDiscountPrice?.toDoubleOrNull() ?: item.foodPrice?.toDoubleOrNull() ?: 0.0
                CartItemPrice.text = "₹ %.2f".format(price)
                quantity.text = item.quantity.toString()
                // Set image into imageview
                item.foodImage?.let {
                    Glide.with(context).load(Uri.parse(it)).into(cartImage)
                }

                miniusButton.setOnClickListener {
                    val currentQuantity = item.quantity ?: 1
                    if (currentQuantity > 1) {
                        onQuantityChanged(position, currentQuantity - 1)
                    }
                }
                plusButton.setOnClickListener {
                    val currentQuantity = item.quantity ?: 1
                    if (currentQuantity < 10) {
                        onQuantityChanged(position, currentQuantity + 1)
                    }
                }
                deleteButton.setOnClickListener {
                    onRemoveItem(position)
                }
            }
        }
    }
}