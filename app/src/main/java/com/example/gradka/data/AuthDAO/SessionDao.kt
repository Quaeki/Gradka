package com.example.gradka.data.AuthDAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SessionDao {
    @Query("SELECT * FROM session LIMIT 1")
    suspend fun getSession(): AuthPhoneDbModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(session: AuthPhoneDbModel)

    @Query("DELETE FROM session")
    suspend fun clearSession()
}