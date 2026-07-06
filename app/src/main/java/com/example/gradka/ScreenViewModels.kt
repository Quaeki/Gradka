package com.example.gradka

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gradka.domain.AddAddressUseCase
import com.example.gradka.domain.AddNoteUseCase
import com.example.gradka.domain.AddPaymentMethodUseCase
import com.example.gradka.domain.AddSubscriptionUseCase
import com.example.gradka.domain.Address
import com.example.gradka.domain.CalculateSubscriptionSummaryUseCase
import com.example.gradka.domain.ClearSupportChatUseCase
import com.example.gradka.domain.SupportKeyChangedException
import com.example.gradka.domain.DeleteAddressUseCase
import com.example.gradka.domain.DeleteNoteUseCase
import com.example.gradka.domain.DeletePaymentMethodUseCase
import com.example.gradka.domain.DeleteSubscriptionUseCase
import com.example.gradka.domain.EditNoteUseCase
import com.example.gradka.domain.GetAddressesUseCase
import com.example.gradka.domain.GetAllNoteUseCase
import com.example.gradka.domain.GetOrderUseCase
import com.example.gradka.domain.GetPaymentMethodsUseCase
import com.example.gradka.domain.GetSupportMessagesUseCase
import com.example.gradka.domain.GetSubscriptionsUseCase
import com.example.gradka.domain.Note
import com.example.gradka.domain.PaymentMethod
import com.example.gradka.domain.ReverseGeocodeUseCase
import com.example.gradka.domain.SendSupportMessageUseCase
import com.example.gradka.domain.SetPrimaryAddressUseCase
import com.example.gradka.domain.Subscription
import com.example.gradka.domain.SupportMessage
import com.example.gradka.domain.SyncSupportMessagesUseCase
import com.example.gradka.domain.SuggestAddressesUseCase
import com.example.gradka.domain.UpdateSubscriptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val cartStore: CartStore,
) : ViewModel() {
    val cartCount: Int get() = cartStore.cartCount
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cartStore: CartStore,
    private val favoriteStore: FavoriteStore,
    getAddressesUseCase: GetAddressesUseCase,
) : ViewModel() {
    val addresses: StateFlow<List<Address>> = getAddressesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cart get() = cartStore.cart
    val favs: Set<String> get() = favoriteStore.favs

    fun addToCart(productId: String) = cartStore.addToCart(productId)
    fun subFromCart(productId: String) = cartStore.subFromCart(productId)
    fun toggleFav(productId: String) = favoriteStore.toggleFav(productId)
}

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val cartStore: CartStore,
    private val favoriteStore: FavoriteStore,
    private val catalogFilterStore: CatalogFilterStore,
) : ViewModel() {
    val cart get() = cartStore.cart
    val favs: Set<String> get() = favoriteStore.favs
    var catFilter: String
        get() = catalogFilterStore.catFilter
        set(value) {
            catalogFilterStore.catFilter = value
        }

    fun addToCart(productId: String) = cartStore.addToCart(productId)
    fun subFromCart(productId: String) = cartStore.subFromCart(productId)
    fun toggleFav(productId: String) = favoriteStore.toggleFav(productId)
}

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val cartStore: CartStore,
    private val favoriteStore: FavoriteStore,
) : ViewModel() {
    val cart get() = cartStore.cart
    val favs: Set<String> get() = favoriteStore.favs

    fun addToCart(productId: String) = cartStore.addToCart(productId)
    fun subFromCart(productId: String) = cartStore.subFromCart(productId)
    fun toggleFav(productId: String) = favoriteStore.toggleFav(productId)
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val cartStore: CartStore,
) : ViewModel() {
    val cart get() = cartStore.cart

    fun addToCart(productId: String) = cartStore.addToCart(productId)
    fun subFromCart(productId: String) = cartStore.subFromCart(productId)
}

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val cartStore: CartStore,
    private val favoriteStore: FavoriteStore,
    getAllNoteUseCase: GetAllNoteUseCase,
) : ViewModel() {
    val notes: StateFlow<List<Note>> = getAllNoteUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cart get() = cartStore.cart
    val favs: Set<String> get() = favoriteStore.favs

    fun addToCart(productId: String) = cartStore.addToCart(productId)
    fun subFromCart(productId: String) = cartStore.subFromCart(productId)
    fun toggleFav(productId: String) = favoriteStore.toggleFav(productId)
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartStore: CartStore,
) : ViewModel() {
    val cart get() = cartStore.cart
    val cartSubtotal: Int get() = cartStore.cartSubtotal
    val cartDelivery: Int get() = cartStore.cartDelivery
    val cartTotal: Int get() = cartStore.cartTotal

    fun addToCart(productId: String) = cartStore.addToCart(productId)
    fun subFromCart(productId: String) = cartStore.subFromCart(productId)
}

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartStore: CartStore,
    private val addPaymentMethodUseCase: AddPaymentMethodUseCase,
    getAddressesUseCase: GetAddressesUseCase,
    getPaymentMethodsUseCase: GetPaymentMethodsUseCase,
) : ViewModel() {
    val addresses: StateFlow<List<Address>> = getAddressesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val paymentMethods: StateFlow<List<PaymentMethod>> = getPaymentMethodsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartSubtotal: Int get() = cartStore.cartSubtotal
    val cartDelivery: Int get() = cartStore.cartDelivery
    val cartTotal: Int get() = cartStore.cartTotal

    fun addPaymentMethod(last4: String, brand: String, expiryMonth: Int, expiryYear: Int): String {
        val id = addPaymentMethodUseCase.createId()
        viewModelScope.launch {
            addPaymentMethodUseCase(id, last4, brand, expiryMonth, expiryYear)
        }
        return id
    }

    fun clearCart() = cartStore.clearCart()
}

@HiltViewModel
class AddressViewModel @Inject constructor(
    getAddressesUseCase: GetAddressesUseCase,
    private val addAddressUseCase: AddAddressUseCase,
    private val setPrimaryAddressUseCase: SetPrimaryAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    private val suggestAddressesUseCase: SuggestAddressesUseCase,
    private val reverseGeocodeUseCase: ReverseGeocodeUseCase,
) : ViewModel() {
    val addresses: StateFlow<List<Address>> = getAddressesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
}

@HiltViewModel
class NotesViewModel @Inject constructor(
    getAllNoteUseCase: GetAllNoteUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val editNoteUseCase: EditNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
) : ViewModel() {
    val notes: StateFlow<List<Note>> = getAllNoteUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addNote(title: String, content: String) {
        viewModelScope.launch { addNoteUseCase(title, content) }
    }

    fun editNote(note: Note) {
        viewModelScope.launch { editNoteUseCase(note) }
    }

    fun deleteNote(noteId: Int) {
        viewModelScope.launch { deleteNoteUseCase(noteId) }
    }
}

@HiltViewModel
class OrdersViewModel @Inject constructor(
    getOrderUseCase: GetOrderUseCase,
) : ViewModel() {
    val orders = getOrderUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileStore: ProfileStore,
    private val calculateSubscriptionSummaryUseCase: CalculateSubscriptionSummaryUseCase,
    getSubscriptionsUseCase: GetSubscriptionsUseCase,
    getAddressesUseCase: GetAddressesUseCase,
) : ViewModel() {
    val subscriptions: StateFlow<List<Subscription>> = getSubscriptionsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val addresses: StateFlow<List<Address>> = getAddressesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profileEmail: String get() = profileStore.profileEmail
    val profileBirthday: String get() = profileStore.profileBirthday
    val profileGender: String get() = profileStore.profileGender

    fun updateProfileExtras(email: String, birthday: String, gender: String) {
        profileStore.updateProfileExtras(email, birthday, gender)
    }

    private val subscriptionSummary get() = calculateSubscriptionSummaryUseCase(subscriptions.value.toList())
    val subscriptionsActiveCount: Int get() = subscriptionSummary.activeCount
}

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    getSubscriptionsUseCase: GetSubscriptionsUseCase,
    private val addSubscriptionUseCase: AddSubscriptionUseCase,
    private val updateSubscriptionUseCase: UpdateSubscriptionUseCase,
    private val deleteSubscriptionUseCase: DeleteSubscriptionUseCase,
    private val calculateSubscriptionSummaryUseCase: CalculateSubscriptionSummaryUseCase,
) : ViewModel() {
    val subscriptions: StateFlow<List<Subscription>> = getSubscriptionsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addSubscription(productId: String, qty: Int, frequencyDays: Int) {
        viewModelScope.launch { addSubscriptionUseCase(productId, qty, frequencyDays) }
    }

    fun updateSubscription(id: String, qty: Int? = null, frequencyDays: Int? = null, active: Boolean? = null) {
        viewModelScope.launch { updateSubscriptionUseCase(id, qty, frequencyDays, active) }
    }

    fun deleteSubscription(id: String) {
        viewModelScope.launch { deleteSubscriptionUseCase(id) }
    }

    private val subscriptionSummary get() = calculateSubscriptionSummaryUseCase(subscriptions.value.toList())
    val subscriptionsActiveCount: Int get() = subscriptionSummary.activeCount
    val subscriptionsMonthlyTotal: Int get() = subscriptionSummary.monthlyTotal
    val subscriptionsMonthlySavings: Int get() = subscriptionSummary.monthlySavings
}

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val cartStore: CartStore,
) : ViewModel() {
    fun addToCart(productId: String) = cartStore.addToCart(productId)
}

data class SupportChatUiState(
    val messages: List<SupportMessage> = emptyList(),
    val input: String = "",
    val syncing: Boolean = false,
    val syncError: String? = null,
)

@HiltViewModel
class SupportChatViewModel @Inject constructor(
    getSupportMessagesUseCase: GetSupportMessagesUseCase,
    private val sendSupportMessageUseCase: SendSupportMessageUseCase,
    private val syncSupportMessagesUseCase: SyncSupportMessagesUseCase,
    private val clearSupportChatUseCase: ClearSupportChatUseCase,
) : ViewModel() {
    private val messages = getSupportMessagesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val input = MutableStateFlow("")
    private val syncState = MutableStateFlow(SupportChatSyncState())

    val uiState: StateFlow<SupportChatUiState> =
        combine(messages, input, syncState.asStateFlow()) { messages, input, syncState ->
            SupportChatUiState(
                messages = messages.toList(),
                input = input,
                syncing = syncState.syncing,
                syncError = syncState.error,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SupportChatUiState())

    init {
        viewModelScope.launch {
            syncNow(showLoading = true)
        }
        viewModelScope.launch {
            while (isActive) {
                delay(SYNC_INTERVAL_MS)
                syncNow(showLoading = false)
            }
        }
    }

    fun onInputChange(value: String) {
        input.value = value.take(MAX_MESSAGE_LENGTH)
    }

    fun sendInput() {
        send(input.value)
    }

    fun sendQuickReply(text: String) {
        send(text)
    }

    fun clearChat() {
        viewModelScope.launch {
            clearSupportChatUseCase()
            syncNow(showLoading = true)
        }
    }

    private fun send(text: String) {
        viewModelScope.launch {
            if (text.trim().isEmpty()) return@launch
            val result = runCatching {
                syncState.value = SupportChatSyncState(syncing = true)
                sendSupportMessageUseCase(text)
            }
            if (result.isSuccess) {
                input.value = ""
                syncState.value = SupportChatSyncState()
            } else {
                syncState.value = SupportChatSyncState(error = result.exceptionOrNull().toUserMessage())
            }
        }
    }

    private suspend fun syncNow(showLoading: Boolean) {
        if (showLoading) {
            syncState.value = SupportChatSyncState(syncing = true)
        }
        val result = runCatching { syncSupportMessagesUseCase() }
        syncState.value = if (result.isSuccess) {
            SupportChatSyncState()
        } else {
            SupportChatSyncState(error = result.exceptionOrNull().toUserMessage())
        }
    }

    private fun Throwable?.toUserMessage(): String =
        when {
            this == null -> "Не удалось синхронизировать чат"
            this is SupportKeyChangedException ->
                "Ключ поддержки изменился. Очистите чат, чтобы принять новый ключ"
            message?.contains("409") == true -> "Сначала зарегистрируйте ключ оператора в панели поддержки"
            else -> "Нет связи с поддержкой. Попробуем обновить чат автоматически"
        }

    private companion object {
        const val MAX_MESSAGE_LENGTH = 500
        const val SYNC_INTERVAL_MS = 5_000L
    }
}

private data class SupportChatSyncState(
    val syncing: Boolean = false,
    val error: String? = null,
)
