package com.example.gradka.domain

import java.util.UUID
import javax.inject.Inject

class AddSupportAutoReplyUseCase @Inject constructor(
    private val repository: SupportChatRepository,
) {
    suspend operator fun invoke(question: String) {
        repository.addMessage(
            SupportMessage(
                id = UUID.randomUUID().toString(),
                text = createReply(question),
                author = SupportMessageAuthor.SUPPORT,
                createdAtMillis = System.currentTimeMillis(),
            )
        )
    }

    private fun createReply(question: String): String {
        val q = question.lowercase()
        return when {
            q.contains("заказ") && (q.contains("где") || q.contains("статус")) ->
                "Сейчас уточню по вашему заказу. Курьер обычно выходит на связь за 10 минут до доставки."
            q.contains("замен") ->
                "Если товара не окажется, курьер предложит замену прямо в чате. Хотите указать предпочтения?"
            q.contains("оплат") ->
                "Подскажите, какой способ оплаты выбран? Иногда помогает попробовать другую карту или СБП."
            q.contains("верн") || q.contains("возврат") ->
                "Конечно, оформим возврат. Опишите, что не так с товаром, и при возможности приложите фото."
            q.contains("спасибо") || q.contains("ок") ->
                "Рада помочь! Если что-то еще - пишите."
            else ->
                "Поняла вас. Сейчас передам коллегам и вернусь с ответом в течение нескольких минут."
        }
    }
}
