package com.depi.bookdiscovery.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PanelizationSummary(
    @SerializedName("containsEpubBubbles")
    var containsEpubBubbles: Boolean?,
    @SerializedName("containsImageBubbles")
    var containsImageBubbles: Boolean?
) : Parcelable