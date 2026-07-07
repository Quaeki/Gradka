package com.example.gradka.data.OrderDAO

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface OrdersApi {
    @GET("orders/")
    suspend fun getOrders(
        @Header("Authorization") bearerToken: String,
    ): List<RemoteOrderDto>

    @POST("orders/")
    suspend fun placeOrder(
        @Header("Authorization") bearerToken: String,
        @Body body: PlaceOrderRequest,
    ): RemoteOrderDto

    @GET("orders/catalog")
    suspend fun getCatalog(): List<CatalogProductDto>
}

data class CatalogProductDto(
    val id: String,
    val name: String,
    val subtitle: String,
    val price: Int,
    val unit: String,
    val cat: String,
    val hue: Float,
    val badge: String?,
    val farm: String,
    val imageUrl: String?,
)

data class PlaceOrderRequest(
    val items: List<PlaceOrderItem>,
    val addressText: String,
)

data class PlaceOrderItem(
    val productId: String,
    val qty: Int,
)

data class RemoteOrderDto(
    val id: String,
    val number: Long,
    val status: String,
    val addressText: String,
    val subtotal: Int,
    val delivery: Int,
    val total: Int,
    val itemsCount: Int,
    val createdAtMillis: Long,
)
