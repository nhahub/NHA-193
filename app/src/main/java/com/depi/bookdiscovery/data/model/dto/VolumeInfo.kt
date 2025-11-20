package com.depi.bookdiscovery.data.model.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class VolumeInfo(
    @SerializedName("allowAnonLogging")
    var allowAnonLogging: Boolean?,
    @SerializedName("authors")
    var authors: List<String?>?,
    @SerializedName("canonicalVolumeLink")
    var canonicalVolumeLink: String?,
    @SerializedName("contentVersion")
    var contentVersion: String?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("imageLinks")
    var imageLinks: ImageLinks?,
    @SerializedName("infoLink")
    var infoLink: String?,
    @SerializedName("maturityRating")
    var maturityRating: String?,
    @SerializedName("panelizationSummary")
    var panelizationSummary: PanelizationSummary?,
    @SerializedName("previewLink")
    var previewLink: String?,
    @SerializedName("publishedDate")
    var publishedDate: String?,
    @SerializedName("publisher")
    var publisher: String?,
    @SerializedName("readingModes")
    var readingModes: ReadingModes?,
    @SerializedName("subtitle")
    var subtitle: String?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("industryIdentifiers")
    var industryIdentifiers: List<IndustryIdentifier>?,
    @SerializedName("averageRating")
    var averageRating: Float?,
    @SerializedName("ratingsCount")
    var ratingsCount: Int?
) : Parcelable