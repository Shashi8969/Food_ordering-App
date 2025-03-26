package com.example.foodordring.model

data class CartItems(
    val foodName: String? = null,
    val foodPrice: String? = null,
    val foodImage: String? = null,
    val foodDescription: String? = null,
    val foodIngredient: String? = null,
    val quantity: Int? = null
)
