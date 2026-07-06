package com.example.gradka.di

import com.example.gradka.BuildConfig
import com.example.gradka.data.AuthDAO.AuthApi
import com.example.gradka.data.SupportDAO.SupportChatApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Hilt-модуль для предоставления зависимостей сетевого слоя.
 *
 * Устанавливается в [SingletonComponent], что гарантирует единственный
 * экземпляр Retrofit и API-интерфейсов на всё время жизни приложения.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Создаёт и предоставляет настроенный экземпляр [Retrofit].
     *
     * Базовый URL читается из BuildConfig, куда он попадает из local.properties.
     * Для десериализации JSON используется [GsonConverterFactory].
     *
     * @return Синглтон [Retrofit] для всего приложения.
     */
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(apiBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    /**
     * Создаёт реализацию [AuthApi] через Retrofit.
     *
     * @param retrofit Экземпляр Retrofit, предоставленный [provideRetrofit].
     * @return Реализация интерфейса [AuthApi] для OTP-аутентификации.
     */
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    /**
     * Создаёт реализацию [SupportChatApi] через Retrofit.
     *
     * @param retrofit Экземпляр Retrofit, предоставленный [provideRetrofit].
     * @return Реализация интерфейса [SupportChatApi] для чата поддержки.
     */
    @Provides
    @Singleton
    fun provideSupportChatApi(retrofit: Retrofit): SupportChatApi =
        retrofit.create(SupportChatApi::class.java)

    /**
     * Возвращает базовый URL API из BuildConfig.
     *
     * URL задаётся через `API_BASE_URL` в `local.properties` и обязателен для сборки.
     * В release-сборках принудительно требует HTTPS.
     * В debug-сборках допускает HTTP (для работы с локальным сервером).
     *
     * @return Валидный базовый URL API.
     * @throws IllegalArgumentException если URL не задан или в release-сборке указан HTTP URL.
     */
    private fun apiBaseUrl(): String {
        val baseUrl = BuildConfig.API_BASE_URL.trim()
        require(baseUrl.isNotEmpty()) {
            "API_BASE_URL is not configured. Set it in local.properties"
        }
        require(BuildConfig.DEBUG || baseUrl.startsWith("https://", ignoreCase = true)) {
            "Release builds must use HTTPS API_BASE_URL"
        }
        return baseUrl
    }
}
