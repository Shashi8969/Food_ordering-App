package com.example.foodordring.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.foodordring.Login
import com.example.foodordring.databinding.FragmentProfileBinding
import com.example.foodordring.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.saveInfo.setOnClickListener {
            saveUserData() // Call saveUserData when the button is clicked
        }
        binding.logoutButton.setOnClickListener {
            auth.signOut()
            showToast("Logged out successfully!")
            startActivity(Intent(requireContext(), Login::class.java))
            requireActivity().finish()
        }

        loadUserData() // Load user data when the fragment is created

        return view
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            Log.d("ProfileFragment", "Loading data for user: $userId")
            val userReference = database.reference.child("users").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userProfile = snapshot.getValue(UserModel::class.java)
                        if (userProfile != null) {
                            Log.d("ProfileFragment", "Data fetched successfully: $userProfile")
                            updateUI(userProfile)
                        } else {
                            Log.w("ProfileFragment", "Data is null for user: $userId")
                            showToast("Failed to fetch profile data.")
                        }
                    } else {
                        Log.i("ProfileFragment", "No data found for user: $userId")
                        showToast("No profile data found.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ProfileFragment", "Database error: ${error.message}")
                    showToast("Failed to fetch profile: ${error.message}")
                }
            })
        } else {
            Log.w("ProfileFragment", "User not logged in.")
            showToast("Please log in to view your profile.")
        }
    }

    private fun saveUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val name = binding.profileName.text.toString()
            val address = binding.profileAddress.text.toString()
            val email = binding.profileEmail.text.toString()
            val phone = binding.profilePhoneNo.text.toString()

            if (name.isBlank() || address.isBlank() || email.isBlank() || phone.isBlank()) {
                showToast("Please fill in all fields.")
                return
            }

            val userProfile = UserModel(name, address, email, phone)
            val userReference = database.reference.child("users").child(userId)

            val userData = hashMapOf<String, Any>(
                "name" to name,
                "address" to address,
                "email" to email,
                "phone" to phone
            )

            userReference.setValue(userProfile)
                .addOnSuccessListener {
                    Log.d("ProfileFragment", "Profile updated successfully.")
                    showToast("Profile updated successfully!")
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileFragment", "Failed to update profile: ${e.message}")
                    showToast("Failed to update profile: ${e.message}")
                }
        } else {
            showToast("Please log in to update your profile.")
        }
    }

    private fun updateUI(user: UserModel) {
        binding.profileName.setText(user.name)
        binding.profileAddress.setText(user.address)
        binding.profileEmail.setText(user.email)
        binding.profilePhoneNo.setText(user.phone)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}