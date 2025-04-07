package com.example.foodordring

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.foodordring.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController // Use a lateinit var, not var

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Get NavHostFragment and NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        // 2. Setup BottomNavigationView with NavController
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)

        // 3. Notification Button Click Listener
        binding.notificationButton.setOnClickListener {
            val bottomSheet = notification_Bottom_Fragment()
            bottomSheet.show(supportFragmentManager, "Test")
        }
        updateUserInfo()
    }
    private fun updateUserInfo() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val displayName = user.displayName
            val photoUrl = user.photoUrl

            if (displayName != null) {
                binding.titleTextView.text = "Welcome, $displayName!"
            } else {
                binding.titleTextView.text = "Welcome!" // Default if no name
            }

            if (photoUrl != null) {
                Glide.with(this)
                    .load(photoUrl)
                    .circleCrop()  // Make the image circular
                    .placeholder(R.drawable.user) // Optional placeholder
                    .into(binding.userProfilePicture)
            }else {
                binding.userProfilePicture.setImageResource(R.drawable.user_plus) // Default image
            }
        } else {
            // Handle the case where no user is logged in (e.g., show a default message)
            binding.titleTextView.text = "Welcome!"
            binding.userProfilePicture.setImageResource(R.drawable.user_plus) // Default image
        }
    }
}