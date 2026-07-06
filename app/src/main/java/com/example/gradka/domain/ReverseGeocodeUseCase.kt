package com.example.gradka.domain

import javax.inject.Inject

/** Use Case для обратного геокодирования: преобразует координаты в текстовый адрес через Yandex MapKit. */
class ReverseGeocodeUseCase @Inject constructor(private val repository: GradkaRepository) {
    suspend operator fun invoke(lat: Double, lon: Double): String {
        return repository.reverseGeocode(lat, lon)
    }
}
