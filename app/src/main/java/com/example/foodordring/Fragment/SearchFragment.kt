package com.example.foodordring.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodordering.adapter.MenuAdapter
import com.example.foodordring.databinding.FragmentSearchBinding
import com.example.foodordring.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private val originalMenuItems: MutableList<MenuItem> = mutableListOf()
    private val filteredMenuItems: MutableList<MenuItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Initialize RecyclerView and Adapter
        adapter = MenuAdapter(filteredMenuItems) // Start with filtered items
        binding.menuView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuView.adapter = adapter
        binding.menuView.visibility = View.VISIBLE
        // Set up search view and fetch data
        setUpSearchView()
        fetchAllMenuItems()

        return binding.root
    }

    private fun fetchAllMenuItems() {
        val database = FirebaseDatabase.getInstance()
        val menuItemsRef = database.getReference("Menu")

        menuItemsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                originalMenuItems.clear()
                for (itemSnapshot in snapshot.children) {
                    val menuItem = itemSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let {
                        originalMenuItems.add(it)
                        Log.d("SearchFragment", "Fetched item: ${it.foodName}")
                    }
                }

                // Update filteredMenuItems and the adapter
                showAllMenu()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SearchFragment", "Error fetching menu items: ${error.message}")
            }
        })
    }

    private fun showAllMenu() {
        // Populate filteredMenuItems and update the adapter
        filteredMenuItems.clear()
        filteredMenuItems.addAll(originalMenuItems)
        adapter.updateData(filteredMenuItems)
    }

    private fun setUpSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterMenuItems(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterMenuItems(newText)
                return true
            }
        })
    }

    private fun filterMenuItems(query: String?) {
        filteredMenuItems.clear()
        if (query.isNullOrEmpty()) {
            filteredMenuItems.addAll(originalMenuItems)
        } else {
            filteredMenuItems.addAll(originalMenuItems.filter {
                it.foodName?.contains(query, ignoreCase = true) ?: false
            })
        }
        Log.d("SearchFragment", "Filtering with query: $query")
        Log.d("SearchFragment", "Filtered items count: ${filteredMenuItems.size}")
        if (filteredMenuItems.isNotEmpty()) {
            Log.d("SearchFragment", "First filtered item: ${filteredMenuItems[0].foodName}")
        }
        adapter.updateData(filteredMenuItems)
    }
}