package com.example.foodordring
import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodordring.databinding.ActivityLocationPickerBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class LocationPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityLocationPickerBinding // Replace with your binding
    private lateinit var map: GoogleMap
    private var pickedLatLng: LatLng? = null
    private var pickedAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationPickerBinding.inflate(layoutInflater) // Replace with your binding
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(binding.map.id) as SupportMapFragment // Replace with your map's ID
        mapFragment.getMapAsync(this)

        binding.pickLocationButton.setOnClickListener {
            if (pickedLatLng != null && pickedAddress != null) {
                val resultIntent = Intent()
                resultIntent.putExtra("picked_address", pickedAddress)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Please pick a location on the map.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Set default location (e.g., your city's center)
        val defaultLocation = LatLng(28.79785, 77.54083)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))

        map.setOnMapClickListener { latLng ->
            map.clear() // Remove previous markers

            pickedLatLng = latLng
            // perform geocoding here to get the address from latLng
            getAddressFromLatLng(latLng)?.let {
                pickedAddress = it
                map.addMarker(MarkerOptions().position(latLng).title(it))
                return@setOnMapClickListener
            }
            // For this example, we'll use a placeholder
            pickedAddress = "Unknown Address"  // Replace with actual geocoding

            map.addMarker(MarkerOptions().position(latLng).title("Picked Location"))
        }
    }

    //  In a real app, implement Geocoding here using Geocoder:

      private fun getAddressFromLatLng(latLng: LatLng): String? {
          return try {
              val geocoder = Geocoder(this, Locale.getDefault())
              val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
              addresses?.get(0)?.getAddressLine(0) // Get the first address line
          } catch (e: Exception) {
              e.printStackTrace()
              null
          }
      }
}