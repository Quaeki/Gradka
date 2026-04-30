package com.example.gradka.data.BillingDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BillingDao {

    @Query("SELECT * FROM billing ORDER BY rowid DESC")
    fun getPaymentMethods(): Flow<List<BillingDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentMethod(paymentMethod: BillingDbModel)

    @Query("DELETE FROM billing WHERE id = :id")
    suspend fun deletePaymentMethod(id: String)
}
