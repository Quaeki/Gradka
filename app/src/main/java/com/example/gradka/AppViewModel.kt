package com.example.gradka

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gradka.data.Address
import com.example.gradka.data.Order
import com.example.gradka.data.PRODUCTS
import com.example.gradka.domain.AddAddressUseCase
import com.example.gradka.domain.DeleteAddressUseCase
import com.example.gradka.domain.GetAddressesUseCase
import com.example.gradka.domain.GetOrderUseCase
import com.example.gradka.domain.GradkaRepository
import com.example.gradka.domain.PlaceOrderUseCase
import com.example.gradka.domain.SetPrimaryAddressUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(
    private val repository: GradkaRepository,
    private val getOrderUseCase: GetOrderUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val getAddressesUseCase: GetAddressesUseCase,
    private val addAddressUseCase: AddAddressUseCase,
    private val setPrimaryAddressUseCase: SetPrimaryAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
) : ViewModel() {
    val cart = mutableStateMapOf<String, Int>()
    var favs by mutableStateOf(setOf<String>())
    var catFilter by mutableStateOf("all")

    val orders: StateFlow<List<Order>> = getOrderUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val addresses: StateFlow<List<Address>> = getAddressesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addToCart(id: String) { cart[id] = (cart[id] ?: 0) + 1 }
    fun subFromCart(id: String) {
        val current = cart[id] ?: 0
        if (current <= 1) cart.remove(id) else cart[id] = current - 1
    }
    fun clearCart() { cart.clear() }
    fun toggleFav(id: String) {
        favs = if (favs.contains(id)) favs - id else favs + id
    }

    fun placeOrder(addressId: String) {
        viewModelScope.launch {
            placeOrderUseCase(cart.toMap(), addressId)
            cart.clear()
        }
    }
    fun addAddress(address: Address) {
        viewModelScope.launch { addAddressUseCase(address) }
    }
    fun setPrimaryAddress(addressId: String) {
        viewModelScope.launch { setPrimaryAddressUseCase(addressId) }
    }
    fun deleteAddress(addressId: String) {
        deleteAddressUseCase(addressId)
    }

    suspend fun suggestAddresses(query: String) = repository.suggestAddresses(query)
    suspend fun reverseGeocode(lat: Double, lon: Double) = repository.reverseGeocode(lat, lon)

    val cartCount: Int get() = cart.values.sum()
    val cartSubtotal: Int get() = cart.entries.sumOf { (id, qty) ->
        PRODUCTS.find { it.id == id }?.price?.times(qty) ?: 0
    }
    val cartDelivery: Int get() = if (cartSubtotal > 1500) 0 else 149
    val cartTotal: Int get() = cartSubtotal + cartDelivery
}