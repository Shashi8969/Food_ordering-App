package com.example.foodordring.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.foodordering.adapter.MenuAdapter
import com.example.foodordring.databinding.FragmentMenuBottomSheetBinding
import com.example.foodordring.model.MenuItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentMenuBottomSheetBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMenuBottomSheetBinding.inflate(inflater, container, false)

        binding.buttonBack.setOnClickListener {
            dismiss()
        }
        retriveMenuItems()
        return binding.root
    }

    private fun retriveMenuItems() {
        database = FirebaseDatabase.getInstance()
        val menuRef = database.getReference("Menu")
        menuItems = mutableListOf()
        menuRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapshot in snapshot.children) {
                    val menuItem = itemSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                    //Set adapter
                    setAdapeter()
                }
            }

            private fun setAdapeter() {
                val adapter = MenuAdapter(menuItems, requireContext())
                val itemWidthDp = 150 // Desired item width in dp
                val noOfColumns = calculateNoOfColumns(requireContext(), itemWidthDp)
                binding.menuRecyclerView.layoutManager =
                    GridLayoutManager(requireContext(), noOfColumns)
                binding.menuRecyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    fun calculateNoOfColumns(context: Context, itemWidthDp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        val screenWidthPx = displayMetrics.widthPixels
        val itemWidthPx = (itemWidthDp * displayMetrics.density).toInt()
        return screenWidthPx / itemWidthPx
    }



    companion object {

    }
}