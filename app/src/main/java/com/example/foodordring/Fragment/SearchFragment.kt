package com.example.foodordring.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.foodordring.adaptar.MenuAdapter
import com.example.foodordring.databinding.FragmentSearchBinding
import com.example.foodordring.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private lateinit var database: FirebaseDatabase
    private val originalMenuItems = mutableListOf<MenuItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Set up search view and fetch data
        setUpSearchView()
        //Retrive data from firebase
        retrieveMenuItem()
        return binding.root
    }

    private fun retrieveMenuItem() {
        //Get database reference
        database = FirebaseDatabase.getInstance()
        val menuRef = database.reference.child("Menu")
        menuRef.addListenerForSingleValueEvent(object : ValueEventListener { // Correct listener
            override fun onDataChange(snapshot: DataSnapshot) {
                val menuItems = mutableListOf<MenuItem>()
                for(foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                originalMenuItems.clear()
                originalMenuItems.addAll(menuItems)
                showAllMenu()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("SearchFragment", "Error fetching menu items: ${error.message}")
            }
        })
    }
    private fun showAllMenu() {
        val filteredMenuItems = ArrayList(originalMenuItems)
        setAdapters(filteredMenuItems)
    }

    private fun setAdapters(filteredMenuItems: ArrayList<MenuItem>) {
        adapter = MenuAdapter() // Corrected - No context, initialize with empty list.
        val itemWidthDp = 150 // Desired item width in dp
        val noOfColumns = calculateNoOfColumns(requireContext(), itemWidthDp)
        binding.menuRecycler.layoutManager =
            GridLayoutManager(requireContext(), noOfColumns)
        binding.menuRecycler.adapter = adapter
        adapter.updateData(filteredMenuItems)  // Pass the list *after* setting the adapter.
    }
    private fun setUpSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                filterMenuItems(query)
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                filterMenuItems(newText)
                return true
            }
        })
    }
    private fun filterMenuItems(query: String) {
        val filteredMenuItems: ArrayList<MenuItem> = originalMenuItems.filter {
            it.foodName?.contains(query, ignoreCase = true) == true
        } as ArrayList<MenuItem>
        setAdapters(filteredMenuItems)
    }
    fun calculateNoOfColumns(context: Context, itemWidthDp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        val screenWidthPx = displayMetrics.widthPixels
        val itemWidthPx = (itemWidthDp * displayMetrics.density).toInt()
        return screenWidthPx / itemWidthPx
    }
}