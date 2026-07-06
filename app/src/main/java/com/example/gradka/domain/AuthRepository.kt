package com.example.gradka.domain

/**
 * Репозиторий аутентификации пользователя.
 *
 * Определяет контракт для входа по номеру телефона с подтверждением через OTP-код.
 * Реализация находится в [com.example.gradka.data.AuthRepositoryImpl].
 */
interface AuthRepository {

    /**
     * Отправляет SMS с одноразовым кодом подтверждения на указанный номер телефона.
     *
     * @param phone Номер телефона пользователя в формате «+7XXXXXXXXXX».
     * @return Количество секунд до возможности повторной отправки кода.
     */
    suspend fun sendCode(phone: String): Int

    /**
     * Проверяет введённый OTP-код и выполняет вход или регистрацию.
     *
     * @param phone Номер телефона, на который был отправлен код.
     * @param code Введённый пользователем одноразовый код.
     * @return [AuthResult] с токенами доступа и данными пользователя.
     * @throws Exception если код неверный или истёк срок его действия.
     */
    suspend fun verifyCode(phone: String, code: String): AuthResult

    /**
     * Обновляет имя текущего пользователя на сервере.
     *
     * @param name Новое имя пользователя.
     * @return Обновлённые данные пользователя [AuthUser].
     */
    suspend fun updateName(name: String): AuthUser

    /**
     * Возвращает сохранённую локальную сессию пользователя, если она существует.
     *
     * @return [UserSession] с телефоном и именем, либо null если сессии нет.
     */
    suspend fun getSession(): UserSession?

    /**
     * Сохраняет данные сессии локально (в зашифрованном хранилище).
     *
     * @param phone Номер телефона пользователя.
     * @param name Имя пользователя.
     */
    suspend fun saveLocalSession(phone: String, name: String)

    /**
     * Удаляет локальную сессию (выход из аккаунта).
     * После вызова [getSession] вернёт null.
     */
    suspend fun clearSession()
}