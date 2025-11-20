package com.depi.bookdiscovery.data.model.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RetailPrice(
    @SerializedName("amountInMicros")
    var amountInMicros: Long?,
    @SerializedName("currencyCode")
    var currencyCode: String?
) : Parcelable