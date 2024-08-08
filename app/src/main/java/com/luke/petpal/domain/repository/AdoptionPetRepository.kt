package com.luke.petpal.domain.repository

import com.luke.petpal.domain.data.Pet

class AdoptionPetRepository {

    fun getAllData(): List<Pet> {
        return listOf(
            Pet(
                id = 0,
                name = "John",
                species = "Dog",
                age = 2
            ),
            Pet(
                id = 1,
                name = "Max",
                species = "Dog",
                age = 3
            ),
            Pet(
                id = 2,
                name = "Tofu",
                species = "Cat",
                age = 2
            ),
            Pet(
                id = 3,
                name = "Loki",
                species = "Cat",
                age = 4
            ),
            Pet(
                id = 4,
                name = "Tommy",
                species = "Dog",
                age = 5
            ),
        )
    }
}