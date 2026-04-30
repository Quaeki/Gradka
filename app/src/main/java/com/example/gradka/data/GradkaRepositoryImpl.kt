package com.example.gradka.data

import android.app.Activity
import com.example.gradka.data.AuthDAO.AuthPhoneDbModel
import com.example.gradka.data.AuthDAO.SessionDao
import com.example.gradka.data.BillingDAO.BillingDao
import com.example.gradka.data.BillingDAO.toBillingDbModel
import com.example.gradka.data.BillingDAO.toPaymentMethod
import com.example.gradka.data.OrderDAO.OrderDao
import com.example.gradka.data.OrderDAO.toDbModel
import com.example.gradka.data.OrderDAO.toOrder
import com.example.gradka.data.SubDAO.SubDao
import com.example.gradka.data.SubDAO.toDbModel
import com.example.gradka.data.SubDAO.toSubscription
import com.example.gradka.domain.ADDRESSES
import com.example.gradka.domain.Address
import com.example.gradka.domain.AddressSuggestion
import com.example.gradka.domain.GradkaRepository
import com.example.gradka.domain.Note
import com.example.gradka.domain.Order
import com.example.gradka.domain.PRODUCTS
import com.example.gradka.domain.PaymentMethod
import com.example.gradka.domain.Subscription
import com.example.gradka.domain.UserSession
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.SuggestOptions
import com.yandex.mapkit.search.SuggestResponse
import com.yandex.mapkit.search.SuggestSession
import com.yandex.runtime.Error
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.ref.WeakReference
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class GradkaRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao,
    private val orderDao: OrderDao,
    private val subDao: SubDao,
    private val billingDao: BillingDao,
) : GradkaRepository {
    private var activityRef: WeakReference<Activity>? = null

    private val addresses = MutableStateFlow(ADDRESSES.toList())
    private val notesListFlow = MutableStateFlow<List<Note>>(listOf())

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var storedVerificationId: String? = null

    private val searchManager = SearchFactory.getInstance()
        .createSearchManager(SearchManagerType.ONLINE)

    private val moscowBounds = BoundingBox(
        Point(55.49, 36.80),
        Point(56.17, 38.20),
    )

    private var suggestSession: SuggestSession? = null

    override fun getOrder(): Flow<List<Order>> =
        orderDao.getOrders().map { orders ->
            orders.map { it.toOrder() }
        }

    override suspend fun placeOrder(cart: Map<String, Int>, addressId: String) {
        val newOrder = createOrderFromCart(cart)
        orderDao.insertOrders(listOf(newOrder.toDbModel()))
    }

    override fun getSubscriptions(): Flow<List<Subscription>> =
        subDao.getSubscriptions().map { subscriptions ->
            subscriptions.map { it.toSubscription() }
        }

    override suspend fun addSubscription(subscription: Subscription) {
        subDao.insertSubscription(subscription.toDbModel())
    }

    override suspend fun updateSubscription(subscription: Subscription) {
        subDao.updateSubscription(subscription.toDbModel())
    }

    override suspend fun deleteSubscription(subscriptionId: String) {
        subDao.deleteSubscription(subscriptionId)
    }

    override fun getPaymentMethods(): Flow<List<PaymentMethod>> =
        billingDao.getPaymentMethods().map { paymentMethods ->
            paymentMethods.map { it.toPaymentMethod() }
        }

    override suspend fun addPaymentMethod(paymentMethod: PaymentMethod) {
        billingDao.insertPaymentMethod(paymentMethod.toBillingDbModel())
    }

    override suspend fun deletePaymentMethod(paymentMethodId: String) {
        billingDao.deletePaymentMethod(paymentMethodId)
    }

    fun attachActivity(activity: Activity) {
        activityRef = WeakReference(activity)
    }

    fun detachActivity(activity: Activity) {
        if (activityRef?.get() === activity) {
            activityRef = null
        }
    }

    override fun getAddresses(): Flow<List<Address>> = addresses.asStateFlow()

    override fun addAddress(address: Address) {
        addresses.update { current -> current + address }
    }

    override fun deleteAddress(addressId: String) {
        addresses.update { current -> current.filter { it.id != addressId } }
    }

    override fun setPrimaryAddress(addressId: String) {
        addresses.update { current ->
            current.map { it.copy(primary = it.id == addressId) }
        }
    }

    override suspend fun suggestAddresses(query: String): List<AddressSuggestion> =
        suspendCancellableCoroutine { cont ->
            suggestSession?.reset()
            suggestSession = searchManager.createSuggestSession()

            suggestSession?.suggest(
                query,
                moscowBounds,
                SuggestOptions(),
                object : SuggestSession.SuggestListener {
                    override fun onResponse(response: SuggestResponse) {
                        val result = response.items.mapNotNull { item ->
                            val center = item.center ?: return@mapNotNull null
                            AddressSuggestion(
                                title = item.title.text,
                                subtitle = item.subtitle?.text ?: "",
                                fullText = buildString {
                                    append(item.title.text)
                                    val sub = item.subtitle?.text
                                    if (!sub.isNullOrEmpty()) append(", $sub")
                                },
                                lat = center.latitude,
                                lon = center.longitude,
                            )
                        }
                        cont.resume(result)
                    }

                    override fun onError(error: Error) {
                        cont.resumeWithException(Exception(error.toString()))
                    }
                }
            )

            cont.invokeOnCancellation { suggestSession?.reset() }
        }

    override suspend fun reverseGeocode(lat: Double, lon: Double): String =
        suspendCancellableCoroutine { cont ->
            val point = Point(lat, lon)
            searchManager.submit(
                point,
                16,
                SearchOptions(),
                object : Session.SearchListener {
                    override fun onSearchResponse(response: Response) {
                        val name = response.collection.children
                            .firstOrNull()?.obj?.name ?: ""
                        cont.resume(name)
                    }

                    override fun onSearchError(error: Error) {
                        cont.resume("")
                    }
                }
            )
        }

    override suspend fun getSession(): UserSession? =
        sessionDao.getSession()?.let { UserSession(phone = it.phone, name = it.name) }

    override suspend fun saveSession(phone: String, name: String) =
        sessionDao.saveSession(AuthPhoneDbModel(phone = phone, name = name))

    override suspend fun clearSession() = sessionDao.clearSession()

    override suspend fun sendOtp(phone: String): Unit = suspendCancellableCoroutine { cont ->
        val act = activityRef?.get() ?: run {
            cont.resumeWithException(IllegalStateException("Activity not attached"))
            return@suspendCancellableCoroutine
        }

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Автоматическая верификация (SMS перехвачен системой) — не используем сейчас
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (cont.isActive) cont.resumeWithException(e)
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                storedVerificationId = verificationId
                if (cont.isActive) cont.resume(Unit)
            }
        }
        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber("+7$phone")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(act)
                .setCallbacks(callbacks)
                .build()
        )
    }

    override suspend fun verifyOtp(phone: String, code: String): Boolean = suspendCancellableCoroutine { cont ->
        val verificationId = storedVerificationId ?: run {
            cont.resume(false)
            return@suspendCancellableCoroutine
        }
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { cont.resume(true) }
            .addOnFailureListener { cont.resume(false) }
    }

    override fun getAllNotes(): Flow<List<Note>> = notesListFlow.asStateFlow()

    override suspend fun addNote(title: String, content: String) {
        notesListFlow.update { oldList ->
            val note = Note(
                id = (oldList.maxOfOrNull { it.id } ?: 0) + 1,
                title = title,
                content = content,
            )
            oldList + note
        }
    }

    override suspend fun editNote(note: Note) {
        notesListFlow.update { list ->
            list.map { if (it.id == note.id) note else it }
        }
    }

    override suspend fun deleteNote(noteId: Int) {
        notesListFlow.update { it.filter { note -> note.id != noteId } }
    }

    private fun createOrderFromCart(cart: Map<String, Int>): Order {
        val totalQty = cart.values.sum()
        val totalPrice = cart.entries.sumOf { (id, qty) ->
            PRODUCTS.find { it.id == id }?.price?.times(qty) ?: 0
        }

        return Order(
            id = UUID.randomUUID().toString(),
            date = "Сегодня",
            number = "№ ${(10000..99999).random()}",
            status = "В пути",
            total = totalPrice,
            items = totalQty,
        )
    }

}
