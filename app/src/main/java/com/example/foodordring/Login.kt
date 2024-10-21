package com.example.foodordring

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodordring.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email : String
    private lateinit var password : String
    private lateinit var database: DatabaseReference

    private val binding:ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.btnCreate.setOnClickListener{
            val i = Intent(this, MainActivity::class.java)
            if(binding.etEmail.text.toString() =="test@gmail.com" && binding.etPassword.text.toString() =="password"){
                startActivity(i)
            }
            else{
                binding.etEmail.error = "Invalid Email"
                binding.etPassword.error = "Invalid Password"
                Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show()
            }

        }
        binding.txtCreate.setOnClickListener{
            val i = Intent(this, Signup::class.java)
            startActivity(i)
        }

    }
}