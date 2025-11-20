package com.depi.bookdiscovery.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Epub(
    @SerializedName("acsTokenLink")
    var acsTokenLink: String?,
    @SerializedName("downloadLink")
    var downloadLink: String?,
    @SerializedName("isAvailable")
    var isAvailable: Boolean?
) : Parcelable