package com.depi.bookdiscovery.data.model.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchInfo(
    @SerializedName("textSnippet")
    var textSnippet: String?
) : Parcelable