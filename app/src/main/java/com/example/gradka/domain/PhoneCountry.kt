package com.example.gradka.domain

/**
 * Страна в каталоге телефонных кодов.
 *
 * @property iso Двухбуквенный ISO-код страны (например, «UZ»).
 * @property flag Эмодзи-флаг для отображения в селекторе.
 * @property displayName Название страны на русском.
 * @property dialCode Телефонный код страны с плюсом (например, «+998»).
 * @property localLength Количество цифр в национальной части номера.
 */
data class PhoneCountry(
    val iso: String,
    val flag: String,
    val displayName: String,
    val dialCode: String,
    val localLength: Int,
)

/**
 * Каталог поддерживаемых телефонных кодов стран.
 */
object PhoneCountries {
    val all = listOf(
        PhoneCountry(iso = "RU", flag = "🇷🇺", displayName = "Россия", dialCode = "+7", localLength = 10),
        PhoneCountry(iso = "UZ", flag = "🇺🇿", displayName = "Узбекистан", dialCode = "+998", localLength = 9),
        PhoneCountry(iso = "KZ", flag = "🇰🇿", displayName = "Казахстан", dialCode = "+7", localLength = 10),
        PhoneCountry(iso = "BY", flag = "🇧🇾", displayName = "Беларусь", dialCode = "+375", localLength = 9),
        PhoneCountry(iso = "KG", flag = "🇰🇬", displayName = "Киргизия", dialCode = "+996", localLength = 9),
        PhoneCountry(iso = "TJ", flag = "🇹🇯", displayName = "Таджикистан", dialCode = "+992", localLength = 9),
        PhoneCountry(iso = "AM", flag = "🇦🇲", displayName = "Армения", dialCode = "+374", localLength = 8),
        PhoneCountry(iso = "AZ", flag = "🇦🇿", displayName = "Азербайджан", dialCode = "+994", localLength = 9),
        PhoneCountry(iso = "GE", flag = "🇬🇪", displayName = "Грузия", dialCode = "+995", localLength = 9),
        PhoneCountry(iso = "UA", flag = "🇺🇦", displayName = "Украина", dialCode = "+380", localLength = 9),
    )

    val default = all.first()

    /**
     * Разбирает номер в формате E.164 на страну и национальную часть.
     * При совпадении кодов (например, +7 у России и Казахстана) выбирается первая страна каталога.
     *
     * @return Пара (страна, цифры национальной части) либо null, если код не найден.
     */
    fun parse(e164Phone: String): Pair<PhoneCountry, String>? {
        if (!e164Phone.startsWith("+")) return null
        val country = all
            .filter { e164Phone.startsWith(it.dialCode) }
            .maxByOrNull { it.dialCode.length }
            ?: return null
        return country to e164Phone.removePrefix(country.dialCode)
    }
}

/** Собирает номер E.164 из кода страны и национальной части. */
fun PhoneCountry.toE164(localDigits: String): String = dialCode + localDigits

/**
 * Форматирует национальную часть номера по маске страны,
 * поддерживая частично введённый номер: «(90) 123-45-67» для 9 цифр.
 */
fun PhoneCountry.formatLocal(digits: String): String {
    val groups = when (localLength) {
        10 -> listOf(3, 3, 2, 2)
        9 -> listOf(2, 3, 2, 2)
        8 -> listOf(2, 3, 3)
        else -> List((localLength + 2) / 3) { 3 }
    }

    val result = StringBuilder()
    var index = 0
    for ((groupIndex, size) in groups.withIndex()) {
        if (index >= digits.length) break
        val part = digits.substring(index, minOf(index + size, digits.length))
        when (groupIndex) {
            0 -> {
                result.append("(").append(part)
                if (part.length == size) result.append(") ")
            }
            1 -> result.append(part)
            else -> result.append("-").append(part)
        }
        index += size
    }
    return result.toString()
}

/** Полное отображение номера: код страны + отформатированная национальная часть. */
fun PhoneCountry.formatFull(digits: String): String = "$dialCode ${formatLocal(digits)}".trim()
