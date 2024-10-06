package com.luke.petpal.domain.data

data class Pet(
    val petId: String? = null,
    val userId: String? = null,
    val name: String? = null,
    val species: String? = null,
    val breed: String? = null,
    val gender: String = "",
    val dob: Long? = null,
    val weight: Int? = null,
    val color: String? = null,
    var photos: List<String>? = null,
    val vaccinationStatus: Boolean = false,
    val publishDate: Long? = null,
    val description: String? = null
)
