package com.example.foodordring

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodordring.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Signup : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var name : String
    private lateinit var email : String
    private lateinit var password : String
    private lateinit var database: DatabaseReference
//    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivitySignupBinding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //Initialising Firebase Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")


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
            name = binding.etName.text.toString()
            email = binding.etEmailSignup.text.toString().trim()
            password = binding.etPasswordSignup.text.toString().trim()

            if(name.isEmpty() || email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"Fill all fields",Toast.LENGTH_SHORT).show()
            }
            val i = Intent(this, ChooseLocation::class.java)
            startActivity(i)
        }
    }
    private fun createAccount(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
            task ->
            if(task.isSuccessful){
                Toast.makeText(this,"Account Created Successfully",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,Login::class.java))
            }
            else{
                Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
                Log.d("Account","createAccount: Failure",task.exception)
            }
        }
    }
    private  fun saveUserData(){
        //Retrive data from the EditText
        name = binding.etName.text.toString()
        password = binding.etPasswordSignup.text.toString().trim()
        email = binding.etEmailSignup.text.toString().trim()


    }
}