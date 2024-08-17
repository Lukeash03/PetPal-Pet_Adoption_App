package com.luke.petpal.domain.data

import android.net.Uri

data class Pet(
    val name: String?,
    val species: String,
    val breed: String,
    val gender: String,
    val age: Int,
    val weight: Int,
    val color: String,
    val photos: List<Uri>?
)
