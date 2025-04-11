package com.example.foodordring.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodordring.PayOutActivity
import com.example.foodordring.adaptar.CartAdapter
import com.example.foodordring.databinding.FragmentCartBinding
import com.example.foodordring.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class OrderItem(
    val foodName: String,
    val foodPrice: Double,
    val quantity: Int,
    // Add other relevant details like foodId, image URL, etc., if needed.
)

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String
    private lateinit var cartItemsReference: DatabaseReference

    private var cartItemsList: MutableList<CartItems> = mutableListOf()
    private var currentDiscount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""
        cartItemsReference = database.reference.child("users").child(userId).child("CartItems")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        setupRecyclerView()
        retrieveCartItems()
        setupCheckoutButton()
        setupCouponButton()

        return binding.root
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            requireContext(),
            cartItemsList,
            this::onQuantityChanged,
            this::onRemoveItem
        )
        binding.cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }

    private fun retrieveCartItems() {
        cartItemsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartItemsList.clear()
                for (cartItemSnapshot in snapshot.children) {
                    val cartItem = cartItemSnapshot.getValue(CartItems::class.java)
                    cartItem?.let {
                        // Store the Firebase key as the itemId
                        it.itemId = cartItemSnapshot.key
                        cartItemsList.add(it)
                    }
                }
                cartAdapter.notifyDataSetChanged()
                updateTotalAmount(currentDiscount)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Failed to retrieve cart items: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("CartFragment", "Error retrieving cart items: ${error.message}")
            }
        })
    }

    private fun onQuantityChanged(position: Int, newQuantity: Int) {
        if (position in 0 until cartItemsList.size) {
            cartItemsList[position].quantity = newQuantity
            updateCartItemInDatabase(position)
        }
    }

    private fun updateCartItemInDatabase(position: Int) {
        if (position in 0 until cartItemsList.size) {
            val cartItem = cartItemsList[position]
            val cartItemId = cartItem.itemId // Use itemId here
            if (cartItemId != null) {
                cartItemsReference.child(cartItemId).setValue(cartItem)
                    .addOnSuccessListener {
                        updateTotalAmount(currentDiscount)
                        cartAdapter.notifyItemChanged(position)
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Failed to update quantity.",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("CartFragment", "Error updating quantity: ${it.message}")
                    }
            } else {
                Log.e("CartFragment", "Cart item at position $position has no ID.")
            }
        }
    }

    private fun onRemoveItem(position: Int) {
        if (position in 0 until cartItemsList.size) {
            val cartItem = cartItemsList[position]
            val cartItemId = cartItem.itemId // Use itemId here
            if (cartItemId != null) {
                cartItemsReference.child(cartItemId).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Item removed from cart", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to remove item.", Toast.LENGTH_SHORT).show()
                        Log.e("CartFragment", "Error removing item: ${it.message}")
                    }
            } else {
                Log.e("CartFragment", "Cart item at position $position has no ID.")
            }
        }
    }

    private fun getOrderItemsDetails() {
        if(cartItemsList.isEmpty()){
            Toast.makeText(context, "Your cart is empty.", Toast.LENGTH_SHORT).show()
            return
        }

        val orderItems = cartItemsList.map { item ->
            OrderItem(
                foodName = item.foodName ?: "",
                foodPrice = (item.foodDiscountPrice?.toDoubleOrNull() ?: item.foodPrice?.toDoubleOrNull()) ?: 0.0,
                quantity = item.quantity ?: 1
            )
        }

        val discountedTotalPrice = calculateTotal() - currentDiscount
        orderNow(orderItems, discountedTotalPrice)
    }

    private fun orderNow(orderItems: List<OrderItem>, discountedTotalPrice: Double) {
        if (isAdded && context != null) {
            val intent = Intent(requireContext(), PayOutActivity::class.java).apply {
                putExtra("orderItems", ArrayList(orderItems)) // Consider using Parcelable for OrderItem
                putExtra("discountedTotalPrice", discountedTotalPrice)
            }
            startActivity(intent)
        }
    }

    private fun setupCouponButton() {
        binding.applyCouponButton.setOnClickListener {
            val couponCode = binding.couponCodeEditText.text.toString()
            applyCoupon(couponCode)
        }
    }

    private fun applyCoupon(couponCode: String) {
        if (isValidCoupon(couponCode)) {
            val discount = calculateDiscount(couponCode)
            currentDiscount = discount
            updateTotalAmount(discount)
            Toast.makeText(context, "Coupon applied!", Toast.LENGTH_SHORT).show()
        } else {
            currentDiscount = 0.0
            updateTotalAmount(0.0)
            Toast.makeText(context, "Invalid coupon code.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidCoupon(couponCode: String): Boolean {
        return couponCode == "DISCOUNT10" // Replace with your logic
    }

    private fun calculateDiscount(couponCode: String): Double {
        return if (couponCode == "DISCOUNT10") {
            calculateTotal() * 0.10
        } else {
            0.0
        }
    }

    private fun setupCheckoutButton() {
        binding.checkoutButton.setOnClickListener {
            getOrderItemsDetails()
        }
    }

    private fun calculateTotal(): Double {
        return cartItemsList.sumOf {
            val price = (it.foodDiscountPrice?.toDoubleOrNull() ?: it.foodPrice?.toDoubleOrNull()) ?: 0.0
            price * (it.quantity ?: 1)
        }
    }

    private fun updateTotalAmount(discount: Double = 0.0) {
        val total = calculateTotal() - discount
        binding.totalAmountTextView.text = "Total: ₹%.2f".format(total)
    }

    companion object {
        @JvmStatic
        fun newInstance() = CartFragment()
    }
}