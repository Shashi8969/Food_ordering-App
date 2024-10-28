package com.example.foodordring.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodordring.PayOutActivity
import com.example.foodordring.R
import com.example.foodordring.adaptar.CartAdapter
import com.example.foodordring.databinding.FragmentCartBinding


class CartFragment : Fragment() {

    private lateinit var binding:FragmentCartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater,container,false)

        val carFoodName = listOf("Pizza","Burgers","sandwich","momo")
        val cartItemPrice = listOf("$5","$6","$7","$8")
        val cartImage = listOf(R.drawable.menu1,R.drawable.menu2,R.drawable.menu3,R.drawable.menu4)
        val cartAdapter = CartAdapter(ArrayList(carFoodName),ArrayList(cartItemPrice),ArrayList(cartImage))
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter = cartAdapter

        binding.proceedbutton.setOnClickListener{
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            startActivity(intent)
        }
        return binding.root
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