package com.example.gradka.domain

import com.example.gradka.data.Address
import com.example.gradka.data.Order
import kotlinx.coroutines.flow.Flow

interface GradkaRepository {
    fun placeOrder(cart: Map<String, Int>, addressId: String)
    fun getOrder(): Flow<List<Order>>
    fun getAddresses(): Flow<List<Address>>
    fun addAddress(address: Address): Unit
    fun deleteAddress(addressId: String)
    fun setPrimaryAddress(addressId: String): Unit
    suspend fun suggestAddresses(query: String): List<AddressSuggestion>
    suspend fun reverseGeocode(lat: Double, lon: Double): String
    suspend fun getSession(): UserSession?
    suspend fun saveSession(phone: String, name: String)
    suspend fun clearSession()
}