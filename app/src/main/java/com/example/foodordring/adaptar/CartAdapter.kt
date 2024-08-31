package com.example.foodordring.adaptar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodordring.databinding.CartItemBinding

class CartAdapter(private val cartList: MutableList<String>, private val cartPrice: MutableList<String>, private val CartImage: MutableList<Int>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val itemquntities = IntArray(cartList.size) { 1 }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartList.size

    inner class CartViewHolder (private val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {

            binding.apply {
                cartImage.setImageResource(CartImage[position])
                CartFoodName.text = cartList[position]
                CartItemPrice.text = cartPrice[position]
                quantity.text = itemquntities[position].toString()

                miniusButton.setOnClickListener {
                    if (itemquntities[position] > 1) {
                        itemquntities[position]--
                        quantity.text = itemquntities[position].toString()
                    }
                    }
                plusButton.setOnClickListener {
                    if (itemquntities[position] < 10){
                    itemquntities[position]++
                    quantity.text = itemquntities[position].toString()
                }
                }
                deleteButton.setOnClickListener {
                    cartList.removeAt(position)
                    cartPrice.removeAt(position)
                    CartImage.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, cartList.size)
                }
            }
        }
    }
}