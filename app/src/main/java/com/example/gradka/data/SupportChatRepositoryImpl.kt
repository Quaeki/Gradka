package com.example.gradka.data

import com.example.gradka.data.SupportDAO.RemoteSupportMessageDto
import com.example.gradka.data.SupportDAO.SendSupportMessageRequest
import com.example.gradka.data.SupportDAO.StoredSupportConversation
import com.example.gradka.data.SupportDAO.SupportChatApi
import com.example.gradka.data.SupportDAO.SupportConversationRequest
import com.example.gradka.data.SupportDAO.SupportConversationStorage
import com.example.gradka.data.SupportDAO.SupportMessageDao
import com.example.gradka.data.SupportDAO.toDbModel
import com.example.gradka.data.SupportDAO.toSupportMessage
import com.example.gradka.domain.SupportChatRepository
import com.example.gradka.domain.SupportKeyChangedException
import com.example.gradka.domain.SupportMessage
import com.example.gradka.domain.SupportMessageAuthor
import com.example.gradka.domain.SupportMessageCrypto
import com.example.gradka.security.storage.SecureTextCipher
import com.example.gradka.security.storage.TokenStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupportChatRepositoryImpl @Inject constructor(
    private val supportMessageDao: SupportMessageDao,
    private val secureTextCipher: SecureTextCipher,
    private val supportChatApi: SupportChatApi,
    private val tokenStorage: TokenStorage,
    private val supportMessageCrypto: SupportMessageCrypto,
    private val conversationStorage: SupportConversationStorage,
) : SupportChatRepository {
    override fun getMessages(): Flow<List<SupportMessage>> =
        supportMessageDao.getMessages().map { messages ->
            messages.mapNotNull { message ->
                runCatching { message.toSupportMessage(secureTextCipher) }.getOrNull()
            }
        }

    override suspend fun addMessage(message: SupportMessage) {
        if (message.author == SupportMessageAuthor.USER) {
            sendRemoteMessage(message)
            syncMessages()
        } else {
            supportMessageDao.insertMessage(message.toDbModel(secureTextCipher))
        }
    }

    override suspend fun clearMessages() {
        supportMessageDao.clearMessages()
        conversationStorage.clear()
    }

    override suspend fun hasMessages(): Boolean =
        supportMessageDao.getMessagesCount() > 0

    override suspend fun syncMessages() {
        val token = requireAccessToken()
        val conversation = getOrCreateConversation(token)
        val messages = supportChatApi.getMessages(
            bearerToken = token.toBearerToken(),
            conversationId = conversation.conversationId,
        ).map { remoteMessage ->
            remoteMessage.toLocalMessage(conversation).toDbModel(secureTextCipher)
        }
        supportMessageDao.replaceMessages(messages)
    }

    private suspend fun sendRemoteMessage(message: SupportMessage) {
        val token = requireAccessToken()
        val conversation = getOrCreateConversation(token)
        val encryptedText = supportMessageCrypto.encrypt(
            plainText = message.text,
            peerPublicKey = conversation.supportPublicKey,
            conversationId = conversation.conversationId,
        )

        supportChatApi.sendMessage(
            bearerToken = token.toBearerToken(),
            body = SendSupportMessageRequest(
                messageId = message.id,
                conversationId = conversation.conversationId,
                encryptedText = encryptedText.value,
                textIv = encryptedText.iv,
                senderPublicKey = supportMessageCrypto.getPublicKey(),
                createdAtMillis = message.createdAtMillis,
            ),
        )
    }

    private fun requireAccessToken(): String =
        requireNotNull(tokenStorage.getAccessToken()) {
            "Access token is missing"
        }

    private suspend fun getOrCreateConversation(accessToken: String): StoredSupportConversation {
        val response = supportChatApi.getOrCreateConversation(
            bearerToken = accessToken.toBearerToken(),
            body = SupportConversationRequest(
                userPublicKey = supportMessageCrypto.getPublicKey(),
            ),
        )
        val conversation = StoredSupportConversation(
            conversationId = response.conversationId,
            supportPublicKey = response.supportPublicKey,
        )
        val cachedConversation = conversationStorage.get()
        // TOFU: ключ поддержки закрепляется при первом контакте. Изменившийся ключ не
        // принимается молча — это может быть MITM. Пользователь подтверждает новый ключ
        // явной очисткой чата (clearMessages сбрасывает закреплённую беседу).
        if (cachedConversation != null && cachedConversation.supportPublicKey != conversation.supportPublicKey) {
            throw SupportKeyChangedException()
        }
        if (cachedConversation != null && cachedConversation.conversationId != conversation.conversationId) {
            supportMessageDao.clearMessages()
        }
        conversationStorage.save(conversation)
        return conversation
    }

    private fun RemoteSupportMessageDto.toLocalMessage(conversation: StoredSupportConversation): SupportMessage {
        val author = when (sender) {
            REMOTE_SENDER_USER -> SupportMessageAuthor.USER
            else -> SupportMessageAuthor.SUPPORT
        }
        // Ключ отправителя из ответа сервера не используется: доверяем только закреплённому
        // ключу беседы, иначе сервер мог бы подменить отправителя сообщения.
        val peerPublicKey = conversation.supportPublicKey
        val text = runCatching {
            supportMessageCrypto.decrypt(
                encryptedText = encryptedText,
                iv = textIv,
                peerPublicKey = peerPublicKey,
                conversationId = conversation.conversationId,
            )
        }.getOrElse {
            UNREADABLE_MESSAGE_TEXT
        }

        return SupportMessage(
            id = id,
            text = text,
            author = author,
            createdAtMillis = createdAtMillis,
        )
    }

    private fun String.toBearerToken(): String = "Bearer $this"

    private companion object {
        const val REMOTE_SENDER_USER = "user"
        const val UNREADABLE_MESSAGE_TEXT =
            "Не удалось расшифровать сообщение. Возможно, был изменен ключ поддержки."
    }
}
