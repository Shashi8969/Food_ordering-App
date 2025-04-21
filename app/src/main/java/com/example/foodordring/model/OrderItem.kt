package com.example.foodordring.model

import android.os.Parcel
import android.os.Parcelable
class OrderItem : Parcelable {
    val foodName: String?
    val foodPrice: Double?
    val quantity: Int?
    val foodImage: String?
    val foodDiscountPrice: Double?
    val foodDescription: String?
    val foodIngredients: String?
    var itemId: String?
    var address: String?
    var phoneNumber: String?
    var orderAccepted: Boolean?
    var totalAmount: Double?
    var paymentMethod: String?
    var paymentReceived: Boolean?
    var currentTime: Long

    constructor(
        foodName: String? = null,
        foodPrice: Double? = null,
        quantity: Int? = null,
        foodImage: String? = null,
        foodDiscountPrice: Double? = null,
        foodDescription: String? = null,
        foodIngredients: String? = null,
        itemId: String? = null,
        address: String? = null,
        phoneNumber: String? = null,
        orderAccepted: Boolean? = false,
        totalAmount: Double? = null,
        paymentMethod: String? = null,
        paymentReceived: Boolean? = false,
        currentTime: Long = 0
    ) {
        this.foodName = foodName
        this.foodPrice = foodPrice
        this.quantity = quantity
        this.foodImage = foodImage
        this.foodDiscountPrice = foodDiscountPrice
        this.foodDescription = foodDescription
        this.foodIngredients = foodIngredients
        this.itemId = itemId
        this.address = address
        this.phoneNumber = phoneNumber
        this.orderAccepted = orderAccepted
        this.totalAmount = totalAmount
        this.paymentMethod = paymentMethod
        this.paymentReceived = paymentReceived
        this.currentTime = currentTime
    }

    constructor(parcel: Parcel) : this(
        foodName = parcel.readString(),
        foodPrice = parcel.readValue(Double::class.java.classLoader) as? Double,
        quantity = parcel.readValue(Int::class.java.classLoader) as? Int,
        foodImage = parcel.readString(),
        foodDiscountPrice = parcel.readValue(Double::class.java.classLoader) as? Double,
        foodDescription = parcel.readString(),
        foodIngredients = parcel.readString(),
        itemId = parcel.readString(),
        address = parcel.readString(),
        phoneNumber = parcel.readString(),
        orderAccepted = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        totalAmount = parcel.readValue(Double::class.java.classLoader) as? Double,
        paymentMethod = parcel.readString(),
        paymentReceived = parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        currentTime = parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(foodName)
        parcel.writeValue(foodPrice)
        parcel.writeValue(quantity)
        parcel.writeString(foodImage)
        parcel.writeValue(foodDiscountPrice)
        parcel.writeString(foodDescription)
        parcel.writeString(foodIngredients)
        parcel.writeString(itemId)
        parcel.writeString(address)
        parcel.writeString(phoneNumber)
        parcel.writeValue(orderAccepted)
        parcel.writeValue(totalAmount)
        parcel.writeString(paymentMethod)
        parcel.writeValue(paymentReceived)
        parcel.writeLong(currentTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderItem> {
        override fun createFromParcel(parcel: Parcel): OrderItem {
            return OrderItem(parcel)
        }

        override fun newArray(size: Int): Array<OrderItem?> {
            return arrayOfNulls(size)
        }
    }
}