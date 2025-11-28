package com.depi.bookdiscovery.data.model.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageLinks(
    @SerializedName("smallThumbnail")
    var smallThumbnail: String?,
    @SerializedName("thumbnail")
    var thumbnail: String?
) : Parcelable