package com.example.gradka.di

import com.example.gradka.BuildConfig
import com.example.gradka.data.AuthDAO.AuthApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(apiBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    private fun apiBaseUrl(): String {
        val baseUrl = BuildConfig.API_BASE_URL.trim()
        require(BuildConfig.DEBUG || baseUrl.startsWith("https://", ignoreCase = true)) {
            "Release builds must use HTTPS API_BASE_URL"
        }
        return baseUrl
    }
}
