package com.example.gradka.data

import com.example.gradka.data.SupportDAO.SupportMessageDao
import com.example.gradka.data.SupportDAO.toDbModel
import com.example.gradka.data.SupportDAO.toSupportMessage
import com.example.gradka.data.security.SecureTextCipher
import com.example.gradka.domain.SupportChatRepository
import com.example.gradka.domain.SupportMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupportChatRepositoryImpl @Inject constructor(
    private val supportMessageDao: SupportMessageDao,
    private val secureTextCipher: SecureTextCipher,
) : SupportChatRepository {
    override fun getMessages(): Flow<List<SupportMessage>> =
        supportMessageDao.getMessages().map { messages ->
            messages.mapNotNull { message ->
                runCatching { message.toSupportMessage(secureTextCipher) }.getOrNull()
            }
        }

    override suspend fun addMessage(message: SupportMessage) {
        supportMessageDao.insertMessage(message.toDbModel(secureTextCipher))
    }

    override suspend fun clearMessages() {
        supportMessageDao.clearMessages()
    }

    override suspend fun hasMessages(): Boolean =
        supportMessageDao.getMessagesCount() > 0
}
