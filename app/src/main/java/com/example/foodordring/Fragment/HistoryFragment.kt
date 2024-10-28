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


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

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