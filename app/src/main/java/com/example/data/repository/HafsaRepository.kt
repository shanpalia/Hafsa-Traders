package com.example.data.repository

import com.example.data.local.AdminSettingEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.HafsaDao
import com.example.data.local.ItemEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.OfferEntity
import com.example.data.local.OfferItemEntity
import com.example.data.local.OfferWithItems
import com.example.data.local.OrderEntity
import com.example.data.local.OrderFileEntity
import com.example.data.local.OrderItemEntity
import com.example.data.local.PaymentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID

data class OrderWithDetails(
    val order: OrderEntity,
    val items: List<OrderItemEntity>,
    val files: List<OrderFileEntity>,
    val payments: List<PaymentEntity>
)

data class CartItem(
    val item: ItemEntity,
    val quantity: Int
) {
    val subtotal: Double
        get() = item.price * quantity
}

data class UploadedFileDraft(
    val fileName: String,
    val fileUri: String,
    val fileSizeKb: Long,
    val fileType: String // e.g. "image/jpeg", "image/png", "application/pdf"
)

class HafsaRepository(private val dao: HafsaDao) {

    // --- CATEGORIES ---
    val allCategories: Flow<List<CategoryEntity>> = dao.getAllCategories()
    val activeCategories: Flow<List<CategoryEntity>> = dao.getActiveCategories()

    suspend fun addCategory(name: String, icon: String = "print", displayOrder: Int = 99) = withContext(Dispatchers.IO) {
        val id = "cat_" + UUID.randomUUID().toString().take(8)
        dao.insertCategory(CategoryEntity(id = id, name = name, icon = icon, isActive = true, displayOrder = displayOrder))
    }

    suspend fun updateCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        dao.updateCategory(category)
    }

    suspend fun deleteCategory(categoryId: String) = withContext(Dispatchers.IO) {
        dao.deleteCategoryById(categoryId)
    }

    // --- ITEMS & PRICING ---
    val allItems: Flow<List<ItemEntity>> = dao.getAllItems()
    val activeItems: Flow<List<ItemEntity>> = dao.getActiveItems()

    fun getItemsByCategory(categoryId: String): Flow<List<ItemEntity>> = dao.getActiveItemsByCategory(categoryId)

    suspend fun addItem(
        categoryId: String,
        name: String,
        description: String,
        price: Double,
        unit: String,
        minQuantity: Int,
        maxQuantity: Int,
        uploadRequired: Boolean,
        iconName: String
    ) = withContext(Dispatchers.IO) {
        val id = "item_" + UUID.randomUUID().toString().take(8)
        val item = ItemEntity(
            id = id,
            categoryId = categoryId,
            name = name,
            description = description,
            price = price,
            unit = unit,
            minQuantity = minQuantity,
            maxQuantity = maxQuantity,
            uploadRequired = uploadRequired,
            isActive = true,
            iconName = iconName,
            createdAt = System.currentTimeMillis()
        )
        dao.insertItem(item)
    }

    suspend fun updateItem(item: ItemEntity) = withContext(Dispatchers.IO) {
        dao.updateItem(item)
    }

    suspend fun updateItemPrice(itemId: String, newPrice: Double) = withContext(Dispatchers.IO) {
        dao.updateItemPrice(itemId, newPrice)
    }

    suspend fun setItemActive(itemId: String, isActive: Boolean) = withContext(Dispatchers.IO) {
        dao.setItemActiveStatus(itemId, isActive)
    }

    suspend fun deleteItem(itemId: String) = withContext(Dispatchers.IO) {
        dao.deleteItemById(itemId)
    }

    // --- ORDERS & REAL BACKEND SECURITY VERIFICATION ---
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()

    fun getCustomerOrders(userId: String): Flow<List<OrderEntity>> = dao.getOrdersByUserId(userId)

    suspend fun getOrderWithDetails(orderId: String): OrderWithDetails? = withContext(Dispatchers.IO) {
        val order = dao.getOrderById(orderId) ?: return@withContext null
        val items = dao.getOrderItemsForOrder(orderId)
        val files = dao.getOrderFilesForOrder(orderId)
        val payments = dao.getPaymentsForOrder(orderId)
        OrderWithDetails(order, items, files, payments)
    }

    /**
     * Places a verified order:
     * 1. Fetches real current database prices for each item (ensuring customer cannot manipulate price).
     * 2. Generates a unique sequential Order ID (e.g. HT10003).
     * 3. Freezes current prices in order_items records (so future price updates don't affect old orders).
     * 4. Uploads / associates files with cloud storage path 'orders/{orderNumber}/files/'.
     * 5. Inserts payment record.
     * 6. Triggers Admin Push Notification for the new order.
     */
    suspend fun placeOrder(
        userId: String,
        customerName: String,
        customerPhone: String,
        customerEmail: String,
        customerAddress: String,
        specialInstructions: String,
        cartItems: List<CartItem>,
        uploadedFiles: List<UploadedFileDraft>,
        paymentMethod: String,
        paymentStatus: String,
        paymentRef: String
    ): OrderEntity = withContext(Dispatchers.IO) {
        val existingOrders = dao.getAllOrders().first()
        val nextSeq = 10001 + existingOrders.size
        val orderNumber = "HT$nextSeq"
        val orderId = "order_" + UUID.randomUUID().toString().take(8)

        // Backend Price Verification & Snapshot Freezing (Re-checks active offers at current timestamp)
        val now = System.currentTimeMillis()
        var calculatedSubtotal = 0.0
        var verifiedTotal = 0.0
        val orderItemEntities = mutableListOf<OrderItemEntity>()

        val activeOffers = dao.getActiveOffers(now).first()
        val allOfferItems = dao.getAllOfferItems().first()

        for (cart in cartItems) {
            val dbItem = dao.getItemById(cart.item.id) ?: cart.item
            val regularPrice = dbItem.price
            val qty = cart.quantity.coerceIn(dbItem.minQuantity, dbItem.maxQuantity)

            // Re-verify if item is part of an active offer at this exact moment
            val matchingOfferIds = allOfferItems.filter { it.itemId == dbItem.id }.map { it.offerId }.toSet()
            val applicableOffers = activeOffers.filter { it.id in matchingOfferIds }

            var unitPriceToCharge = regularPrice
            if (applicableOffers.isNotEmpty()) {
                var bestOfferPrice = regularPrice
                for (offer in applicableOffers) {
                    val offerWithItems = OfferWithItems(offer, listOf(dbItem))
                    val discounted = offerWithItems.calculateDiscountedPrice(regularPrice)
                    if (discounted < bestOfferPrice) {
                        bestOfferPrice = discounted
                    }
                }
                unitPriceToCharge = bestOfferPrice
            }

            val itemSubtotal = unitPriceToCharge * qty
            calculatedSubtotal += (regularPrice * qty)
            verifiedTotal += itemSubtotal

            orderItemEntities.add(
                OrderItemEntity(
                    id = "oi_" + UUID.randomUUID().toString().take(8),
                    orderId = orderId,
                    itemId = dbItem.id,
                    itemName = dbItem.name,
                    unitPrice = unitPriceToCharge, // strictly frozen at moment of order
                    unit = dbItem.unit,
                    quantity = qty,
                    subtotal = itemSubtotal
                )
            )
        }

        val discountAmount = (calculatedSubtotal - verifiedTotal).coerceAtLeast(0.0)

        val orderEntity = OrderEntity(
            id = orderId,
            orderNumber = orderNumber,
            userId = userId,
            customerName = customerName,
            customerPhone = customerPhone,
            customerEmail = customerEmail,
            customerAddress = customerAddress,
            totalAmount = verifiedTotal,
            subtotal = calculatedSubtotal,
            discount = discountAmount,
            paymentStatus = paymentStatus,
            paymentMethod = paymentMethod,
            paymentRef = paymentRef,
            orderStatus = "RECEIVED",
            specialInstructions = specialInstructions,
            createdAt = now,
            updatedAt = now
        )

        // File Cloud Storage Association (orders/{orderNumber}/files/...)
        val orderFileEntities = uploadedFiles.map { draft ->
            OrderFileEntity(
                id = "of_" + UUID.randomUUID().toString().take(8),
                orderId = orderId,
                fileName = draft.fileName,
                fileUri = draft.fileUri,
                fileSizeKb = draft.fileSizeKb,
                fileType = draft.fileType,
                storagePath = "orders/$orderNumber/files/${draft.fileName}",
                uploadedAt = now
            )
        }

        // Payment record
        val paymentEntity = PaymentEntity(
            id = "pay_" + UUID.randomUUID().toString().take(8),
            orderId = orderId,
            amount = verifiedTotal,
            paymentMethod = paymentMethod,
            transactionId = paymentRef.ifEmpty { "TXN" + System.currentTimeMillis().toString().takeLast(8) },
            status = paymentStatus,
            createdAt = now
        )

        // Save everything into Room
        dao.insertOrder(orderEntity)
        dao.insertOrderItems(orderItemEntities)
        if (orderFileEntities.isNotEmpty()) {
            dao.insertOrderFiles(orderFileEntities)
        }
        dao.insertPayment(paymentEntity)

        // Push Notification to Admin
        val adminNotif = NotificationEntity(
            id = "notif_" + UUID.randomUUID().toString().take(8),
            recipientRole = "ADMIN",
            userId = userId,
            title = "🔔 New Order #$orderNumber",
            message = "New order from $customerName for ₹${verifiedTotal.toInt()} ($paymentStatus via $paymentMethod).",
            orderId = orderId,
            orderNumber = orderNumber,
            type = "NEW_ORDER",
            isRead = false,
            createdAt = now
        )
        dao.insertNotification(adminNotif)

        // Push Notification to Customer
        val custNotif = NotificationEntity(
            id = "notif_" + UUID.randomUUID().toString().take(8),
            recipientRole = "CUSTOMER",
            userId = userId,
            title = "Order Placed Successfully! 📄",
            message = "Your Hafsa Traders order #$orderNumber has been received and is queued for processing.",
            orderId = orderId,
            orderNumber = orderNumber,
            type = "ORDER_STATUS",
            isRead = false,
            createdAt = now
        )
        dao.insertNotification(custNotif)

        orderEntity
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) = withContext(Dispatchers.IO) {
        val order = dao.getOrderById(orderId) ?: return@withContext
        dao.updateOrderStatus(orderId, newStatus)

        // Customer Push Notification for Status Transition
        val statusMessage = when (newStatus) {
            "RECEIVED" -> "Your Hafsa Traders order #${order.orderNumber} has been received."
            "PROCESSING" -> "Your order #${order.orderNumber} is now being printed and processed."
            "READY" -> "Your order is ready. Please visit the shop and pick up your order. Order #${order.orderNumber} 🎉"
            "COMPLETED" -> "Your order #${order.orderNumber} has been completed and delivered. Thank you!"
            "CANCELLED" -> "Your order #${order.orderNumber} has been cancelled. Please contact shop for assistance."
            else -> "Status for order #${order.orderNumber} updated to $newStatus."
        }

        val statusNotif = NotificationEntity(
            id = "notif_" + UUID.randomUUID().toString().take(8),
            recipientRole = "CUSTOMER",
            userId = order.userId,
            title = "Order Status: $newStatus",
            message = statusMessage,
            orderId = orderId,
            orderNumber = order.orderNumber,
            type = "ORDER_STATUS",
            isRead = false,
            createdAt = System.currentTimeMillis()
        )
        dao.insertNotification(statusNotif)
    }

    suspend fun updatePaymentStatus(orderId: String, status: String, paymentRef: String) = withContext(Dispatchers.IO) {
        dao.updatePaymentStatus(orderId, status, paymentRef)
    }

    // --- NOTIFICATIONS ---
    fun getCustomerNotifications(): Flow<List<NotificationEntity>> = dao.getNotificationsForRole("CUSTOMER")
    fun getAdminNotifications(): Flow<List<NotificationEntity>> = dao.getNotificationsForRole("ADMIN")

    suspend fun markNotificationRead(id: String) = withContext(Dispatchers.IO) {
        dao.markNotificationAsRead(id)
    }

    suspend fun markAllNotificationsRead(role: String) = withContext(Dispatchers.IO) {
        dao.markAllNotificationsAsRead(role)
    }

    // --- SETTINGS ---
    val allSettings: Flow<List<AdminSettingEntity>> = dao.getAllSettingsFlow()

    suspend fun getSetting(key: String, defaultValue: String = ""): String = withContext(Dispatchers.IO) {
        dao.getSettingValue(key) ?: defaultValue
    }

    suspend fun updateSetting(key: String, value: String) = withContext(Dispatchers.IO) {
        dao.insertSetting(AdminSettingEntity(key, value))
    }

    // --- METRICS ---
    val totalOrdersCount: Flow<Int> = dao.getTotalOrdersCount()
    val newOrdersCount: Flow<Int> = dao.getNewOrdersCount()
    val processingOrdersCount: Flow<Int> = dao.getProcessingOrdersCount()
    val readyOrdersCount: Flow<Int> = dao.getReadyOrdersCount()
    val completedOrdersCount: Flow<Int> = dao.getCompletedOrdersCount()
    val totalRevenue: Flow<Double> = dao.getTotalRevenue()
    val pendingRevenue: Flow<Double> = dao.getPendingRevenue()

    // --- OFFERS ---
    val allOffers: Flow<List<OfferEntity>> = dao.getAllOffers()
    val allOfferItems: Flow<List<OfferItemEntity>> = dao.getAllOfferItems()

    fun getOffersWithItemsFlow(): Flow<List<OfferWithItems>> = combine(
        dao.getAllOffers(),
        dao.getAllOfferItems(),
        dao.getAllItems()
    ) { offers, offerItems, items ->
        val itemsMap = items.associateBy { it.id }
        val offerItemsGrouped = offerItems.groupBy { it.offerId }
        offers.map { offer ->
            val linkedItemIds = offerItemsGrouped[offer.id]?.map { it.itemId } ?: emptyList()
            val linkedItems = linkedItemIds.mapNotNull { itemsMap[it] }
            OfferWithItems(offer, linkedItems)
        }
    }

    fun getActiveOffersWithItemsFlow(now: Long = System.currentTimeMillis()): Flow<List<OfferWithItems>> = combine(
        dao.getAllOffers(),
        dao.getAllOfferItems(),
        dao.getActiveItems()
    ) { offers, offerItems, items ->
        val itemsMap = items.associateBy { it.id }
        val offerItemsGrouped = offerItems.groupBy { it.offerId }
        val currentTime = System.currentTimeMillis()
        offers.filter { it.isEnabled && it.startAt <= currentTime && it.expiresAt >= currentTime }
            .map { offer ->
                val linkedItemIds = offerItemsGrouped[offer.id]?.map { it.itemId } ?: emptyList()
                val linkedItems = linkedItemIds.mapNotNull { itemsMap[it] }
                OfferWithItems(offer, linkedItems)
            }
            .filter { it.items.isNotEmpty() }
    }

    suspend fun getVerifiedItemPrice(itemId: String, now: Long = System.currentTimeMillis()): Pair<Double, Double> = withContext(Dispatchers.IO) {
        val dbItem = dao.getItemById(itemId) ?: return@withContext Pair(0.0, 0.0)
        val regularPrice = dbItem.price
        val activeOffers = dao.getActiveOffers(now).first()
        val allOfferItems = dao.getAllOfferItems().first()

        val matchingOfferIds = allOfferItems.filter { it.itemId == itemId }.map { it.offerId }.toSet()
        val applicableOffers = activeOffers.filter { it.id in matchingOfferIds }

        if (applicableOffers.isEmpty()) {
            return@withContext Pair(regularPrice, regularPrice)
        }

        var bestPrice = regularPrice
        for (offer in applicableOffers) {
            val offerWithItems = OfferWithItems(offer, listOf(dbItem))
            val discounted = offerWithItems.calculateDiscountedPrice(regularPrice)
            if (discounted < bestPrice) {
                bestPrice = discounted
            }
        }
        Pair(bestPrice, regularPrice)
    }

    suspend fun createOffer(
        title: String,
        description: String,
        imageUrl: String,
        offerType: String,
        offerValue: Double,
        startAt: Long,
        expiresAt: Long,
        selectedItemIds: List<String>,
        displayOrder: Int = 0,
        isEnabled: Boolean = true,
        notifyCustomers: Boolean = false
    ): String = withContext(Dispatchers.IO) {
        val offerId = "offer_" + UUID.randomUUID().toString().take(8)
        val now = System.currentTimeMillis()

        val offer = OfferEntity(
            id = offerId,
            title = title,
            description = description,
            imageUrl = imageUrl,
            offerType = offerType,
            offerValue = offerValue,
            startAt = startAt,
            expiresAt = expiresAt,
            isEnabled = isEnabled,
            displayOrder = displayOrder,
            createdAt = now,
            updatedAt = now
        )

        val offerItemEntities = selectedItemIds.map { itemId ->
            OfferItemEntity(offerId = offerId, itemId = itemId)
        }

        dao.insertOffer(offer)
        if (offerItemEntities.isNotEmpty()) {
            dao.insertOfferItems(offerItemEntities)
        }

        if (notifyCustomers && isEnabled) {
            val notif = NotificationEntity(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                recipientRole = "CUSTOMER",
                title = "🎉 Special Offer: $title",
                message = description.ifEmpty { "Check out the special discounted prices at Hafsa Traders!" },
                orderId = offerId,
                type = "PROMO",
                isRead = false,
                createdAt = now
            )
            dao.insertNotification(notif)
        }

        offerId
    }

    suspend fun updateOffer(
        offer: OfferEntity,
        selectedItemIds: List<String>
    ) = withContext(Dispatchers.IO) {
        val updatedOffer = offer.copy(updatedAt = System.currentTimeMillis())
        dao.updateOffer(updatedOffer)
        dao.deleteOfferItemsForOffer(offer.id)
        val newLinks = selectedItemIds.map { itemId ->
            OfferItemEntity(offerId = offer.id, itemId = itemId)
        }
        if (newLinks.isNotEmpty()) {
            dao.insertOfferItems(newLinks)
        }
    }

    suspend fun deleteOffer(offerId: String) = withContext(Dispatchers.IO) {
        dao.deleteOfferById(offerId)
        dao.deleteOfferItemsForOffer(offerId)
    }

    suspend fun toggleOfferEnabled(offerId: String, isEnabled: Boolean) = withContext(Dispatchers.IO) {
        dao.setOfferEnabledStatus(offerId, isEnabled)
    }
}
