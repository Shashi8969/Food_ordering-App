package com.example.foodordring.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.foodordering.adapter.MenuAdapter
import com.example.foodordring.R
import com.example.foodordring.databinding.FragmentHomeBinding
import com.example.foodordring.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
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
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewAllMenu.setOnClickListener {
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }

        //Retrieve and display popular menu items
        retrieveAndDisplayPopularMenuItems()
        return binding.root
    }

    private fun retrieveAndDisplayPopularMenuItems() {
        //Get a reference to the Firebase Realtime Database
        database = FirebaseDatabase.getInstance()
        val menuRef: DatabaseReference = database.getReference("Menu")
        menuItems = mutableListOf()

        menuRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapshot in snapshot.children) {
                    val menuItem = itemSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                    //Display popular menu items
                    randomPopularItems()
                }

            }

            private fun randomPopularItems() {
                //Randomly select 6 popular menu items
                val index: List<Int> = menuItems.indices.toList().shuffled()
                val numItemToTake = 6
                val subList = index.take(numItemToTake).map { menuItems[it] }
                setPopularItemsAdapter(subList)
            }

            private fun setPopularItemsAdapter(subList: List<MenuItem>) {
                val adapter = MenuAdapter(subList, requireContext())
                val itemWidthDp = 150 // Desired item width in dp
                val noOfColumns = calculateNoOfColumns(requireContext(), itemWidthDp)
                binding.popularRecyclerView.layoutManager = GridLayoutManager(requireContext(), noOfColumns) // '2' specifies the number of columns
                binding.popularRecyclerView.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })

    }
    fun calculateNoOfColumns(context: Context, itemWidthDp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        val screenWidthPx = displayMetrics.widthPixels
        val itemWidthPx = (itemWidthDp * displayMetrics.density).toInt()
        return screenWidthPx / itemWidthPx
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = ArrayList<SlideModel>() // Create image list
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner2, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner3, ScaleTypes.FIT))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(position: Int) {
                // You can listen here.
                val itemPosition = imageList[position]
                val itemMessage = "Selected Image $position"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}