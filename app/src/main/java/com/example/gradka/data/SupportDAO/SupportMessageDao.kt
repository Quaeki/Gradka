package com.example.gradka.data.SupportDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SupportMessageDao {
    @Query("SELECT * FROM support_messages ORDER BY createdAtMillis ASC")
    fun getMessages(): Flow<List<SupportMessageDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: SupportMessageDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<SupportMessageDbModel>)

    @Query("DELETE FROM support_messages")
    suspend fun clearMessages()

    @Query("SELECT COUNT(*) FROM support_messages")
    suspend fun getMessagesCount(): Int

    @Transaction
    suspend fun replaceMessages(messages: List<SupportMessageDbModel>) {
        clearMessages()
        insertMessages(messages)
    }
}
