package com.example.gradka.data.OrderDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY rowid DESC")
    fun getOrders(): Flow<List<OrderDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderDbModel>)

    @Query("DELETE FROM orders")
    suspend fun clearOrders()

    @Transaction
    suspend fun replaceOrders(orders: List<OrderDbModel>) {
        clearOrders()
        insertOrders(orders)
    }
}
