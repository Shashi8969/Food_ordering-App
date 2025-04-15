package com.example.foodordring.Fragment

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
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private val listOfItems: MutableList<RecentBuyModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        setupRecyclerView() // Initialize RecyclerView *before* fetching data
        retrieveOrderHistory()
        return binding.root
    }

    private fun setupRecyclerView() {
        buyAgainAdapter = BuyAgainAdapter(listOfItems) // Initialize with an empty list
        binding.buyAgainRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = buyAgainAdapter
        }
    }

    private fun retrieveOrderHistory() {
        userId = auth.currentUser?.uid ?: ""
        Log.d("HistoryFragment", "User ID: $userId")

        if (userId.isEmpty()) {
            Log.w("HistoryFragment", "User ID is empty. Not retrieving history.")
            return
        }

        val buyItemReference = database.reference.child("users").child(userId).child("orderHistory")
        buyItemReference.orderByChild("timestamp").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderHistoryItems = mutableListOf<RecentBuyModel>()
                var mostRecentItem: RecentBuyModel? = null

                if (snapshot.hasChildren()) {
                    // Since we ordered by timestamp, the last child is the most recent
                    val mostRecentOrderSnapshot = snapshot.children.last()

                    if (mostRecentOrderSnapshot.hasChild("foodItems")) {
                        val foodItemsSnapshot = mostRecentOrderSnapshot.child("foodItems")
                        //Assumes foodItems is a List. Adapt if it is a map!
                        if(foodItemsSnapshot.hasChildren()){
                            val firstFoodItemSnapshot = foodItemsSnapshot.children.first()
                            mostRecentItem = firstFoodItemSnapshot.getValue(RecentBuyModel::class.java)
                        }

                        if (mostRecentItem != null) {
                            setDataInRecentBuyItem(mostRecentItem!!)
                        }
                    }
                    // Collect all food items EXCEPT the most recent one for the RecyclerView.
                    for (orderSnapshot in snapshot.children) {
                        if (orderSnapshot.key != mostRecentOrderSnapshot.key) {
                            if (orderSnapshot.hasChild("foodItems")) {
                                val foodItems = orderSnapshot.child("foodItems")
                                for (foodItemSnapshot in foodItems.children) {
                                    val buyHistoryItem = foodItemSnapshot.getValue(RecentBuyModel::class.java)
                                    buyHistoryItem?.let {
                                        orderHistoryItems.add(it)
                                    }
                                }
                            }
                        }
                        else { //For the most recent order. Get all except the first one
                            val foodItems = orderSnapshot.child("foodItems")
                            // Skip the first one
                            val foodItemsToDisplay = foodItems.children.toList().drop(1)
                            for (foodItemSnapshot in foodItemsToDisplay) {
                                val buyHistoryItem = foodItemSnapshot.getValue(RecentBuyModel::class.java)
                                buyHistoryItem?.let {
                                    orderHistoryItems.add(it)
                                }
                            }
                        }
                    }
                    listOfItems.addAll(orderHistoryItems)
                    Log.d("HistoryFragment", "List of items to be shown in RecyclerView $listOfItems")
                } else {
                    Log.d("HistoryFragment", "No order history found for user.")
                }
                Log.d("HistoryFragment", "Updating RecyclerView with ${listOfItems.size} items")
                buyAgainAdapter.notifyDataSetChanged() // Notify adapter of data change

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HistoryFragment", "Database error: ${error.message}")
            }
        })
    }
    private fun setDataInRecentBuyItem(recentOrderItem: RecentBuyModel) {
        binding.recentBuyItemConstraintLayout.visibility = View.VISIBLE
        binding.apply {
            RecentFoodName.text = recentOrderItem.name ?: "Not Available"
            RecentItemPrice.text = recentOrderItem.price?.toString() ?: "N/A"
            Glide.with(requireContext())
                .load(recentOrderItem.image)
                .placeholder(R.drawable.menu1) // Add a default image
                .error(R.drawable.menu2) // Image to show on error
                .into(recentImage)
        }
    }
}