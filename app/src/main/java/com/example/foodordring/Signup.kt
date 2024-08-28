package com.example.foodordring

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodordring.databinding.ActivityLoginBinding
import com.example.foodordring.databinding.ActivitySignupBinding

class Signup : AppCompatActivity() {
    private val binding: ActivitySignupBinding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
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
        binding.txtAlready.setOnClickListener{
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
        }
        binding.btnCreate.setOnClickListener{
            val i = Intent(this, ChooseLocation::class.java)
            startActivity(i)
        }
    }
}