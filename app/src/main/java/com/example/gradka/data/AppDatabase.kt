package com.example.gradka.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities =
    [
        AuthPhoneDbModel::class,
        OrderDbModel::class,
    ],
    version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun orderDao(): OrderDao
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
                    name = "gradka.db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}