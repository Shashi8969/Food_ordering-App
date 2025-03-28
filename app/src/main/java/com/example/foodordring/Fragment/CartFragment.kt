package com.example.foodordring.Fragment

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


class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var foodName: MutableList<String>
    private lateinit var foodPrice: MutableList<String>
    private lateinit var foodDescription: MutableList<String>
    private lateinit var foodImageUrl: MutableList<String>
    private lateinit var foodUngredients: MutableList<String>
    private lateinit var quantity: MutableList<Int>
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        retriveCartItems()


        binding.proceedbutton.setOnClickListener {
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
                    cartItems?.foodIngredient?.let { foodIngredients.add(it) }
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
            foodUngredients = mutableListOf()
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
                        cartItem?.foodIngredient?.let { foodUngredients.add(it) }
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
                        foodUngredients,
                        quantity
                    )
                    binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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


                companion object {

            @JvmStatic
            fun newInstance(param1: String, param2: String) =
                CartFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
        }
    }