import android.os.Parcel
import android.os.Parcelable

data class OrderItem(
    val foodName: String? = null,
    val foodPrice: Double? = null,
    val quantity: Int? = null,
    val foodImage: String? = null // This is what we need to make sure is being passed!
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(foodName)
        parcel.writeValue(foodPrice)
        parcel.writeValue(quantity)
        parcel.writeString(foodImage)
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