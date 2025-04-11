package com.example.foodordring.model
import android.os.Parcel
import android.os.Parcelable

data class OrderItem(
    val foodName: String? = null,
    val foodPrice: Double = 0.0,
    val quantity: Int = 0,
    val foodId: String? = null,
    val foodImage: String? = null,
    val foodDescription: String? = null,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readDouble(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(foodName)
        parcel.writeDouble(foodPrice)
        parcel.writeInt(quantity)
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