package com.luke.petpal.domain.data

data class VaccinationEntry(
    val petId: String? = null,
    val vaccineName: String,
    val doctorName: String?,
    val date: Long,
    val reminder: Boolean,
    val reminderDate: Long? = null
)
