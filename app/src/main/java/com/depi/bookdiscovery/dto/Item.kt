package com.depi.bookdiscovery.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    @SerializedName("accessInfo")
    var accessInfo: AccessInfo?,
    @SerializedName("etag")
    var etag: String?,
    @SerializedName("id")
    var id: String?,
    @SerializedName("kind")
    var kind: String?,
    @SerializedName("saleInfo")
    var saleInfo: SaleInfo?,
    @SerializedName("searchInfo")
    var searchInfo: SearchInfo?,
    @SerializedName("selfLink")
    var selfLink: String?,
    @SerializedName("volumeInfo")
    var volumeInfo: VolumeInfo?
): Parcelable