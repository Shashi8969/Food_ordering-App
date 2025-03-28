package com.example.foodordring

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.foodordring.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding // Assuming you're using View Binding
    private var isPasswordVisible = false
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up password visibility toggle
        setupPasswordVisibilityToggle(binding.etPassword)

        binding.txtCreate.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }

        auth = Firebase.auth

        binding.LoginButton.setOnClickListener {
            loginWithEmailAndPassword()
        }
    }

    private fun loginWithEmailAndPassword() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    startActivity(Intent(this, MainActivity::class.java))
                    finish() // Optional: Finish the LoginActivity
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Finish LoginActivity to prevent going back
        }
        // ... (Your existing login button click listener, if needed) ...
    }
    private fun setupPasswordVisibilityToggle(passwordEditText: EditText) {
        passwordEditText.setOnTouchListener { _, event ->
            val drawableRight = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (passwordEditText.right - passwordEditText.compoundDrawables[drawableRight].bounds.width())) {
                    isPasswordVisible = !isPasswordVisible
                    val selection = passwordEditText.selectionEnd
                    if (isPasswordVisible) {
                        passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            ContextCompat.getDrawable(this, R.drawable.lock),
                            null,
                            ContextCompat.getDrawable(this, R.drawable.eye),
                            null
                        )
                        passwordEditText.inputType =
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    } else {
                        passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            ContextCompat.getDrawable(this, R.drawable.lock),
                            null,
                            ContextCompat.getDrawable(this, R.drawable.eye_hide),
                            null
                        )
                        passwordEditText.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }
                    passwordEditText.setSelection(selection)
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }
}