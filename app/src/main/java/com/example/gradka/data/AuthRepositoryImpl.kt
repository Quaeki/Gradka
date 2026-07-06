package com.example.gradka.data

import com.example.gradka.data.AuthDAO.AuthApi
import com.example.gradka.data.AuthDAO.AuthPhoneDbModel
import com.example.gradka.data.AuthDAO.AuthUserDto
import com.example.gradka.data.AuthDAO.SessionDao
import com.example.gradka.data.AuthDAO.SendCodeRequest
import com.example.gradka.data.AuthDAO.UpdateNameRequest
import com.example.gradka.data.AuthDAO.VerifyCodeRequest
import com.example.gradka.domain.AuthRepository
import com.example.gradka.domain.AuthResult
import com.example.gradka.domain.AuthUser
import com.example.gradka.domain.UserSession
import com.example.gradka.security.storage.TokenStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val sessionDao: SessionDao,
    private val tokenStorage: TokenStorage,
) : AuthRepository {
    override suspend fun sendCode(phone: String): Int {
        val response = api.sendCode(SendCodeRequest(phone))
        return response.retryAfterSeconds
    }

    override suspend fun verifyCode(phone: String, code: String): AuthResult {
        val response = api.verifyCode(
            VerifyCodeRequest(
                phone = phone,
                code = code,
            ),
        )

        tokenStorage.save(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
        )

        return AuthResult(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            user = response.user.toAuthUser(),
        )
    }

    override suspend fun updateName(name: String): AuthUser {
        val accessToken = requireNotNull(tokenStorage.getAccessToken()) {
            "Access token is missing"
        }
        val user = api.updateName(
            bearerToken = "Bearer $accessToken",
            body = UpdateNameRequest(name.trim()),
        )

        sessionDao.saveSession(
            AuthPhoneDbModel(
                phone = user.phone,
                name = user.name.orEmpty(),
            ),
        )

        return user.toAuthUser()
    }

    override suspend fun getSession(): UserSession? =
        sessionDao.getSession()?.let { UserSession(phone = it.phone, name = it.name) }

    override suspend fun saveLocalSession(phone: String, name: String) {
        sessionDao.saveSession(
            AuthPhoneDbModel(
                phone = phone,
                name = name.trim(),
            ),
        )
    }

    override suspend fun clearSession() {
        tokenStorage.clear()
        sessionDao.clearSession()
    }
}

private fun AuthUserDto.toAuthUser(): AuthUser =
    AuthUser(
        id = id,
        phone = phone,
        name = name,
        isNew = isNew,
    )
