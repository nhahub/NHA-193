package com.depi.bookdiscovery.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListPriceX(
    @SerializedName("amountInMicros")
    var amountInMicros: Long?,
    @SerializedName("currencyCode")
    var currencyCode: String?
) : Parcelable