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
import com.example.gradka.data.Subscription
import com.example.gradka.domain.AddAddressUseCase
import com.example.gradka.domain.AddNoteUseCase
import com.example.gradka.domain.DeleteAddressUseCase
import com.example.gradka.domain.DeleteNoteUseCase
import com.example.gradka.domain.EditNoteUseCase
import com.example.gradka.domain.GetAddressesUseCase
import com.example.gradka.domain.GetAllNoteUseCase
import com.example.gradka.domain.GetOrderUseCase
import com.example.gradka.domain.GradkaRepository
import com.example.gradka.domain.Note
import com.example.gradka.domain.PlaceOrderUseCase
import com.example.gradka.domain.SetPrimaryAddressUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn

import kotlinx.coroutines.launch

data class NotesScreenState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
)

class AppViewModel(
    private val repository: GradkaRepository,
    private val getOrderUseCase: GetOrderUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val getAddressesUseCase: GetAddressesUseCase,
    private val addAddressUseCase: AddAddressUseCase,
    private val setPrimaryAddressUseCase: SetPrimaryAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val editNoteUseCase: EditNoteUseCase,
    private val getAllNoteUseCase: GetAllNoteUseCase
) : ViewModel() {
    val cart = mutableStateMapOf<String, Int>()
    var favs by mutableStateOf(setOf<String>())
    var catFilter by mutableStateOf("all")

    var profileEmail by mutableStateOf("")
    var profileBirthday by mutableStateOf("")
    var profileGender by mutableStateOf("")

    fun updateProfileExtras(email: String, birthday: String, gender: String) {
        profileEmail = email.trim()
        profileBirthday = birthday.trim()
        profileGender = gender
    }

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

    fun addNote(title: String, content: String) {
        viewModelScope.launch { addNoteUseCase(title, content) }
    }
    fun deleteNote(noteId: Int) {
        viewModelScope.launch { deleteNoteUseCase(noteId) }
    }
    fun editNote(note: Note) {
        viewModelScope.launch { editNoteUseCase(note) }
    }
    val notes: StateFlow<List<Note>> = getAllNoteUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subscriptions: StateFlow<List<Subscription>> = repository.getSubscriptions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addSubscription(productId: String, qty: Int, frequencyDays: Int) {
        viewModelScope.launch {
            val current = subscriptionSnapshot
            if (current.any { it.productId == productId }) return@launch

            val nextId = "s" + ((current.maxOfOrNull {
                it.id.removePrefix("s").toIntOrNull() ?: 0
            } ?: 0) + 1)

            repository.addSubscription(
                Subscription(
                    id = nextId,
                    productId = productId,
                    qty = qty.coerceAtLeast(1),
                    frequencyDays = frequencyDays,
                    nextDelivery = nextDeliveryLabel(frequencyDays),
                )
            )
        }
    }

    fun updateSubscription(id: String, qty: Int? = null, frequencyDays: Int? = null, active: Boolean? = null) {
        viewModelScope.launch {
            val cur = subscriptionSnapshot.firstOrNull { it.id == id } ?: return@launch
            repository.updateSubscription(
                cur.copy(
                    qty = qty?.coerceAtLeast(1) ?: cur.qty,
                    frequencyDays = frequencyDays ?: cur.frequencyDays,
                    active = active ?: cur.active,
                    nextDelivery = if (frequencyDays != null) nextDeliveryLabel(frequencyDays) else cur.nextDelivery,
                )
            )
        }
    }

    fun deleteSubscription(id: String) {
        viewModelScope.launch {
            repository.deleteSubscription(id)
        }
    }

    private val subscriptionSnapshot: List<Subscription>
        get() = subscriptions.value.toList()

    val subscriptionsActiveCount: Int get() = subscriptionSnapshot.count { it.active }
    val subscriptionsMonthlyTotal: Int get() = subscriptionSnapshot
        .filter { it.active }
        .sumOf { sub ->
            val price = PRODUCTS.find { it.id == sub.productId }?.price ?: 0
            price * sub.qty * 30 / sub.frequencyDays
        }
    val subscriptionsMonthlySavings: Int get() = subscriptionsMonthlyTotal * 5 / 100
}

private fun nextDeliveryLabel(frequencyDays: Int): String {
    val days = listOf("вс", "пн", "вт", "ср", "чт", "пт", "сб")
    val months = listOf("янв", "фев", "мар", "апр", "мая", "июн", "июл", "авг", "сен", "окт", "ноя", "дек")
    val cal = java.util.Calendar.getInstance()
    cal.add(java.util.Calendar.DAY_OF_YEAR, frequencyDays)
    val dow = days[(cal.get(java.util.Calendar.DAY_OF_WEEK) - 1).coerceIn(0, 6)]
    val day = cal.get(java.util.Calendar.DAY_OF_MONTH)
    val mon = months[cal.get(java.util.Calendar.MONTH).coerceIn(0, 11)]
    return "$dow, $day $mon"
}
