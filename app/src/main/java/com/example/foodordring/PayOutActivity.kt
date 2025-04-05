package com.example.foodordring

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var totalAmount: String

    private lateinit var databaseReference: DatabaseReference
    private lateinit var orderId: String
    private lateinit var foodName: MutableList<String>
    private lateinit var foodPrice: MutableList<String>
    private lateinit var foodDescription: MutableList<String>
    private lateinit var foodImageUrl: MutableList<String>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var foodQuantities: MutableList<Int>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialize Firebase Authentication
        databaseReference = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()

        //Set user data
        setUserData()

        //Get order details from Firebase
        orderId = intent.getStringExtra("orderId") ?: ""
        foodName = intent.getStringArrayListExtra("foodName") ?: mutableListOf()
        foodPrice = intent.getStringArrayListExtra("foodPrice") ?: mutableListOf()
        foodDescription = intent.getStringArrayListExtra("foodDescription") ?: mutableListOf()
        foodImageUrl = intent.getStringArrayListExtra("foodImageUrl") ?: mutableListOf()
        foodIngredients = intent.getStringArrayListExtra("foodIngredients") ?: mutableListOf()
        foodQuantities = intent.getIntegerArrayListExtra("foodQuantities") ?: mutableListOf()

        //Calculate total amount and show it in the UI
        binding.totalAmount.text = buildString {
            append("₹")
            append(calculateTotalAmount().toString())
        }
        binding.totalAmount.isEnabled = false


        val bottomSheetFragment = CongratsBottomSheet()
        binding.placeorderbutton.setOnClickListener {
            name = binding.name.text.toString()
            address = binding.address.text.toString()
            phone = binding.phone.text.toString()
            totalAmount = binding.totalAmount.text.toString()
            if (name.isNotEmpty() && address.isNotEmpty() && phone.isNotEmpty()) {
                createOrderId()
                bottomSheetFragment.show(
                    supportFragmentManager,
                    "Test"
                ) // Show bottom sheet before finishing
            } else {
                // Show an error message if any field is empty
                // You can use a Toast or a more user-friendly dialog
                Log.w("PayOutActivity", "Please fill in all fields.")
                // Example using a Toast:
                // Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.backbtn.setOnClickListener {
            finish()
        }
    }


    private fun calculateTotalAmount(): Int {
        var totalAmount = 0
        for (i in 0 until foodQuantities.size) {
            val price = foodPrice[i].toInt()
            val quantity = foodQuantities[i]
            totalAmount += price * quantity
        }
        return totalAmount
    }

    private fun setUserData() {
        val user = auth.currentUser
        if (user != null) {
            userId = user.uid
            Log.d("PayOutActivity", "Before databaseReference initialization")
            val userReference = databaseReference.child("users").child(userId)
            Log.d("PayOutActivity", "After databaseReference initialization")

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userName = snapshot.child("name").getValue(String::class.java) ?: ""
                        val userAddress =
                            snapshot.child("address").getValue(String::class.java) ?: ""
                        val userPhone = snapshot.child("phone").getValue(String::class.java) ?: ""
                        Log.d(
                            "PayOutActivity",
                            "User data fetched: $userName, $userAddress, $userPhone"
                        )


                        binding.apply {
                            name.setText(userName)
                            address.setText(userAddress)
                            phone.setText(userPhone)
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
        //Create a orderId in the database
        userId = auth.currentUser?.uid ?: ""
        val dateTime = LocalDate.now(ZoneId.of("Asia/Kolkata")).toString()
        val time = System.currentTimeMillis().toString()
        orderId = "$userId$time"
        // Create a map to hold the order details
        val orderDetails = hashMapOf(
            "userId" to userId,
            "orderTime" to LocalTime.now(ZoneId.of("Asia/Kolkata")).toString(),
            "orderDate" to dateTime,
            "foodItems" to foodName.zip(foodQuantities) { name, quantity ->
                hashMapOf("name" to name, "quantity" to quantity)
            },
            "totalAmount" to calculateTotalAmount(),
            "deliveryDetails" to hashMapOf(
                "name" to name,
                "address" to address,
                "phone" to phone
            )
            // You can add more order details here as needed, e.g., payment method, status, etc.
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