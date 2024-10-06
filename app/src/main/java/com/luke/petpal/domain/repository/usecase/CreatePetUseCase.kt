package com.luke.petpal.domain.repository.usecase

import com.luke.petpal.domain.data.Pet
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CreatePetUseCase {
    fun execute(
        petName: String,
        petSpecies: String,
        petBreed: String,
        petGender: String,
        formattedDate: String,
        petWeight: String,
        petColor: String,
        vaccinationStatus: Boolean,
        petDescription: String,
        photos: List<String>?
    ): Pet {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val localDate = LocalDate.parse(formattedDate, formatter)
        val dobTimestamp: Long = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        // Handle weight conversion safely
        val petWeightInt = petWeight.toIntOrNull() ?: 0

        // Create and return the Pet object
        return Pet(
            name = petName,
            species = petSpecies,
            breed = petBreed,
            gender = petGender,
            dob = dobTimestamp,
            weight = petWeightInt,
            color = petColor,
            photos = photos,
            vaccinationStatus = vaccinationStatus,
            description = petDescription,
            publishDate = LocalDate.now().toEpochDay()
        )
    }
}