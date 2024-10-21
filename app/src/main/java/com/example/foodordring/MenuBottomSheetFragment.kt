package com.example.foodordring

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodordring.adaptar.MenuAdapter
import com.example.foodordring.databinding.FragmentMenuBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MenuBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentMenuBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMenuBottomSheetBinding.inflate(inflater,container,false)

        binding.buttonBack.setOnClickListener {
            dismiss()
        }
        val menuFoodName = listOf("Pizza","Burgers","sandwich","momo")
        val menuItemPrice = listOf("$5","$6","$7","$8")
        val menuItemImage = listOf(R.drawable.menu1,R.drawable.menu2,R.drawable.menu3,R.drawable.menu4)
        val adapter = MenuAdapter(ArrayList(menuFoodName),ArrayList(menuItemPrice),ArrayList(menuItemImage))
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adapter
        return binding.root
    }

    companion object {

    }
}