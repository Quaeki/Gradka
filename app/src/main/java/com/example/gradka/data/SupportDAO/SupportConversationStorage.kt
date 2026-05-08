package com.example.gradka.data.SupportDAO

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupportConversationStorage @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE,
    )

    fun get(): StoredSupportConversation? {
        val conversationId = prefs.getString(CONVERSATION_ID, null) ?: return null
        val supportPublicKey = prefs.getString(SUPPORT_PUBLIC_KEY, null) ?: return null
        return StoredSupportConversation(
            conversationId = conversationId,
            supportPublicKey = supportPublicKey,
        )
    }

    fun save(conversation: StoredSupportConversation) {
        prefs.edit()
            .putString(CONVERSATION_ID, conversation.conversationId)
            .putString(SUPPORT_PUBLIC_KEY, conversation.supportPublicKey)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val PREFS_NAME = "support_conversation"
        const val CONVERSATION_ID = "conversation_id"
        const val SUPPORT_PUBLIC_KEY = "support_public_key"
    }
}

data class StoredSupportConversation(
    val conversationId: String,
    val supportPublicKey: String,
)
