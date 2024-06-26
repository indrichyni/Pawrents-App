package com.android.pawrents.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Knowledge(
    val id: Int,
    val title: String,
    val resourceId: Int
): Parcelable
