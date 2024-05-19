package com.pukimen.social.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryModel(
    val photoUrl: String?,
    val createdAt: String?,
    val name: String?,
    val description: String?,
    val lon: String?,
    val id: String?,
    val lat: String?
): Parcelable