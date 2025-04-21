package com.example.foodordring.model

data class RecentBuyModel(
    val name: String? = null,
    val price: Double = 0.0,
    val quantity: Int = 0,
    val foodId: String? = null,
    val image: String? = null,
    val totalAmount: Double = 0.0
)
