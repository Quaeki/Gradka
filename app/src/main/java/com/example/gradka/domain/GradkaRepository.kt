package com.example.gradka.domain

import com.example.gradka.data.Address
import com.example.gradka.data.Order
import com.example.gradka.data.PaymentMethod
import com.example.gradka.data.Subscription
import kotlinx.coroutines.flow.Flow

interface GradkaRepository {
    suspend fun placeOrder(cart: Map<String, Int>, addressId: String)
    fun getOrder(): Flow<List<Order>>
    fun getSubscriptions(): Flow<List<Subscription>>
    suspend fun addSubscription(subscription: Subscription)
    suspend fun updateSubscription(subscription: Subscription)
    suspend fun deleteSubscription(subscriptionId: String)
    fun getPaymentMethods(): Flow<List<PaymentMethod>>
    suspend fun addPaymentMethod(paymentMethod: PaymentMethod)
    suspend fun deletePaymentMethod(paymentMethodId: String)
    fun getAddresses(): Flow<List<Address>>
    fun addAddress(address: Address): Unit
    fun deleteAddress(addressId: String)
    fun setPrimaryAddress(addressId: String): Unit
    suspend fun suggestAddresses(query: String): List<AddressSuggestion>
    suspend fun reverseGeocode(lat: Double, lon: Double): String
    suspend fun getSession(): UserSession?
    suspend fun saveSession(phone: String, name: String)
    suspend fun clearSession()
    suspend fun sendOtp(phone: String)
    suspend fun verifyOtp(phone: String, code: String): Boolean
    suspend fun addNote(title: String, content: String)
    suspend fun deleteNote(noteId: Int)
    suspend fun editNote(note: Note)
    fun getAllNotes(): Flow<List<Note>>
}
