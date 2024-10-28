package com.example.foodordring

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foodordring.databinding.ActivityPayOutBinding

class PayOutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayOutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bottomSheetFragment = CongratsBottomSheet()
        binding.placeorderbutton.setOnClickListener {
            bottomSheetFragment.show(supportFragmentManager, "Test")
        }
    }
}