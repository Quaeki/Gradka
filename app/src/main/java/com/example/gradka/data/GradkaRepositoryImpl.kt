package com.example.gradka.data

import android.content.Context
import com.example.gradka.domain.AddressSuggestion
import com.example.gradka.domain.GradkaRepository
import com.example.gradka.domain.UserSession
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
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class GradkaRepositoryImpl private constructor(
    private val dao: SessionDao
) : GradkaRepository {
    private val orders = MutableStateFlow(ORDERS.toMutableList())
    private val addresses = MutableStateFlow(ADDRESSES.toMutableList())

    override fun placeOrder(cart: Map<String, Int>, addressId: String) {
        val totalQty = cart.values.sum()
        val totalPrice = cart.entries.sumOf { (id, qty) ->
            PRODUCTS.find { it.id == id }?.price?.times(qty) ?: 0
        }
        val newOrder = Order(
            id = UUID.randomUUID().toString(),
            date = "Сегодня",
            number = "№ ${(10000..99999).random()}",
            status = "В пути",
            total = totalPrice,
            items = totalQty,
        )
        orders.value = (listOf(newOrder) + orders.value).toMutableList()
    }

    override fun getOrder(): Flow<List<Order>> = orders

    override fun getAddresses(): Flow<List<Address>> = addresses

    override fun addAddress(address: Address) {
        addresses.value = (addresses.value + address).toMutableList()
    }

    override fun deleteAddress(addressId: String) {
        addresses.value = addresses.value.filter { it.id != addressId }.toMutableList()
    }

    override fun setPrimaryAddress(addressId: String) {
        addresses.value = addresses.value.map {
            it.copy(primary = it.id == addressId)
        }.toMutableList()
    }

    private val searchManager = SearchFactory.getInstance()
        .createSearchManager(SearchManagerType.ONLINE)

    private val moscowBounds = BoundingBox(
        Point(55.49, 36.80),
        Point(56.17, 38.20)
    )

    private var suggestSession: SuggestSession? = null


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

    companion object{
        private val LOCK = Any()
        private var INSTANCE: GradkaRepositoryImpl? = null
        fun getInstance(context: Context) : GradkaRepositoryImpl{
            INSTANCE?.let { return it }
            synchronized(LOCK){
                INSTANCE?.let{return it}
                val dao = AppDatabase.getInstance(context).sessionDao()
                return GradkaRepositoryImpl(dao).also {
                    INSTANCE = it
                }
            }
        }
    }
    override suspend fun getSession(): UserSession? =
        dao.getSession()?.let { UserSession(phone = it.phone, name = it.name) }

    override suspend fun saveSession(phone: String, name: String) =
        dao.saveSession(AuthPhoneDbModel(phone = phone, name = name))

    override suspend fun clearSession() = dao.clearSession()
}