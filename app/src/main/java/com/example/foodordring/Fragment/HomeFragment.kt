package com.example.foodordring.Fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.foodordring.R
import com.example.foodordring.StringHandling.getFirstNameWithoutPrefix
import com.example.foodordring.adaptar.MenuAdapter
import com.example.foodordring.databinding.FragmentHomeBinding
import com.example.foodordring.model.MenuItem
import com.example.foodordring.notification_Bottom_Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItem>
    private var auth = FirebaseAuth.getInstance()

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
        binding.userProfilePicture.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
        //Notification Button Click Listener
        binding.notificationButton.setOnClickListener {
            val bottomSheet = notification_Bottom_Fragment()
            bottomSheet.show(parentFragmentManager, "Test")
        }

        //Retrieve and display popular menu items
        retrieveAndDisplayPopularMenuItems()
        updateUserName()
        return binding.root
    }

    private fun retrieveAndDisplayPopularMenuItems() {
        //Get a reference to the Firebase Realtime Database
        database = FirebaseDatabase.getInstance()
        val menuRef: DatabaseReference = database.getReference("Menu")
        menuItems = mutableListOf()
        //Retrieve data from the database
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
                val adapter = MenuAdapter(subList as MutableList<MenuItem>)
                val itemWidthDp = 150 // Desired item width in dp
                val noOfColumns = calculateNoOfColumns(requireContext(), itemWidthDp)
                binding.popularRecyclerView.layoutManager = GridLayoutManager(
                    requireContext(),
                    noOfColumns
                ) // '2' specifies the number of columns
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitConfirmationDialog()
                }
            }
        )
    }
   private fun updateUserName() {
        val userId = auth.currentUser?.uid ?: ""
        if (userId.isNotEmpty()) { // Check if userId is not empty
            val userRef: DatabaseReference = database.reference.child("users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) { //Check if snapshot exists
                        val username = snapshot.child("name").getValue(String::class.java) ?: ""
                        val profileImageUrl = snapshot.child("profileImageUrl").getValue(String::class.java)

                        binding.usernameTextView.text = "Welcome, ${username.getFirstNameWithoutPrefix()}"

                        if (profileImageUrl != null) {
                            Glide.with(this@HomeFragment) // Use this@HomeFragment
                                .load(profileImageUrl)
                                .placeholder(R.drawable.user) // Replace with your default image
                                .circleCrop() // To display as circle
                                .into(binding.userProfilePicture)
                        } else {
                            binding.userProfilePicture.setImageResource(R.drawable.user)
                        }
                    } else {
                        Log.w("HomeFragment", "No data found for user: $userId")
                        //Handle the case where no user data exists (e.g. set default values)
                        binding.usernameTextView.text = "Welcome, Guest"
                        binding.userProfilePicture.setImageResource(R.drawable.user)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HomeFragment", "Database error: ${error.message}")
                    //Handle the error (e.g. display an error message)
                    binding.usernameTextView.text = "Welcome, Guest" // Set a default
                    binding.userProfilePicture.setImageResource(R.drawable.user)
                    Toast.makeText(requireContext(), "Error loading user data", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Handle case where no user is logged in
            binding.usernameTextView.text = "Welcome, Guest"
            binding.userProfilePicture.setImageResource(R.drawable.user)
        }
    }
    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext()) // Use requireContext() in a Fragment
            .setTitle("Exit?")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                // In a Fragment, navigate up or perform other actions, not finish()
                findNavController().navigateUp() // Example with Navigation Component
                // or
                requireActivity().finish() // If you want to exit the entire app
            }
            .setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .show()
    }
}