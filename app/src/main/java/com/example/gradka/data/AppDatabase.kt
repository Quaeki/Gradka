package com.example.gradka.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AuthPhoneDbModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private val LOCK = Any()
        fun getInstance(context: Context) : AppDatabase{
            INSTANCE?.let { return it }
            synchronized(LOCK){
                INSTANCE?.let{return it}
                return Room.databaseBuilder(
                    context = context,
                    klass = AppDatabase::class.java,
                    name = "auth.db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}