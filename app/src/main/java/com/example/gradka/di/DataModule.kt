package com.example.gradka.di

import android.content.Context
import com.example.gradka.data.AppDatabase
import com.example.gradka.data.AuthDAO.SessionDao
import com.example.gradka.data.AuthRepositoryImpl
import com.example.gradka.data.BillingDAO.BillingDao
import com.example.gradka.data.GradkaRepositoryImpl
import com.example.gradka.data.OrderDAO.OrderDao
import com.example.gradka.data.SubDAO.SubDao
import com.example.gradka.data.SupportChatRepositoryImpl
import com.example.gradka.data.SupportDAO.SupportMessageDao
import com.example.gradka.domain.AuthRepository
import com.example.gradka.domain.GradkaRepository
import com.example.gradka.domain.SupportChatRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindGradkaRepository(repository: GradkaRepositoryImpl): GradkaRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(repository: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSupportChatRepository(repository: SupportChatRepositoryImpl): SupportChatRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideSessionDao(database: AppDatabase): SessionDao = database.sessionDao()

    @Provides
    fun provideOrderDao(database: AppDatabase): OrderDao = database.orderDao()

    @Provides
    fun provideSubDao(database: AppDatabase): SubDao = database.subDao()

    @Provides
    fun provideBillingDao(database: AppDatabase): BillingDao = database.billingDao()

    @Provides
    fun provideSupportMessageDao(database: AppDatabase): SupportMessageDao = database.supportMessageDao()

}
