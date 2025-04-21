package com.example.foodordring

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodordring.adaptar.BuyAgainAdapter
import com.example.foodordring.databinding.ActivityRecentBuyBinding
import com.example.foodordring.model.RecentBuyModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RecentBuy : AppCompatActivity() {

    private lateinit var binding: ActivityRecentBuyBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private val listOfItems: MutableList<RecentBuyModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecentBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        setupRecyclerView()
        retrieveRecentBuys()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        buyAgainAdapter = BuyAgainAdapter(listOfItems)
        binding.recentBuysRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RecentBuy)
            adapter = buyAgainAdapter
        }
    }

    private fun retrieveRecentBuys() {
        userId = auth.currentUser?.uid ?: ""
        Log.d("RecentBuysActivity", "User ID: $userId")

        if (userId.isEmpty()) {
            Log.w("RecentBuysActivity", "User ID is empty. Not retrieving history.")
            return
        }

        val buyItemReference =
            database.reference.child("users").child(userId).child("orderHistory")
        buyItemReference.orderByChild("timestamp")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listOfItems.clear() // Clear existing data

                    for (orderSnapshot in snapshot.children) {
                        if (orderSnapshot.hasChild("foodItems")) {
                            val foodItemsSnapshot = orderSnapshot.child("foodItems")
                            for (foodItemSnapshot in foodItemsSnapshot.children) {
                                val buyHistoryItem =
                                    foodItemSnapshot.getValue(RecentBuyModel::class.java)
                                buyHistoryItem?.let {
                                    listOfItems.add(it)
                                }
                            }
                        }
                    }
                    //  You might want to sort the list, e.g., by date/time
                    // listOfItems.sortByDescending { it.timestamp } // If you have a timestamp property.
                    Log.d(
                        "RecentBuysActivity",
                        "Retrieved ${listOfItems.size} recent buy items."
                    )
                    buyAgainAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("RecentBuysActivity", "Database error: ${error.message}")
                }
            })
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, RecentBuy::class.java)
            context.startActivity(intent)
        }
    }
}