package com.example.gradka.domain

import javax.inject.Inject

class ReverseGeocodeUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(lat: Double, lon: Double): String {
        return repository.reverseGeocode(lat, lon)
    }
}
