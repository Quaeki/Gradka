package com.example.gradka.data.SubDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SubDao {
    @Query("SELECT * FROM subscriptions ORDER BY rowid DESC")
    fun getSubscriptions(): Flow<List<SubDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubDbModel)

    @Update
    suspend fun updateSubscription(subscription: SubDbModel)

    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun deleteSubscription(id: String)
}
