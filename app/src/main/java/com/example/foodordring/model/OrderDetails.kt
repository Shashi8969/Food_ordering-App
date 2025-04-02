package com.example.foodordring.model

import android.os.Parcel
import android.os.Parcelable

class OrderDetails(): Parcelable {
    var userUid: String? = null
    var orderId: String? = null
    var foodName: String? = null
    var foodPrice: String? = null
    var foodDescription: String? = null
    var foodImageUrl: String? = null
    var foodIngredients: String? = null
    var foodQuantity: Int? = null
    var foodTotalPrice: Int? = null
    var orderStatus: String? = null
    var orderTime: String? = null
    var orderDate: String? = null
    var paymentMethod: String? = null
    var paymentStatus: String? = null
    var itemPushKey: String? = null
    var deliveryAddress: String? = null
    var deliveryStatus: String? = null
    var deliveryTime: String? = null
    var deliveryDate: String? = null
    var currentLocation: String? = null
    var currentTime: String? = null

    constructor(parcel: Parcel) : this() {
        userUid = parcel.readString()
        orderId = parcel.readString()
        foodName = parcel.readString()
        foodPrice = parcel.readString()
        foodDescription = parcel.readString()
        foodImageUrl = parcel.readString()
        foodIngredients = parcel.readString()
        foodQuantity = parcel.readValue(Int::class.java.classLoader) as? Int
        foodTotalPrice = parcel.readValue(Int::class.java.classLoader) as? Int
        orderStatus = parcel.readString()
        orderTime = parcel.readString()
        orderDate = parcel.readString()
        paymentMethod = parcel.readString()
        paymentStatus = parcel.readString()
        itemPushKey = parcel.readString()
        deliveryAddress = parcel.readString()
        deliveryStatus = parcel.readString()
        deliveryTime = parcel.readString()
        deliveryDate = parcel.readString()
        currentLocation = parcel.readString()
        currentTime = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userUid)
        parcel.writeString(orderId)
        parcel.writeString(foodName)
        parcel.writeString(foodPrice)
        parcel.writeString(foodDescription)
        parcel.writeString(foodImageUrl)
        parcel.writeString(foodIngredients)
        parcel.writeValue(foodQuantity)
        parcel.writeValue(foodTotalPrice)
        parcel.writeString(orderStatus)
        parcel.writeString(orderTime)
        parcel.writeString(orderDate)
        parcel.writeString(paymentMethod)
        parcel.writeString(paymentStatus)
        parcel.writeString(itemPushKey)
        parcel.writeString(deliveryAddress)
        parcel.writeString(deliveryStatus)
        parcel.writeString(deliveryTime)
        parcel.writeString(deliveryDate)
        parcel.writeString(currentLocation)
        parcel.writeString(currentTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderDetails> {
        override fun createFromParcel(parcel: Parcel): OrderDetails {
            return OrderDetails(parcel)
        }

        override fun newArray(size: Int): Array<OrderDetails?> {
            return arrayOfNulls(size)
        }
    }

}