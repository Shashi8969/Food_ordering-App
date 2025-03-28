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

        val bottomSheetFragment = CongratsBottomSheet()
        binding.placeorderbutton.setOnClickListener {
            bottomSheetFragment.show(supportFragmentManager, "Test")
        }
        binding.backbtn.setOnClickListener {
            finish()
        }
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
                        val userAddress = snapshot.child("address").getValue(String::class.java)?:""
                        val userPhone = snapshot.child("phone").getValue(String::class.java)?:""

                        binding.apply {
                            name.setText(userName)
                            address.setText(userAddress)
                            phone.setText(userPhone)
                        }
                    }
                    }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
        name = intent.getStringExtra("name") ?: ""
        address = intent.getStringExtra("address") ?: ""
        phone = intent.getStringExtra("phone") ?: ""
        totalAmount = intent.getStringExtra("totalAmount") ?: ""
    }
}