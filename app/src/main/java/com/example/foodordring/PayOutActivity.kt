package com.example.foodordring

import OrderItem
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodordring.StringHandling.formatAddressEnhanced
import com.example.foodordring.StringHandling.formatName
import com.example.foodordring.databinding.ActivityPayOutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class PayOutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayOutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String
    private var totalAmount: Double = 0.0
    private var orderItems: ArrayList<OrderItem> = ArrayList() // Initialize orderItems

    private lateinit var databaseReference: DatabaseReference
    private lateinit var orderId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialize Firebase Authentication
        databaseReference = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()

        // Retrieve data from intent
        orderItems = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("orderItems", OrderItem::class.java) ?: ArrayList()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("orderItems") ?: ArrayList()
        }
        totalAmount = intent.getDoubleExtra("discountedTotalPrice", 0.0)
        Log.d("PayOutActivity", "Received orderItems: $orderItems")
        Log.d("PayOutActivity", "Received discountedTotalPrice: $totalAmount")

        //Set user data
        setUserData()

        //Calculate total amount and show it in the UI
        binding.totalAmountEditText.text = android.text.Editable.Factory.getInstance().newEditable("%.2f".format(totalAmount))
        binding.totalAmountEditText.isEnabled = false

        val bottomSheetFragment = CongratsBottomSheet()
        binding.placeOrderButton.setOnClickListener {
            name = binding.nameEditText.text.toString()
            address = binding.addressEditText.text.toString()
            phone = binding.phoneEditText.text.toString()
            name.formatName()
            address.formatAddressEnhanced()

            if(name.isEmpty()){
                binding.nameEditText.error = "Please enter your name"
                binding.nameEditText.requestFocus()
            }
            if(address.isEmpty()){
                binding.addressEditText.error = "Please enter your address"
                binding.addressEditText.requestFocus()
            }
            if(phone.isEmpty()){
                binding.phoneEditText.error = "Please enter your phone number"
                binding.phoneEditText.requestFocus()
            }
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(phone).matches()){
                binding.phoneEditText.error = "Invalid Indian Phone Number"
                binding.phoneEditText.requestFocus()
            }
            if(phone.matches("^\\+91[6-9][0-9]{9}$".toRegex()) || phone.matches("^[6-9][0-9]{9}$".toRegex())) {
                binding.phoneEditText.error = "Invalid Indian Phone Number"
                binding.phoneEditText.requestFocus()
            }
            if(name.isNotEmpty() && address.isNotEmpty() && phone.isNotEmpty()) {
                Toast.makeText(this, "Placing order...", Toast.LENGTH_SHORT).show()
                createOrderId()
                bottomSheetFragment.show(
                    supportFragmentManager,
                    "Test"
                )
            }
            else {
                Log.w("PayOutActivity", "Please fill in all fields.")
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                // You can optionally display a Toast here to inform the user.
            }
        }

        binding.toolbar.setOnClickListener {
            finish()
        }
    }


    private fun setUserData() {
        val user = auth.currentUser
        if (user != null) {
            userId = user.uid
            val userReference = databaseReference.child("users").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userName = snapshot.child("name").getValue(String::class.java) ?: ""
                        val userAddress =
                            snapshot.child("address").getValue(String::class.java) ?: ""
                        val userPhone = snapshot.child("phone").getValue(String::class.java) ?: ""


                        binding.apply {
                            nameEditText.setText(userName)
                            addressEditText.setText(userAddress)
                            phoneEditText.setText(userPhone)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("PayOutActivity", "Database error: ${error.message}")
                    // Handle the error appropriately, e.g., show a user-friendly message
                }
            })
        } else {
            Log.w("PayOutActivity", "User not logged in.")
            // Handle the case where the user is not logged in, e.g., redirect to login
        }

    }

    private fun createOrderId() {
        userId = auth.currentUser?.uid ?: ""
        val dateTime = LocalDate.now(ZoneId.of("Asia/Kolkata")).toString()
        val time = System.currentTimeMillis().toString()
        orderId = "$userId$time"
        // Create a map to hold the order details
        val orderDetails = hashMapOf(
            "userId" to userId,
            "orderTime" to LocalTime.now(ZoneId.of("Asia/Kolkata")).toString(),
            "orderDate" to dateTime,
            "timestamp" to System.currentTimeMillis(),
            "orderId" to orderId,
            "foodItems" to orderItems.map { item ->
                hashMapOf("name" to item.foodName, "quantity" to item.quantity, "price" to item.foodPrice,"image" to item.foodImage.toString())
            },
            "totalAmount" to totalAmount,
            "deliveryDetails" to hashMapOf(
                "name" to name,
                "address" to address,
                "phone" to phone
            )

        )

        // Push the order details to the database under a unique order ID
        databaseReference.child("orders").child(orderId).setValue(orderDetails)
            .addOnSuccessListener {
                Log.d("PayOutActivity", "Order created successfully with ID: $orderId")
                // Order was successfully created, you can now proceed to show the bottom sheet
                removeItemFromCart()
                addOrderToHistory(orderDetails)
            }
            .addOnFailureListener { e ->
                Log.e("PayOutActivity", "Error creating order: ${e.message}", e)
                // Handle the error, e.g., show an error message to the user
            }
    }

    private fun addOrderToHistory(orderDetails: Any) {
        databaseReference.child("users").child(userId).child("orderHistory").child(orderId).setValue(orderDetails)
            .addOnSuccessListener {
                Log.d("PayOutActivity", "Order ID added to history successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("PayOutActivity", "Error adding order ID to history: ${e.message}", e)

            }
    }

    private fun removeItemFromCart() {
        val cartReference = databaseReference.child("users").child(userId).child("CartItems")
        cartReference.removeValue()
    }
}