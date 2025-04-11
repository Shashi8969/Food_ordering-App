package com.example.foodordring.model

data class CartItems(
    val foodName: String?=null,
    val foodPrice: String?=null,
    val foodImage: String?=null,
    val foodDescription: String?=null,
    val foodIngredients: String?=null,
    val foodDiscountPrice: String?=null,
    var quantity: Int?=null,
    var itemId: String? = null
)
