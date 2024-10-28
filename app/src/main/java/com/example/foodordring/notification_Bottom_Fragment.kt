package com.example.foodordring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodordring.adaptar.notificationAdapter
import com.example.foodordring.databinding.FragmentNotificationBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class notification_Bottom_Fragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNotificationBottomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentNotificationBottomBinding.inflate(layoutInflater,container,false)
        val notfications = listOf("Your order has been accepted","Your order has been canceled","Your order has been accepted")
        val notificationImages = listOf(R.drawable.congrats,R.drawable.sademoji,R.drawable.truck)

        val adapter = notificationAdapter(ArrayList(notfications),ArrayList(notificationImages))
        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationRecyclerView.adapter = adapter
        return binding.root
    }

    companion object {

    }
}