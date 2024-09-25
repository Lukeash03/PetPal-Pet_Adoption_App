package com.luke.petpal.domain.data

data class User(
    var uid: String = "",
    var email: String = "",
    var username: String = "",
    var profileImageUrl: String = "",
    var location: Location? = null
)

data class Location(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)
