package com.example.gradka.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gradka.data.AuthDAO.AuthPhoneDbModel
import com.example.gradka.data.AuthDAO.SessionDao
import com.example.gradka.data.BillingDAO.BillingDao
import com.example.gradka.data.BillingDAO.BillingDbModel
import com.example.gradka.data.OrderDAO.OrderDao
import com.example.gradka.data.OrderDAO.OrderDbModel
import com.example.gradka.data.SubDAO.SubDao
import com.example.gradka.data.SubDAO.SubDbModel
import com.example.gradka.data.SupportDAO.SupportMessageDao
import com.example.gradka.data.SupportDAO.SupportMessageDbModel
import javax.inject.Singleton

@Database(
    entities = [
        AuthPhoneDbModel::class,
        OrderDbModel::class,
        SubDbModel::class,
        BillingDbModel::class,
        SupportMessageDbModel::class,
    ],
    version = 5,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun orderDao(): OrderDao
    abstract fun subDao(): SubDao
    abstract fun billingDao(): BillingDao
    abstract fun supportMessageDao(): SupportMessageDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private val LOCK = Any()

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `orders` (
                        `id` TEXT NOT NULL,
                        `date` TEXT NOT NULL,
                        `number` TEXT NOT NULL,
                        `status` TEXT NOT NULL,
                        `total` INTEGER NOT NULL,
                        `items` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `subscriptions` (
                        `id` TEXT NOT NULL,
                        `productId` TEXT NOT NULL,
                        `qty` INTEGER NOT NULL,
                        `frequencyDays` INTEGER NOT NULL,
                        `nextDelivery` TEXT NOT NULL,
                        `active` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `billing` (
                        `id` TEXT NOT NULL,
                        `last4` TEXT NOT NULL,
                        `brand` TEXT NOT NULL,
                        `expiryMonth` INTEGER NOT NULL,
                        `expiryYear` INTEGER NOT NULL,
                        `isDefault` INTEGER NOT NULL,
                        `createdAtMillis` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `support_messages` (
                        `id` TEXT NOT NULL,
                        `encryptedText` TEXT NOT NULL,
                        `textIv` TEXT NOT NULL,
                        `author` TEXT NOT NULL,
                        `createdAtMillis` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            INSTANCE?.let { return it }
            synchronized(LOCK) {
                INSTANCE?.let { return it }
                val appContext = context.applicationContext
                return Room.databaseBuilder(
                    context = appContext,
                    klass = AppDatabase::class.java,
                    name = "gradka.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}
