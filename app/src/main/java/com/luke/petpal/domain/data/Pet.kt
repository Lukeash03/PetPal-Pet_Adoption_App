package com.luke.petpal.domain.data

data class Pet(
    val id: Int,
    val name: String?,
    val species: String?,
    val breed: String? = null,
    val age: Int?
)
