package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HafsaDao {

    // --- USERS ---
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // --- CATEGORIES ---
    @Query("SELECT * FROM categories ORDER BY displayOrder ASC, name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY displayOrder ASC, name ASC")
    fun getActiveCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: String): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategoryById(id: String)

    // --- ITEMS ---
    @Query("SELECT * FROM items ORDER BY categoryId ASC, name ASC")
    fun getAllItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE categoryId = :categoryId AND isActive = 1")
    fun getActiveItemsByCategory(categoryId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: String): ItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity)

    @Update
    suspend fun updateItem(item: ItemEntity)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun deleteItemById(id: String)

    @Query("UPDATE items SET isActive = :isActive WHERE id = :id")
    suspend fun setItemActiveStatus(id: String, isActive: Boolean)

    @Query("UPDATE items SET price = :newPrice WHERE id = :id")
    suspend fun updateItemPrice(id: String, newPrice: Double)

    // --- ORDERS ---
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getOrdersByUserId(userId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE orderNumber = :orderNumber")
    suspend fun getOrderByNumber(orderNumber: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("UPDATE orders SET orderStatus = :status, updatedAt = :timestamp WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET paymentStatus = :paymentStatus, paymentRef = :paymentRef, updatedAt = :timestamp WHERE id = :orderId")
    suspend fun updatePaymentStatus(orderId: String, paymentStatus: String, paymentRef: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrderById(orderId: String)

    // --- ORDER ITEMS ---
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItemsForOrder(orderId: String): List<OrderItemEntity>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItemsForOrderFlow(orderId: String): Flow<List<OrderItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    // --- ORDER FILES ---
    @Query("SELECT * FROM order_files WHERE orderId = :orderId")
    suspend fun getOrderFilesForOrder(orderId: String): List<OrderFileEntity>

    @Query("SELECT * FROM order_files WHERE orderId = :orderId")
    fun getOrderFilesForOrderFlow(orderId: String): Flow<List<OrderFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderFiles(files: List<OrderFileEntity>)

    // --- ORDER STATUS HISTORY ---
    @Query("SELECT * FROM order_status_history WHERE orderId = :orderId ORDER BY changedAt ASC")
    suspend fun getOrderStatusHistory(orderId: String): List<OrderStatusHistoryEntity>

    @Query("SELECT * FROM order_status_history WHERE orderId = :orderId ORDER BY changedAt ASC")
    fun getOrderStatusHistoryFlow(orderId: String): Flow<List<OrderStatusHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderStatusHistory(history: OrderStatusHistoryEntity)

    // --- PAYMENTS ---
    @Query("SELECT * FROM payments WHERE orderId = :orderId ORDER BY createdAt DESC")
    suspend fun getPaymentsForOrder(orderId: String): List<PaymentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    // --- NOTIFICATIONS ---
    @Query("SELECT * FROM notifications WHERE recipientRole = :role OR recipientRole = 'ALL' ORDER BY createdAt DESC")
    fun getNotificationsForRole(role: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE (recipientRole = 'CUSTOMER' AND userId = :userId) OR recipientRole = 'ALL' ORDER BY createdAt DESC")
    fun getCustomerNotificationsForUser(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE recipientRole = :role OR recipientRole = 'ALL'")
    suspend fun markAllNotificationsAsRead(role: String)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: String)

    // --- ADMIN SETTINGS ---
    @Query("SELECT * FROM admin_settings")
    fun getAllSettingsFlow(): Flow<List<AdminSettingEntity>>

    @Query("SELECT value FROM admin_settings WHERE `key` = :key")
    suspend fun getSettingValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: AdminSettingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: List<AdminSettingEntity>)

    // --- COUNTERS / AGGREGATES FOR ADMIN DASHBOARD ---
    @Query("SELECT COUNT(*) FROM orders")
    fun getTotalOrdersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM orders WHERE orderStatus = 'RECEIVED'")
    fun getNewOrdersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM orders WHERE orderStatus = 'PROCESSING'")
    fun getProcessingOrdersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM orders WHERE orderStatus = 'READY'")
    fun getReadyOrdersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM orders WHERE orderStatus = 'COMPLETED'")
    fun getCompletedOrdersCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(totalAmount), 0.0) FROM orders WHERE paymentStatus = 'PAID'")
    fun getTotalRevenue(): Flow<Double>

    @Query("SELECT COALESCE(SUM(totalAmount), 0.0) FROM orders WHERE paymentStatus = 'PENDING'")
    fun getPendingRevenue(): Flow<Double>

    // --- OFFERS ---
    @Query("SELECT * FROM offers ORDER BY displayOrder ASC, createdAt DESC")
    fun getAllOffers(): Flow<List<OfferEntity>>

    @Query("SELECT * FROM offers WHERE isEnabled = 1 AND startAt <= :now AND expiresAt >= :now ORDER BY displayOrder ASC, createdAt DESC")
    fun getActiveOffers(now: Long = System.currentTimeMillis()): Flow<List<OfferEntity>>

    @Query("SELECT * FROM offers WHERE id = :offerId")
    suspend fun getOfferById(offerId: String): OfferEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffer(offer: OfferEntity)

    @Update
    suspend fun updateOffer(offer: OfferEntity)

    @Query("DELETE FROM offers WHERE id = :offerId")
    suspend fun deleteOfferById(offerId: String)

    @Query("UPDATE offers SET isEnabled = :isEnabled, updatedAt = :now WHERE id = :offerId")
    suspend fun setOfferEnabledStatus(offerId: String, isEnabled: Boolean, now: Long = System.currentTimeMillis())

    // --- OFFER ITEMS ---
    @Query("SELECT * FROM offer_items WHERE offerId = :offerId")
    suspend fun getOfferItemsForOffer(offerId: String): List<OfferItemEntity>

    @Query("SELECT * FROM offer_items")
    fun getAllOfferItems(): Flow<List<OfferItemEntity>>

    @Query("SELECT itemId FROM offer_items WHERE offerId = :offerId")
    suspend fun getItemIdsForOffer(offerId: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfferItems(items: List<OfferItemEntity>)

    @Query("DELETE FROM offer_items WHERE offerId = :offerId")
    suspend fun deleteOfferItemsForOffer(offerId: String)

    @Query("DELETE FROM offer_items WHERE itemId = :itemId")
    suspend fun deleteOfferItemsByItemId(itemId: String)
}
