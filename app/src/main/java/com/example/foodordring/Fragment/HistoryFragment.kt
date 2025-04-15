package com.example.foodordring.Fragment

import OrderItem
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.foodordring.R
import com.example.foodordring.adaptar.BuyAgainAdapter
import com.example.foodordring.databinding.FragmentHistoryBinding
import com.example.foodordring.model.RecentBuyModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth  // Initialize auth here
    private lateinit var userId: String
    private var listOfItems: MutableList<OrderItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth in onCreate
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()

        // Retrieve and display order history
        retrieveOrderHistory()

        setupRecyclerView()
        return binding.root
    }

    private fun retrieveOrderHistory() {
        binding.recentBuyItemConstraintLayout.visibility = View.INVISIBLE
        userId = auth.currentUser?.uid ?: ""
        Log.d("HistoryFragment", "User ID: $userId")

        if (userId.isEmpty()) {
            Log.w("HistoryFragment", "User ID is empty. Not retrieving history.")
            return
        }

        val buyItemReference = database.reference.child("users").child(userId).child("orderHistory")
        val sortingQuery = buyItemReference.orderByChild("timestamp").limitToLast(1) // Assuming timestamp is still directly under the orderId node

        sortingQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("HistoryFragment", "DataSnapshot exists: ${snapshot.exists()}")
                Log.d("HistoryFragment", "Number of children: ${snapshot.childrenCount}")

                if (snapshot.hasChildren()) {
                    val mostRecentOrderSnapshot = snapshot.children.last() // Get the most recent order
                    Log.d("HistoryFragment", "Most recent order key: ${mostRecentOrderSnapshot.key}")

                    // Check if "foodItems" exists within the most recent order
                    if (mostRecentOrderSnapshot.hasChild("foodItems")) {
                        val foodItemsSnapshot = mostRecentOrderSnapshot.child("foodItems")

                        //Assuming foodItems is a List
                        if(foodItemsSnapshot.childrenCount > 0){
                            val firstFoodItemSnapshot = foodItemsSnapshot.children.first() // Get first item.
                            val buyHistoryItem = firstFoodItemSnapshot.getValue(RecentBuyModel::class.java)
                            Log.d("HistoryFragment", "Retrieved OrderItem: $buyHistoryItem")
                            buyHistoryItem?.let {
                                setDataInRecentBuyItem(it)
                            }
                        } else{
                            Log.d("HistoryFragment", "No Food items found in history")
                        }

                    } else {
                        Log.w("HistoryFragment", "No 'foodItems' found in the most recent order.")
                    }
                } else {
                    Log.d("HistoryFragment", "No order history found for user.")
                    // Optionally display a message or placeholder in the UI
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HistoryFragment", "Database error: ${error.message}")
            }
        })
    }
    private fun setDataInRecentBuyItem(recentOrderItem: RecentBuyModel) {
        binding.recentBuyItemConstraintLayout.visibility = View.VISIBLE
        with(binding) {
            RecentFoodName.text = recentOrderItem.name ?: "Not Available" // Use name, handle null
            RecentItemPrice.text = recentOrderItem.price?.toString() ?: "N/A"  //Use price, handle null
            // No image to load anymore, replace with a default image or remove the ImageView
            Glide.with(requireContext()).load(recentOrderItem.image).into(recentImage)?:""
        }
    }

    private fun setupRecyclerView() {
        val buyAgainFoodName: List<String> = listOf("Pizza", "Burgers", "sandwich", "momo")
        val buyAgainFoodPrice: List<String> = listOf("$5", "$6", "$7", "$8")
        val buyAgainImage = listOf(R.drawable.menu1, R.drawable.menu2, R.drawable.menu3, R.drawable.menu4)
        buyAgainAdapter = BuyAgainAdapter(buyAgainFoodName, buyAgainFoodPrice, buyAgainImage)
        binding.buyAgainRecyclerView.adapter = buyAgainAdapter

        binding.buyAgainRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}