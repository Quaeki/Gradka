package com.example.gradka.domain

/**
 * Message displayed in the support chat.
 *
 * @property id Stable message identifier used for local storage and server synchronization.
 * @property text Decrypted message text displayed to the user.
 * @property author Message author: application user or support operator.
 * @property createdAtMillis Creation timestamp in Unix epoch milliseconds.
 */
data class SupportMessage(
    val id: String,
    val text: String,
    val author: SupportMessageAuthor,
    val createdAtMillis: Long,
)

/**
 * Identifies the side that created a support chat message.
 */
enum class SupportMessageAuthor {
    /** Message was sent by the mobile app user. */
    USER,

    /** Message was sent by the support operator. */
    SUPPORT,
}
