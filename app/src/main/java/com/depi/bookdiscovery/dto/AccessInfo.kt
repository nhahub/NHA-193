package com.depi.bookdiscovery.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccessInfo(
    @SerializedName("accessViewStatus")
    var accessViewStatus: String?,
    @SerializedName("country")
    var country: String?,
    @SerializedName("epub")
    var epub: Epub?,
    @SerializedName("pdf")
    var pdf: Pdf?
) : Parcelable