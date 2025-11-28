package com.depi.bookdiscovery.data.model.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SaleInfo(
    @SerializedName("buyLink")
    var buyLink: String?,
    @SerializedName("country")
    var country: String?,
    @SerializedName("listPrice")
    var listPrice: ListPrice?,
    @SerializedName("offers")
    var offers: List<Offer>?,
    @SerializedName("retailPrice")
    var retailPrice: RetailPriceX?
) : Parcelable