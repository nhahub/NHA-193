package com.depi.bookdiscovery.data.model.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Offer(
    @SerializedName("finskyOfferType")
    var finskyOfferType: Int?,
    @SerializedName("listPrice")
    var listPrice: ListPriceX?,
    @SerializedName("retailPrice")
    var retailPrice: RetailPrice?
) : Parcelable