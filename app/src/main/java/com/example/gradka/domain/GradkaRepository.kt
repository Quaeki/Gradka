package com.example.gradka.domain

import kotlinx.coroutines.flow.Flow

/**
 * Основной репозиторий приложения «Грядка».
 *
 * Определяет контракт доменного слоя для работы с заказами, подписками,
 * способами оплаты, адресами и заметками пользователя.
 * Реализация находится в [com.example.gradka.data.GradkaRepositoryImpl].
 */
interface GradkaRepository {

    /**
     * Оформляет новый заказ на сервере на основе содержимого корзины и выбранного адреса.
     * Сумма заказа рассчитывается сервером по серверному прайсу.
     *
     * @param cart Map, где ключ — идентификатор товара, значение — количество.
     * @param addressId Идентификатор адреса доставки из сохранённых адресов пользователя.
     * @throws Exception если сервер недоступен или отклонил заказ.
     */
    suspend fun placeOrder(cart: Map<String, Int>, addressId: String)

    /**
     * Загружает актуальную историю заказов с сервера и обновляет локальный кэш.
     */
    suspend fun syncOrders()

    /**
     * Загружает каталог товаров с сервера (источник — прайс-лист Saby/СБИС)
     * и подменяет встроенный список [PRODUCTS].
     */
    suspend fun syncCatalog()

    /**
     * Возвращает реактивный поток со списком заказов пользователя.
     * Поток обновляется при любом изменении данных в БД.
     */
    fun getOrder(): Flow<List<Order>>

    /**
     * Возвращает реактивный поток со списком активных подписок пользователя.
     */
    fun getSubscriptions(): Flow<List<Subscription>>

    /**
     * Добавляет новую подписку на регулярную доставку товара.
     *
     * @param subscription Данные новой подписки.
     */
    suspend fun addSubscription(subscription: Subscription)

    /**
     * Обновляет параметры существующей подписки (периодичность, количество, статус).
     *
     * @param subscription Подписка с обновлёнными данными.
     */
    suspend fun updateSubscription(subscription: Subscription)

    /**
     * Удаляет подписку по её идентификатору.
     *
     * @param subscriptionId Идентификатор удаляемой подписки.
     */
    suspend fun deleteSubscription(subscriptionId: String)

    /**
     * Возвращает реактивный поток со списком сохранённых способов оплаты.
     */
    fun getPaymentMethods(): Flow<List<PaymentMethod>>

    /**
     * Добавляет новый способ оплаты (банковскую карту).
     *
     * @param paymentMethod Данные карты для сохранения.
     */
    suspend fun addPaymentMethod(paymentMethod: PaymentMethod)

    /**
     * Удаляет способ оплаты по его идентификатору.
     *
     * @param paymentMethodId Идентификатор удаляемой карты.
     */
    suspend fun deletePaymentMethod(paymentMethodId: String)

    /**
     * Возвращает реактивный поток со списком сохранённых адресов доставки.
     */
    fun getAddresses(): Flow<List<Address>>

    /**
     * Добавляет новый адрес доставки.
     *
     * @param address Данные нового адреса.
     */
    fun addAddress(address: Address): Unit

    /**
     * Удаляет адрес доставки по его идентификатору.
     *
     * @param addressId Идентификатор удаляемого адреса.
     */
    fun deleteAddress(addressId: String)

    /**
     * Устанавливает адрес как основной (primary) для доставки.
     * Снимает отметку primary со всех других адресов.
     *
     * @param addressId Идентификатор адреса, который нужно сделать основным.
     */
    fun setPrimaryAddress(addressId: String): Unit

    /**
     * Запрашивает у Yandex MapKit список адресных подсказок по строке поиска.
     *
     * @param query Строка поиска, введённая пользователем.
     * @return Список подсказок адресов с координатами.
     */
    suspend fun suggestAddresses(query: String): List<AddressSuggestion>

    /**
     * Выполняет обратное геокодирование: определяет текстовый адрес по координатам.
     *
     * @param lat Широта точки.
     * @param lon Долгота точки.
     * @return Читаемый адрес в виде строки.
     */
    suspend fun reverseGeocode(lat: Double, lon: Double): String

    /**
     * Создаёт новую заметку пользователя.
     *
     * @param title Заголовок заметки.
     * @param content Текст заметки.
     */
    suspend fun addNote(title: String, content: String)

    /**
     * Удаляет заметку по её идентификатору.
     *
     * @param noteId Идентификатор удаляемой заметки.
     */
    suspend fun deleteNote(noteId: Int)

    /**
     * Обновляет содержимое существующей заметки.
     *
     * @param note Заметка с обновлёнными данными.
     */
    suspend fun editNote(note: Note)

    /**
     * Возвращает реактивный поток со всеми заметками пользователя.
     */
    fun getAllNotes(): Flow<List<Note>>
}
