package com.example.foodordring

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.foodordring.databinding.ActivityDetailsBinding
import com.example.foodordring.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private var foodName: String? = null
    private var foodPrice: String? = null
    private var foodImage: String? = null
    private var foodDescription: String? = null
    private var foodIngredient: String? = null
    private var foodDiscountPrice: String? = null
    private var itemId: String? = null
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        //accept all the extra intent
        receiveData()

        binding.toolbar.setOnClickListener {
            finish()
        }
        binding.addToCart.setOnClickListener {
            //Add to cart
            addToCart()
        }

    }

    private fun addToCart() {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid?: ""
        //Create a cart item objects
        val cartItem = CartItems(foodName.toString(),foodPrice.toString(),foodImage.toString(),foodDescription.toString(),foodIngredient.toString(),foodDiscountPrice.toString(),quantity = 1,itemId.toString())

        //Save data to cart
        database.child("users").child(userId).child("CartItems").push().setValue(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show()
            }
    }

    private fun receiveData() {
        foodName = intent.getStringExtra("MenuItemName")
        foodPrice = intent.getStringExtra("MenuItemPrice")
        foodDescription = intent.getStringExtra("MenuItemDescription")
        foodIngredient = intent.getStringExtra("MenuItemIngredient")
        foodImage = intent.getStringExtra("MenuItemImage")
        foodDiscountPrice = intent.getStringExtra("MenuItemDiscountPrice")
        itemId = intent.getStringExtra("itemId")

        with(binding) {
            detailsFoodName.text = foodName
            detailsDescription.text = foodDescription
            detailsIngredients.text = foodIngredient
            Glide.with(this@DetailsActivity).load(foodImage).into(detailFoodImage)
        }
    }
}