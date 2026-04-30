package com.example.gradka

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gradka.domain.Address
import com.example.gradka.domain.Order
import com.example.gradka.domain.PaymentMethod
import com.example.gradka.domain.Subscription
import com.example.gradka.domain.AddAddressUseCase
import com.example.gradka.domain.AddNoteUseCase
import com.example.gradka.domain.AddPaymentMethodUseCase
import com.example.gradka.domain.AddSubscriptionUseCase
import com.example.gradka.domain.CalculateCartSummaryUseCase
import com.example.gradka.domain.CalculateSubscriptionSummaryUseCase
import com.example.gradka.domain.DeleteAddressUseCase
import com.example.gradka.domain.DeleteNoteUseCase
import com.example.gradka.domain.DeletePaymentMethodUseCase
import com.example.gradka.domain.DeleteSubscriptionUseCase
import com.example.gradka.domain.EditNoteUseCase
import com.example.gradka.domain.GetAddressesUseCase
import com.example.gradka.domain.GetAllNoteUseCase
import com.example.gradka.domain.GetOrderUseCase
import com.example.gradka.domain.GetPaymentMethodsUseCase
import com.example.gradka.domain.GetSubscriptionsUseCase
import com.example.gradka.domain.Note
import com.example.gradka.domain.PlaceOrderUseCase
import com.example.gradka.domain.ReverseGeocodeUseCase
import com.example.gradka.domain.SetPrimaryAddressUseCase
import com.example.gradka.domain.SuggestAddressesUseCase
import com.example.gradka.domain.UpdateSubscriptionUseCase
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
    private val getOrderUseCase: GetOrderUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val getAddressesUseCase: GetAddressesUseCase,
    private val addAddressUseCase: AddAddressUseCase,
    private val setPrimaryAddressUseCase: SetPrimaryAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    private val suggestAddressesUseCase: SuggestAddressesUseCase,
    private val reverseGeocodeUseCase: ReverseGeocodeUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val editNoteUseCase: EditNoteUseCase,
    private val getAllNoteUseCase: GetAllNoteUseCase,
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase,
    private val addSubscriptionUseCase: AddSubscriptionUseCase,
    private val updateSubscriptionUseCase: UpdateSubscriptionUseCase,
    private val deleteSubscriptionUseCase: DeleteSubscriptionUseCase,
    private val calculateSubscriptionSummaryUseCase: CalculateSubscriptionSummaryUseCase,
    private val getPaymentMethodsUseCase: GetPaymentMethodsUseCase,
    private val addPaymentMethodUseCase: AddPaymentMethodUseCase,
    private val deletePaymentMethodUseCase: DeletePaymentMethodUseCase,
    private val calculateCartSummaryUseCase: CalculateCartSummaryUseCase,
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

    suspend fun suggestAddresses(query: String) = suggestAddressesUseCase(query)
    suspend fun reverseGeocode(lat: Double, lon: Double) = reverseGeocodeUseCase(lat, lon)

    private val cartSummary get() = calculateCartSummaryUseCase(cart.toMap())
    val cartCount: Int get() = cartSummary.count
    val cartSubtotal: Int get() = cartSummary.subtotal
    val cartDelivery: Int get() = cartSummary.delivery
    val cartTotal: Int get() = cartSummary.total

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

    val subscriptions: StateFlow<List<Subscription>> = getSubscriptionsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paymentMethods: StateFlow<List<PaymentMethod>> = getPaymentMethodsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addSubscription(productId: String, qty: Int, frequencyDays: Int) {
        viewModelScope.launch {
            addSubscriptionUseCase(productId, qty, frequencyDays)
        }
    }

    fun updateSubscription(id: String, qty: Int? = null, frequencyDays: Int? = null, active: Boolean? = null) {
        viewModelScope.launch {
            updateSubscriptionUseCase(id, qty, frequencyDays, active)
        }
    }

    fun deleteSubscription(id: String) {
        viewModelScope.launch {
            deleteSubscriptionUseCase(id)
        }
    }

    fun addPaymentMethod(
        last4: String,
        brand: String,
        expiryMonth: Int,
        expiryYear: Int,
    ): String {
        val id = addPaymentMethodUseCase.createId()
        viewModelScope.launch {
            addPaymentMethodUseCase(id, last4, brand, expiryMonth, expiryYear)
        }
        return id
    }

    fun deletePaymentMethod(paymentMethodId: String) {
        viewModelScope.launch {
            deletePaymentMethodUseCase(paymentMethodId)
        }
    }

    private val subscriptionSnapshot: List<Subscription>
        get() = subscriptions.value.toList()

    private val subscriptionSummary get() = calculateSubscriptionSummaryUseCase(subscriptionSnapshot)
    val subscriptionsActiveCount: Int get() = subscriptionSummary.activeCount
    val subscriptionsMonthlyTotal: Int get() = subscriptionSummary.monthlyTotal
    val subscriptionsMonthlySavings: Int get() = subscriptionSummary.monthlySavings
}
