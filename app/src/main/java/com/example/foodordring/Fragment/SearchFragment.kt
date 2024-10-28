package com.example.foodordring.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodordring.R
import com.example.foodordring.adaptar.MenuAdapter
import com.example.foodordring.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {

    private  lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private val orignalMenuFoodName :List<String> = listOf("Pizza","Burgers","sandwich","momo")
    private val orignalMenuItemPrice :List<String> = listOf("$5","$6","$7","$8")
    private val orignalMenuItemImage :List<Int> = listOf(R.drawable.menu1,R.drawable.menu2,R.drawable.menu3,R.drawable.menu4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private val filteredMenuFoodName = mutableListOf<String>()
    private val filteredMenuItemPrice = mutableListOf<String>()
    private val filteredMenuItemImage = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater,container,false)
        adapter = MenuAdapter(filteredMenuFoodName,filteredMenuItemPrice,filteredMenuItemImage,requireContext())
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adapter


        //setup for search view
        setUpSearchView()

        //show all menu items
        showAllMenu()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun showAllMenu() {
        filteredMenuFoodName.clear()
        filteredMenuItemPrice.clear()
        filteredMenuItemImage.clear()

        filteredMenuFoodName.addAll(orignalMenuFoodName)
        filteredMenuItemPrice.addAll(orignalMenuItemPrice)
        filteredMenuItemImage.addAll(orignalMenuItemImage)

        adapter.notifyDataSetChanged()
    }


    private fun setUpSearchView(){
       binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
           android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterMenuItems(query)
                return true
            }

           override fun onQueryTextChange(newText: String?): Boolean {
               filterMenuItems(newText)
               return true
           }
        })
    }

    private fun filterMenuItems(query: String?) {
        filteredMenuFoodName.clear()
        filteredMenuItemPrice.clear()
        filteredMenuItemImage.clear()

        orignalMenuFoodName.forEachIndexed{ index, foodName ->
            if (foodName.contains(query.toString(),ignoreCase = true))
            {
                filteredMenuFoodName.add(foodName)
                filteredMenuItemPrice.add(orignalMenuItemPrice[index])
                filteredMenuItemImage.add(orignalMenuItemImage[index])
            }
        }
        adapter.notifyDataSetChanged()
    }

    companion object {

    }
}