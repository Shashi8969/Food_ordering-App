package com.example.foodordring.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.foodordring.MenuBottomSheetFragment
import com.example.foodordring.R
import com.example.foodordring.adaptar.PopularAdapter
import com.example.foodordring.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.viewAllMenu.setOnClickListener {
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager,"Test")
        }
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = ArrayList<SlideModel>() // Create image list
        imageList.add(SlideModel(R.drawable.banner1 , ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner2 , ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner3 , ScaleTypes.FIT))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(position: Int) {
                // You can listen here.
                val itemPosition = imageList[position]
                val itemMessage = "Selected Image $position"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
            }
        })
        val item = listOf("Pizza","Burgers","sandwich","momo")
        val Price = listOf("$5","$6","$7","$8")
        val populerFoodImages = listOf(R.drawable.menu1,R.drawable.menu2,R.drawable.menu3,R.drawable.menu4)
        val adapter = PopularAdapter(item,populerFoodImages,Price)
        binding.popularRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.popularRecyclerView.adapter = adapter

    }

    companion object {

        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {

            }
    }
}