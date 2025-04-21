package com.example.foodordring

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.foodordring.StringHandling.formatAddressEnhanced
import com.example.foodordring.StringHandling.formatName
import com.example.foodordring.databinding.ActivityPayOutBinding
import com.example.foodordring.model.OrderItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Locale

class PayOutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayOutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String
    private var totalAmount: Double = 0.0
    private var orderItems: ArrayList<OrderItem> = ArrayList()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private val geocoder: Geocoder by lazy { Geocoder(this, Locale.getDefault()) }
    private val locationPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val address = data.getStringExtra("picked_address")
                    if (address != null) {
                        binding.addressEditText.setText(address)
                    }
                }
            }
        }
    private lateinit var databaseReference: DatabaseReference
    private lateinit var orderId: String
    private var selectedPaymentMethod: String = "Cash on Delivery"  // Default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient =
            com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this)
        setupLocationIconClickListener()
        setupPermissions()
        setupMapIconClickListener()
        databaseReference = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()

        orderItems = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("orderItems", OrderItem::class.java) ?: ArrayList()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("orderItems") ?: ArrayList()
        }
        totalAmount = intent.getDoubleExtra("discountedTotalPrice", 0.0)
        Log.d("PayOutActivity", "Received orderItems: $orderItems")
        Log.d("PayOutActivity", "Received discountedTotalPrice: $totalAmount")

        setUserData()
        binding.totalAmountEditText.text =
            android.text.Editable.Factory.getInstance().newEditable("%.2f".format(totalAmount))
        binding.totalAmountEditText.isEnabled = false

        setupPaymentMethodDropdown() // Initialize dropdown

        val bottomSheetFragment = CongratsBottomSheet()
        binding.placeOrderButton.setOnClickListener {
            name = binding.nameEditText.text.toString()
            address = binding.addressEditText.text.toString()
            phone = binding.phoneEditText.text.toString()
            name.formatName()
            address.formatAddressEnhanced()

            if (validateFields()) {
                if (selectedPaymentMethod == "Paytm") {
                    initiatePaytmPayment()  //  You'll need to implement this (see below)
                } else {
                    placeOrder(bottomSheetFragment) // COD
                }
            }
        }

        binding.toolbar.setOnClickListener {
            finish()
        }
    }

    private fun validateFields(): Boolean {
        if (binding.nameEditText.text.isNullOrEmpty()) {
            binding.nameEditText.error = "Please enter your name"
            binding.nameEditText.requestFocus()
            return false
        }
        if (binding.addressEditText.text.isNullOrEmpty()) {
            binding.addressEditText.error = "Please enter your address"
            binding.addressEditText.requestFocus()
            return false
        }
        if (binding.phoneEditText.text.isNullOrEmpty()) {
            binding.phoneEditText.error = "Please enter your phone number"
            binding.phoneEditText.requestFocus()
            return false
        }
        val phone = binding.phoneEditText.text.toString()
        if (!android.util.Patterns.PHONE.matcher(phone).matches() ||
            !phone.matches("^[6-9]\\d{9}$".toRegex())
        ) { //Basic Indian phone number check
            binding.phoneEditText.error = "Invalid Indian Phone Number"
            binding.phoneEditText.requestFocus()
            return false
        }
        return true
    }

    private fun placeOrder(bottomSheetFragment: CongratsBottomSheet) {
        Toast.makeText(this, "Placing order...", Toast.LENGTH_SHORT).show()
        createOrderId()
        bottomSheetFragment.show(supportFragmentManager, "Test")
    }


    // *** Payment Method Dropdown ***
    private fun setupPaymentMethodDropdown() {
        val paymentMethods = arrayOf("Cash on Delivery", "Paytm")
        val adapter = ArrayAdapter(this, R.layout.list_item, paymentMethods) // Create list_item.xml (see below)
        (binding.paymentMethodLayout.editText as? android.widget.AutoCompleteTextView)?.setAdapter(adapter)

        (binding.paymentMethodLayout.editText as? android.widget.AutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            selectedPaymentMethod = paymentMethods[position]
            Log.d("PayOutActivity", "Selected payment method: $selectedPaymentMethod")
        }
        // Set a default selection (optional, but good practice)
        (binding.paymentMethodLayout.editText as? android.widget.AutoCompleteTextView)?.setText(paymentMethods[0], false) // COD
        selectedPaymentMethod = paymentMethods[0]
    }

    // Create list_item.xml in res/layout:
    /*
    <?xml version="1.0" encoding="utf-8"?>
    <TextView xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="16dp"
        android:textAppearance="?attr/textAppearanceSubtitle1"
        android:textColor="?android:attr/textColorPrimary" />
    */


    // *** Paytm Integration *** (Placeholder - Needs Actual SDK Implementation)
    private fun initiatePaytmPayment() {
        Toast.makeText(this, "Redirecting to Paytm...", Toast.LENGTH_SHORT).show()
        Log.d("PayOutActivity", "Initiating Paytm payment (placeholder)")

        // TODO:  Replace this with your actual Paytm SDK integration

        //  **Example of launching an Activity (replace with Paytm SDK call):**
        // val intent = Intent(this, PaytmPaymentActivity::class.java)
        //  intent.putExtra("amount", totalAmount) // Pass relevant data to the Paytm activity
        //  startActivityForResult(intent, PAYTM_REQUEST_CODE)  // Use startActivityForResult

        // For now, we'll just simulate a successful payment and place the order
        // **Remove this when integrating the actual Paytm SDK!**
        //Simulate successful Paytm payment
        placeOrderAfterPaytm()
    }

    //Simulate successful Paytm payment -  **Remove when integrating the actual Paytm SDK!**
    private fun placeOrderAfterPaytm() {
        Log.d("PayOutActivity", "Simulated successful Paytm payment, placing order.")
        val bottomSheetFragment = CongratsBottomSheet()
        placeOrder(bottomSheetFragment)
    }

    // *** The rest of your original code (setUserData, createOrderId, addOrderToHistory, removeItemFromCart, location functions) remains largely the same ***
    // **However**, you'll

    // 1. Need to modify `createOrderId` slightly to include payment method.
    // 2. Need to adapt it to be called from both direct COD and successful Paytm scenarios.

    private fun setUserData() {
        val user = auth.currentUser
        if (user != null) {
            userId = user.uid
            val userReference = databaseReference.child("users").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userName = snapshot.child("name").getValue(String::class.java) ?: ""
                        val userAddress =
                            snapshot.child("address").getValue(String::class.java) ?: ""
                        val userPhone = snapshot.child("phone").getValue(String::class.java) ?: ""


                        binding.apply {
                            nameEditText.setText(userName)
                            addressEditText.setText(userAddress)
                            phoneEditText.setText(userPhone)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("PayOutActivity", "Database error: ${error.message}")
                    // Handle the error appropriately, e.g., show a user-friendly message
                }
            })
        } else {
            Log.w("PayOutActivity", "User not logged in.")
            // Handle the case where the user is not logged in, e.g., redirect to login
        }

    }

    private fun createOrderId() {
        userId = auth.currentUser?.uid ?: ""
        val dateTime = LocalDate.now(ZoneId.of("Asia/Kolkata")).toString()
        val time = System.currentTimeMillis().toString()
        orderId = "$userId$time"

        // Create a map to hold the order details
        val orderDetails = hashMapOf(
            "userId" to userId,
            "orderTime" to LocalTime.now(ZoneId.of("Asia/Kolkata")).toString(),
            "orderDate" to dateTime,
            "timestamp" to System.currentTimeMillis(),
            "orderId" to orderId,
            "foodItems" to orderItems.map { item ->
                hashMapOf(
                    "name" to item.foodName,
                    "quantity" to item.quantity,
                    "price" to item.foodPrice,
                    "image" to item.foodImage.toString()
                )
            },
            "totalAmount" to totalAmount,
            "paymentMethod" to selectedPaymentMethod, // Include payment method
            "deliveryDetails" to hashMapOf(
                "name" to name,
                "address" to address,
                "phone" to phone
            )
        )


        // Push the order details to the database under a unique order ID
        databaseReference.child("orders").child(orderId).setValue(orderDetails)
            .addOnSuccessListener {
                Log.d("PayOutActivity", "Order created successfully with ID: $orderId")
                // Order was successfully created, you can now proceed to show the bottom sheet
                removeItemFromCart()
                addOrderToHistory(orderDetails)
            }
            .addOnFailureListener { e ->
                Log.e("PayOutActivity", "Error creating order: ${e.message}", e)
                // Handle the error, e.g., show an error message to the user
            }
    }

    private fun addOrderToHistory(orderDetails: Any) {
        databaseReference.child("users").child(userId).child("orderHistory").child(orderId)
            .setValue(orderDetails)
            .addOnSuccessListener {
                Log.d("PayOutActivity", "Order ID added to history successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("PayOutActivity", "Error adding order ID to history: ${e.message}", e)

            }
    }

    private fun removeItemFromCart() {
        val cartReference = databaseReference.child("users").child(userId).child("CartItems")
        cartReference.removeValue()
    }

    private fun setupPermissions() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                ) {
                    getCurrentLocation()
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupLocationIconClickListener() {
        // Use binding to access the TextInputLayout's endIcon
        binding.addressInputLayout.setEndIconOnClickListener {
            checkLocationPermissionsAndGetLocation()
        }
    }

    // Add this function
    private fun setupMapIconClickListener() {
        binding.addressInputLayout.setStartIconOnClickListener {  // Assuming the map icon is the start icon
            val intent = Intent(this, LocationPickerActivity::class.java) // Replace with your activity
            locationPickerLauncher.launch(intent)
        }
    }

    private fun checkLocationPermissionsAndGetLocation() {
        if (checkLocationPermission()) {
            getCurrentLocation()
        } else {
            requestLocationPermissions()
        }
    }

    private fun requestLocationPermissions() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
    }

    private fun checkLocationPermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (!checkLocationPermission()) {
            requestLocationPermissions()
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    getGeocodedAddress(it)
                } ?: run {
                    Toast.makeText(this, "Could not get last known location", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Location", "Error getting location: ${e.message}")
                Toast.makeText(this, "Error getting location", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getGeocodedAddress(location: Location) {
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val addressText = address.getAddressLine(0)
                runOnUiThread {
                    // Use binding to access the EditText
                    binding.addressEditText.setText(addressText)
                }
            } else {
                runOnUiThread {
                    binding.addressEditText.setText("No address found for this location")
                }
            }
        } catch (e: Exception) {
            Log.e("Geocoding", "Geocoding error: ${e.message}")
            runOnUiThread {
                binding.addressEditText.setText("Geocoding error")
            }
        }
    }
}