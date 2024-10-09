package com.luke.petpal.domain.data

data class PersonalPet(
    val petId: String? = null,
    val userId: String? = null,
    val name: String? = null,
    val species: String? = null,
    val breed: String? = null,
    val gender: String = "",
    val dob: Long? = null,
    var photos: List<String>? = null,
    val publishDate: Long? = null,
)
