package com.depi.bookdiscovery.data.model.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReadingModes(
    @SerializedName("image")
    var image: Boolean?,
    @SerializedName("text")
    var text: Boolean?
) : Parcelable