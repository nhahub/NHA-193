package com.depi.bookdiscovery.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterOption(
    val label: String,
    val value: String
) : Parcelable