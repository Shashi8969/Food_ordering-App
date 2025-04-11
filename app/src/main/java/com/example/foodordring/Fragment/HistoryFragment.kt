package com.example.foodordring.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodordring.R
import com.example.foodordring.adaptar.BuyAgainAdapter
import com.example.foodordring.databinding.FragmentHistoryBinding
import com.example.foodordring.model.OrderItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var database:FirebaseDatabase
    private lateinit var auth:FirebaseAuth
    private lateinit var userId:String
    private var listOfItems: MutableList<OrderItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        // Initialize Firebase
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid ?: ""
        val orderReference = database.reference.child("users").child(userId).child("orderHistory")
        orderReference.get().addOnSuccessListener {
            if(it.exists()) {
                for (childSnapshot in it.children) {
                    val orderDetails = childSnapshot.getValue(OrderItem::class.java)
                    if (orderDetails != null) {
                        listOfItems.add(orderDetails)
                    }
                    buyAgainAdapter.notifyDataSetChanged()
                }
            }
        }

        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView()
    {
        val buyAgainFoodName :List<String> = listOf("Pizza","Burgers","sandwich","momo")
        val buyAgainFoodPrice :List<String> = listOf("$5","$6","$7","$8")
        val buyAgainImage = listOf(R.drawable.menu1,R.drawable.menu2,R.drawable.menu3,R.drawable.menu4)
        buyAgainAdapter = BuyAgainAdapter(buyAgainFoodName,buyAgainFoodPrice,buyAgainImage)
        binding.buyAgainRecyclerView.adapter = buyAgainAdapter

        binding.buyAgainRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}