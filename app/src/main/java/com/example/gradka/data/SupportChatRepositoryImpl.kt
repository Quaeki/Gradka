package com.example.gradka.data

import com.example.gradka.data.SupportDAO.RemoteSupportMessageDto
import com.example.gradka.data.SupportDAO.SendSupportMessageRequest
import com.example.gradka.data.SupportDAO.SupportChatApi
import com.example.gradka.data.SupportDAO.SupportMessageDao
import com.example.gradka.data.SupportDAO.toDbModel
import com.example.gradka.data.SupportDAO.toSupportMessage
import com.example.gradka.domain.SupportChatRepository
import com.example.gradka.domain.SupportMessage
import com.example.gradka.domain.SupportMessageAuthor
import com.example.gradka.security.storage.SecureTextCipher
import com.example.gradka.security.storage.TokenStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий чата поддержки на основе Telegram-релея.
 *
 * Сообщения пользователя отправляются на сервер, который пересылает их оператору
 * в Telegram; ответы оператора сервер получает через Bot API и отдаёт приложению
 * при синхронизации. Локальная копия переписки хранится в Room в зашифрованном
 * виде ([SecureTextCipher]).
 */
@Singleton
class SupportChatRepositoryImpl @Inject constructor(
    private val supportMessageDao: SupportMessageDao,
    private val secureTextCipher: SecureTextCipher,
    private val supportChatApi: SupportChatApi,
    private val tokenStorage: TokenStorage,
) : SupportChatRepository {
    override fun getMessages(): Flow<List<SupportMessage>> =
        supportMessageDao.getMessages().map { messages ->
            messages.mapNotNull { message ->
                runCatching { message.toSupportMessage(secureTextCipher) }.getOrNull()
            }
        }

    override suspend fun addMessage(message: SupportMessage) {
        if (message.author == SupportMessageAuthor.USER) {
            supportChatApi.sendMessage(
                bearerToken = requireAccessToken().toBearerToken(),
                body = SendSupportMessageRequest(
                    messageId = message.id,
                    text = message.text,
                ),
            )
            syncMessages()
        } else {
            supportMessageDao.insertMessage(message.toDbModel(secureTextCipher))
        }
    }

    override suspend fun clearMessages() {
        runCatching { supportChatApi.clearMessages(requireAccessToken().toBearerToken()) }
        supportMessageDao.clearMessages()
    }

    override suspend fun hasMessages(): Boolean =
        supportMessageDao.getMessagesCount() > 0

    override suspend fun syncMessages() {
        val messages = supportChatApi.getMessages(requireAccessToken().toBearerToken())
            .map { remoteMessage -> remoteMessage.toLocalMessage().toDbModel(secureTextCipher) }
        supportMessageDao.replaceMessages(messages)
    }

    private fun requireAccessToken(): String =
        requireNotNull(tokenStorage.getAccessToken()) {
            "Access token is missing"
        }

    private fun RemoteSupportMessageDto.toLocalMessage(): SupportMessage =
        SupportMessage(
            id = id,
            text = text,
            author = when (sender) {
                REMOTE_SENDER_USER -> SupportMessageAuthor.USER
                else -> SupportMessageAuthor.SUPPORT
            },
            createdAtMillis = createdAtMillis,
        )

    private fun String.toBearerToken(): String = "Bearer $this"

    private companion object {
        const val REMOTE_SENDER_USER = "user"
    }
}
