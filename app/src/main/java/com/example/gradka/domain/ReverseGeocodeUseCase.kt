package com.example.gradka.domain

class ReverseGeocodeUseCase(private val repository: GradkaRepository) {
    suspend operator fun invoke(lat: Double, lon: Double): String {
        return repository.reverseGeocode(lat, lon)
    }
}
