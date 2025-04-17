package com.example.foodordring.Fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.foodordring.Login
import com.example.foodordring.R
import com.example.foodordring.StringHandling.formatName
import com.example.foodordring.databinding.FragmentProfileBinding
import com.example.foodordring.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.UUID

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var storage: StorageReference
    private var selectedImageUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 71
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        storage = FirebaseStorage.getInstance().reference.child("profile_images")
        binding.editImageButton.elevation = 0f
        binding.editImageButton.setOnClickListener {  //Changed this line
            chooseImage()
        }


        binding.saveInfoButton.setOnClickListener {
            saveUserData()
        }
        binding.logoutButton.setOnClickListener {
            auth.signOut()
            showToast("Logged out successfully!")
            startActivity(Intent(requireContext(), Login::class.java))
            requireActivity().finish()
        }

        loadUserData()

        return view
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImageUri)
                binding.userImage.setImageBitmap(bitmap) // Changed this line
                uploadImage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage() {
        if (selectedImageUri != null) {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userId = currentUser.uid
                val imageRef = storage.child("${userId}/${UUID.randomUUID()}.jpg")
                imageRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener {
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            saveImageUrlToDatabase(userId, uri.toString())
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("ProfileFragment", "Upload failed: ${e.message}")
                        showToast("Image upload failed.")
                    }
            }
        }
    }

    private fun saveImageUrlToDatabase(userId: String, imageUrl: String) {
        database.reference.child("users").child(userId).child("profileImageUrl").setValue(imageUrl)
            .addOnSuccessListener {
                Log.d("ProfileFragment", "Image URL saved to database")
                showToast("Profile picture updated!")
                //  Optional: Reload user data to reflect the new image immediately
                loadUserData()
            }
            .addOnFailureListener { e ->
                Log.e("ProfileFragment", "Failed to save image URL: ${e.message}")
                showToast("Failed to update profile picture.")
            }
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

                            //Load profile image if it exists
                            val profileImageUrl = snapshot.child("profileImageUrl").getValue(String::class.java)
                            if (profileImageUrl != null) {
                                Glide.with(this@ProfileFragment)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.user) // Use a default image
                                    .circleCrop() // If you want a circular image
                                    .into(binding.userImage)
                            } else {
                                binding.userImage.setImageResource(R.drawable.user_plus)
                            }

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
            val name = binding.profileName.text.toString().formatName()
            val address = binding.profileAddress.text.toString()
            val email = binding.profileEmail.text.toString()
            val phone = binding.profilePhoneNo.text.toString()

            if(name.isNullOrEmpty()){
                binding.profileName.error = "Name cannot be empty"
                binding.profileName.requestFocus()
                return
            }
            if(address.isNullOrEmpty()){
                binding.profileAddress.error = "Address cannot be empty"
                binding.profileAddress.requestFocus()
                return
            }
            if(email.isNullOrEmpty()){
                binding.profileEmail.error = "Email cannot be empty"
                binding.profileEmail.requestFocus()
                return
            }
            if(phone.isNullOrEmpty()){
                binding.profilePhoneNo.error = "Phone number cannot be empty"
                binding.profilePhoneNo.requestFocus()
                return
            }
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.profileEmail.error = "Invalid Email"
                binding.profileEmail.requestFocus()
                return
            }
            if(phone.length != 10){
                binding.profilePhoneNo.error = "Invalid Phone Number"
                binding.profilePhoneNo.requestFocus()
                return
            }
            if (!phone.matches("^\\+91[6-9][0-9]{9}$".toRegex()) && !phone.matches("^[6-9][0-9]{9}$".toRegex())) {
                binding.profilePhoneNo.error = "Invalid Indian Phone Number"
                binding.profilePhoneNo.requestFocus()
                return
            }
            if(name.length < 3){
                binding.profileName.error = "Name should be at least 3 characters"
                binding.profileName.requestFocus()
                return
            }
            if(address.length < 10){
                binding.profileAddress.error = "Address should be at least 10 characters"
                binding.profileAddress.requestFocus()
                return
            }
            if(email.length < 10){
                binding.profileEmail.error = "Email should be at least 10 characters"
                binding.profileEmail.requestFocus()
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

            userReference.updateChildren(userData)
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