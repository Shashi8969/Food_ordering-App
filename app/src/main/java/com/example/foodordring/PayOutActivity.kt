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
        val intent = intent
        orderId = intent.getStringExtra("orderId") ?: ""
        foodName = intent.getStringArrayListExtra("foodName") ?: mutableListOf()
        foodPrice = intent.getStringArrayListExtra("foodPrice") ?: mutableListOf()
        foodDescription = intent.getStringArrayListExtra("foodDescription") ?: mutableListOf()
        foodImageUrl = intent.getStringArrayListExtra("foodImageUrl") ?: mutableListOf()
        foodIngredients = intent.getStringArrayListExtra("foodIngredients") ?: mutableListOf()
        foodQuantities = intent.getIntegerArrayListExtra("foodQuantities") ?: mutableListOf()

        //Calculate total amount and show it in the UI
        binding.totalAmount.text = calculateTotalAmount().toString()
        binding.totalAmount.isEnabled = false


        val bottomSheetFragment = CongratsBottomSheet()
        binding.placeorderbutton.setOnClickListener {
            name = binding.name.text.toString()
            address = binding.address.text.toString()
            phone = binding.phone.text.toString()
            totalAmount = binding.totalAmount.text.toString()
            if (name.isNotEmpty() && address.isNotEmpty() && phone.isNotEmpty()) {
                createOrderId()
                bottomSheetFragment.show(supportFragmentManager, "Test") // Show bottom sheet before finishing
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
        if(user!=null){
            userId = user.uid
            Log.d("PayOutActivity", "Before databaseReference initialization")
            val userReference = databaseReference.child("users").child(userId)
            Log.d("PayOutActivity", "After databaseReference initialization")

            userReference.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val userName = snapshot.child("name").getValue(String::class.java)?:""
                        val userAddress = snapshot.child("address").getValue(String::class.java)?:"" // Swapped
                        val userPhone = snapshot.child("phone").getValue(String::class.java)?:"" // Swapped

                        binding.apply {
                            name.setText(userName)
                            address.setText(userAddress) // Swapped
                            phone.setText(userPhone) // Swapped
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
        // The following lines are likely incorrect, as they override data fetched from Firebase
        // and use data from the intent, which might not be what you want.  Comment them out
        // unless you have a specific reason to use intent data as a fallback.
        /*
        name = intent.getStringExtra("name") ?: ""
        address = intent.getStringExtra("address") ?: ""
        phone = intent.getStringExtra("phone") ?: ""
        */
    }
    private fun createOrderId() {
        //Create a orderId in the database
        userId = auth.currentUser?.uid?:""
        val time = System.currentTimeMillis()
        val itemPushKeys =databaseReference.child("orderDetails").child(userId).child("items").push().key

    }
}