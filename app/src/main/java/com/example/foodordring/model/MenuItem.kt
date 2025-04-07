package com.example.foodordring.model

data class MenuItem(
    val foodName: String?=null,
    val foodPrice: String?=null,
    val foodImage: String?=null,
    val foodDescription: String?=null,
    val foodIngredients: String?=null,
    val foodRating: Double?=null,
    val foodDiscountPrice: String?=null,
    var isFavorite: Boolean = false,
    var itemId: String? = null
    )
