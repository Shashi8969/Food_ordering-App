package com.example.foodordring

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.foodordring.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false
    private lateinit var googleSignInClient: GoogleSignInClient
    // private lateinit var credentialManager: CredentialManager // Unused?
    private lateinit var signInIntentLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Password visibility toggle (seems fine)
        setupPasswordVisibilityToggle(binding.etPassword)
        checkIfUserIsLoggedIn()

        binding.txtCreate.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }

        auth = Firebase.auth

        binding.LoginButton.setOnClickListener {
            loginWithEmailAndPassword()
        }

        // Initialize Credential Manager - Commenting out as it appears unused.  Remove if not needed.
        // credentialManager = CredentialManager.create(this)

        // Initialize the ActivityResultLauncher for Google Sign-In
        signInIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    if (account != null) {
                        firebaseAuthWithGoogle(account.idToken!!)
                    } else {
                        Log.d(TAG, "Google Sign-In failed: No account found")
                        Toast.makeText(
                            this, "Google Sign-In failed: No account found", Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: ApiException) {
                    // **Improved Error Handling (crucial):**
                    val statusCode = e.statusCode
                    val message = e.message
                    Log.e(TAG, "Google Sign-In failed (code $statusCode): $message")
                    val errorMessage = when (statusCode) {
                        10 -> "Google Sign-in failed. Please check your internet connection and try again." // More specific
                        // Add other status code handling as needed (see GoogleSignInStatusCodes)
                        else -> "Google Sign-in failed: ${e.message}" // Include the original message
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

        binding.btngSignup.setOnClickListener {
            // Use new sign-in method
            signInWithGoogle()
        }
    }

    private fun checkIfUserIsLoggedIn() {
        val auth = FirebaseAuth.getInstance() // Use the existing 'auth' instance instead?
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // auth.signOut() // **Remove this signOut() here!**
            // It's causing a sign-out when the user ISN'T logged in.
            // Only sign out on explicit logout action.
        }
    }

    // Password Visibility Toggle (seems fine)
    @SuppressLint("ClickableViewAccessibility")
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
                        passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
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


    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) //Make sure you have this resource
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInIntent = googleSignInClient.signInIntent
        signInIntentLauncher.launch(signInIntent)
    }

    // Email/Password Login (seems fine)
    private fun loginWithEmailAndPassword() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                val user = auth.currentUser
                startActivity(Intent(this, MainActivity::class.java))
                finish() // Optional: Finish the LoginActivity
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(
                    baseContext, "Authentication failed.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithCredential:success")
                val user = auth.currentUser
                // Navigate to your main activity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                // **Improved Error Handling:**
                Log.w(TAG, "signInWithCredential:failure", task.exception)
                Toast.makeText(this, "Firebase authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}