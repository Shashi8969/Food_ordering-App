package com.example.foodordring

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.foodordring.databinding.ActivitySignupBinding
import com.example.foodordring.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class Signup : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var database: DatabaseReference
    private var isPasswordVisible = false
    private lateinit var googleSignInClient: GoogleSignInClient


    private val binding: ActivitySignupBinding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Set up password visibility toggle
        setupPasswordVisibilityToggle(binding.etPasswordSignup)

        //Initialising Firebase Database
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        //Initialize Google Signin
        val googleSignInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)



        binding.txtAlready.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        binding.btnCreate.setOnClickListener {
            name = binding.etName.text.toString()
            email = binding.etEmailSignup.text.toString().trim()
            password = binding.etPasswordSignup.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(name, email, password)
            }
        }
        binding.googleButton.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account: GoogleSignInAccount? = task.result
                    val credental = GoogleAuthProvider.getCredential(account?.idToken, null)
                    auth.signInWithCredential(credental).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "SignIn Failed ", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }else{
                Toast.makeText(this, "SignIn Failed ", Toast.LENGTH_SHORT).show()
            }
        }

    private fun createAccount(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                saveUserData()
                startActivity(Intent(this, Login::class.java))
            } else {
                Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show()
                Log.d("Account", "createAccount: Failure", task.exception)
            }
        }
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

    private fun saveUserData() {
        //Retrive data from the EditText
        name = binding.etName.text.toString()
        password = binding.etPasswordSignup.text.toString().trim()
        email = binding.etEmailSignup.text.toString().trim()
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val user = UserModel(name,"", email, "", password,userId)
        database.child("users").child(userId).setValue(user)
    }
}