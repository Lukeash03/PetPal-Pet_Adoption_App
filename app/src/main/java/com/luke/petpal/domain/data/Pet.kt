package com.luke.petpal.domain.data

import android.net.Uri

data class Pet(
    val name: String? = null,
    val species: String? = null,
    val breed: String? = null,
    val gender: String? = null,
    val age: Int? = null,
    val weight: Int? = null,
    val color: String? = null,
    var photos: List<String>? = null
)
