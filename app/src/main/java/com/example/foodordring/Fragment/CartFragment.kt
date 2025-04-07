package com.example.foodordring.Fragment // Adjust your package name

import android.content.Intent
import android.os.Bundle
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

// Data class to represent an order item (you might need to adjust this)
data class OrderItem(
    val foodName: String,
    val foodPrice: Double,
    val quantity: Int,
    // Add other relevant details like foodId, image URL, etc.
)

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var foodName: MutableList<String>
    private lateinit var foodPrice: MutableList<String>
    private lateinit var foodDescription: MutableList<String>
    private lateinit var foodImageUrl: MutableList<String>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var quantity: MutableList<Int>
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    private var cartItemsList: MutableList<CartItems> = mutableListOf() // Store CartItems directly
    private var currentDiscount: Double = 0.0 // Keep track of the current discount

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        retriveCartItems()


        binding.checkoutButton.setOnClickListener {
            //get order items details before proceeding to checkout
            getOrderItemsDetails()
        }

        return binding.root
    }

    private fun getOrderItemsDetails() {
        val orderIdReference: DatabaseReference =
            database.reference.child("users").child(userId).child("CartItems")
        val foodName = mutableListOf<String>()
        val foodPrice = mutableListOf<String>()
        val foodDescription = mutableListOf<String>()
        val foodImageUrl = mutableListOf<String>()
        val foodIngredients = mutableListOf<String>()
        val foodQuantities = cartAdapter.getUpdatedItemQuantities()

        orderIdReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (cartItemSnapshot in snapshot.children) {
                    val cartItems = cartItemSnapshot.getValue(CartItems::class.java)

                    //add items details to list
                    cartItems?.foodName?.let { foodName.add(it) }
                    cartItems?.foodPrice?.let { foodPrice.add(it) }
                    cartItems?.foodDescription?.let { foodDescription.add(it) }
                    cartItems?.foodImage?.let { foodImageUrl.add(it) }
                    cartItems?.foodIngredients?.let { foodIngredients.add(it) }
                }
                orderNow(
                    foodName,
                    foodPrice,
                    foodDescription,
                    foodImageUrl,
                    foodIngredients,
                    foodQuantities
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Order making Faild. Please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }


            private fun orderNow(
                foodName: MutableList<String>,
                foodPrice: MutableList<String>,
                foodDescription: MutableList<String>,
                foodImageUrl: MutableList<String>,
                foodIngredients: MutableList<String>,
                foodQuantities: MutableList<Int>
            ) {
                if (isAdded && context != null) {
                    val intent = Intent(requireContext(), PayOutActivity::class.java)
                    intent.putExtra("foodName", ArrayList(foodName) as ArrayList<String>)
                    intent.putExtra("foodPrice", ArrayList(foodPrice) as ArrayList<String>)
                    intent.putExtra(
                        "foodDescription",
                        ArrayList(foodDescription) as ArrayList<String>
                    )
                    intent.putExtra("foodImageUrl", ArrayList(foodImageUrl) as ArrayList<String>)
                    intent.putExtra(
                        "foodIngredients",
                        ArrayList(foodIngredients) as ArrayList<String>
                    )
                    intent.putExtra("foodQuantities", ArrayList(foodQuantities) as ArrayList<Int>)
                    startActivity(intent)
                }

            }
        })
    }


    private fun onQuantityChanged(position: Int, newQuantity: Int) {
        if (position in 0 until cartItemsList.size) {
            cartItemsList[position].quantity = newQuantity
            updateTotalAmount(currentDiscount) // Update total whenever quantity changes.
            // Notify the adapter that the item at the specified position has changed.
            cartAdapter.notifyItemChanged(position)


        }
    }

    private fun retriveCartItems() {
        //Database reference to the Firebase
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""
        val cartItemsReference =
            database.reference.child("users").child(userId).child("CartItems")

        //Initialize empty lists
        foodName = mutableListOf()
        foodPrice = mutableListOf()
        foodDescription = mutableListOf()
        foodImageUrl = mutableListOf()
        foodIngredients = mutableListOf()
        quantity = mutableListOf()

        //Retrieve data from Firebase
        cartItemsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (cartItemSnapshot in snapshot.children) {
                    //Get the data from the cart item snapshot
                    val cartItem = cartItemSnapshot.getValue(CartItems::class.java)
                    //Add the data to the respective lists
                    cartItem?.foodName?.let { foodName.add(it) }
                    cartItem?.foodPrice?.let { foodPrice.add(it) }
                    cartItem?.foodDescription?.let { foodDescription.add(it) }
                    cartItem?.foodImage?.let { foodImageUrl.add(it) }
                    cartItem?.foodIngredients?.let { foodIngredients.add(it) }
                    cartItem?.quantity?.let { quantity.add(it) }
                }
                setAdatper()
            }

            private fun setAdatper() {
                cartAdapter = CartAdapter(
                    requireContext(),
                    foodName,
                    foodPrice,
                    foodImageUrl,
                    foodDescription,
                    foodIngredients,
                    quantity
                )
                binding.cartRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.cartRecyclerView.adapter = cartAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Failed to retrieve cart items",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

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
            currentDiscount = discount //Store the current discount
            updateTotalAmount(discount)
            Toast.makeText(context, "Coupon applied!", Toast.LENGTH_SHORT).show()
        } else {
            currentDiscount = 0.0 //Reset discount if invalid
            updateTotalAmount(0.0)
            Toast.makeText(context, "Invalid coupon code.", Toast.LENGTH_SHORT).show()
        }
    }

    //  ---  COUPON FUNCTIONS (PLACEHOLDERS - IMPLEMENT YOUR LOGIC)  ---

    private fun isValidCoupon(couponCode: String): Boolean {
        // TODO: Implement your logic to validate the coupon code.
        // Example (replace with your actual validation):
        return couponCode == "DISCOUNT10" // Sample: a valid coupon code
    }

    private fun calculateDiscount(couponCode: String): Double {
        // TODO: Implement your logic to calculate the discount amount based on the code.
        // Example (replace with your actual calculation):
        return if (couponCode == "DISCOUNT10") {
            calculateTotal() * 0.10 // 10% discount
        } else {
            0.0
        }
    }

    // --- ---

    private fun setupCheckoutButton() {
        binding.checkoutButton.setOnClickListener {
            val orderItems = getOrderItems()
            if (orderItems.isNotEmpty()) {
                startPayment(orderItems)
            } else {
                Toast.makeText(context, "Your cart is empty.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getOrderItems(): List<OrderItem> {
        return cartItemsList.map { cartItem ->
            OrderItem(
                foodName = cartItem.foodName ?: "", // Provide a default value for foodName
                foodPrice = cartItem.foodDiscountPrice?.toDoubleOrNull() ?: cartItem.foodPrice?.toDoubleOrNull() ?: 0.0,
                quantity = cartItem.quantity ?: 1,
            )
        }
    }

    private fun startPayment(orderItems: List<OrderItem>) {
        // TODO: Implement your payment processing integration here.
        //  Example (replace with your actual implementation - this just passes data to PayOutActivity):
        val intent = Intent(requireContext(), PayOutActivity::class.java)
        //  You might serialize orderItems to a String or Parcelable if needed
        val foodNames = orderItems.map { it.foodName }.toTypedArray()
        val foodPrices = orderItems.map { it.foodPrice }.toTypedArray()
        val quantities = orderItems.map { it.quantity }.toIntArray()
        intent.putExtra("foodNames", foodNames)
        intent.putExtra("foodPrices", foodPrices)
        intent.putExtra("quantities", quantities)
        intent.putExtra("totalAmount", calculateTotal() - currentDiscount) // Pass the total
        startActivity(intent)

        Toast.makeText(context, "Payment started (not implemented fully yet)", Toast.LENGTH_SHORT).show()
    }

    private fun calculateTotal(): Double {
        return cartItemsList.sumOf {
            val price = it.foodDiscountPrice?.toDoubleOrNull() ?: it.foodPrice?.toDoubleOrNull() ?: 0.0
            price * (it.quantity ?: 1).toDouble()
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