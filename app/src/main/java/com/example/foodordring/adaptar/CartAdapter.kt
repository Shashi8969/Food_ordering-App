package com.example.foodordring.adaptar

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodordring.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<String>,
    private val foodItemPrice: MutableList<String>,
    private val foodImage: MutableList<String>,
    private var foodDescription: MutableList<String>,
    private var foodIngredients: MutableList<String>,
    private var quantity: MutableList<Int>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        val cartItemNumber: Int = cartItems.size

        itemquntities = IntArray(cartItemNumber) { 1 }
        cartItemsReference = database.reference.child("users").child(userId).child("CartItems")

    }

    companion object {
        private var itemquntities: IntArray = intArrayOf()
        private lateinit var cartItemsReference: DatabaseReference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartItems.size

    //get updated quantities
    fun getUpdatedItemQuantities(): MutableList<Int> {
        val itemQuantities = mutableListOf<Int>()
        itemQuantities.addAll(itemquntities.toList())
        return itemQuantities
    }

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(position: Int) {

            binding.apply {
                CartFoodName.text = cartItems[position]
                CartItemPrice.text = buildString {
        append("₹ ")
        append(foodItemPrice[position])
    }
                quantity.text = itemquntities[position].toString()
                //set image into imageview
                val uri: Uri = Uri.parse(foodImage[position])
                Glide.with(context).load(uri).into(cartImage)

                miniusButton.setOnClickListener {
                    if (itemquntities[position] > 1) {
                        itemquntities[position]--
                        quantity.text = itemquntities[position].toString()
                    }
                }
                plusButton.setOnClickListener {
                    if (itemquntities[position] < 10) {
                        itemquntities[position]++
                        quantity.text = itemquntities[position].toString()
                    }
                }
                deleteButton.setOnClickListener {
                    getUniqueKey(position) { uniqueKey ->
                        if (uniqueKey != null) {
                            removeItem(position, uniqueKey)
                        }
                    }
                }
            }
        }



        private fun removeItem(position: Int, uniqueKey: String) {
            cartItemsReference.child(uniqueKey).removeValue()
                .addOnSuccessListener {
                    cartItems.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, cartItems.size)
                }
                .addOnFailureListener {
                    //  Consider showing an error message to the user
                    Log.e("CartAdapter", "Error removing item: ${it.message}")
                }
        }

        private fun getUniqueKey(positionRetrive: Int, onComplete: (String?) -> Unit) {
            cartItemsReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var uniqueKey: String? = null
                    //Loop for snapshot children
                    snapshot.children.forEachIndexed { index, childSnapshot ->
                        if (index == positionRetrive) {
                            uniqueKey = childSnapshot.key
                            return@forEachIndexed
                        }
                    }
                    onComplete(uniqueKey)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })
        }
    }
}