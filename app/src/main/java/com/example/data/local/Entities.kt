package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val email: String = "",
    val address: String = "",
    val role: String = "CUSTOMER" // CUSTOMER or ADMIN
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val icon: String = "print",
    val isActive: Boolean = true,
    val displayOrder: Int = 0
)

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val name: String,
    val description: String,
    val price: Double,
    val unit: String, // Per Page, Per Copy, Per Photo, Per Sheet, Per Set, Per Document
    val minQuantity: Int = 1,
    val maxQuantity: Int = 500,
    val uploadRequired: Boolean = true,
    val isActive: Boolean = true,
    val iconName: String = "document",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: String, // HT10001, HT10002 etc.
    val userId: String,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String = "",
    val customerAddress: String = "",
    val totalAmount: Double,
    val subtotal: Double,
    val discount: Double = 0.0,
    val paymentStatus: String, // PAID, PENDING, FAILED
    val paymentMethod: String, // UPI Direct, UPI QR, Online Gateway, Counter Cash
    val paymentRef: String = "",
    val orderStatus: String, // RECEIVED, PROCESSING, READY, COMPLETED, CANCELLED
    val specialInstructions: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val itemId: String,
    val itemName: String,
    val unitPrice: Double, // Snapshot of item price at time order was placed
    val unit: String,
    val quantity: Int,
    val subtotal: Double
)

@Entity(tableName = "order_files")
data class OrderFileEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val fileName: String,
    val fileUri: String,
    val fileSizeKb: Long,
    val fileType: String, // JPG, PNG, PDF
    val storagePath: String, // orders/{orderId}/files/{fileName}
    val uploadedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_status_history")
data class OrderStatusHistoryEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val status: String, // RECEIVED, PROCESSING, READY, COMPLETED, CANCELLED
    val message: String,
    val changedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val amount: Double,
    val paymentMethod: String,
    val transactionId: String,
    val status: String, // PAID, PENDING, FAILED
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val recipientRole: String, // CUSTOMER, ADMIN, ALL
    val userId: String = "",
    val title: String,
    val message: String,
    val orderId: String = "",
    val orderNumber: String = "",
    val type: String = "ORDER_STATUS", // ORDER_STATUS, NEW_ORDER, PAYMENT, PROMO
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_settings")
data class AdminSettingEntity(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(tableName = "offers")
data class OfferEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val imageUrl: String = "", // Cloud storage / URI / asset
    val offerType: String, // FIXED_PRICE, PERCENTAGE_DISCOUNT, FIXED_DISCOUNT
    val offerValue: Double, // e.g. 50.0 for ₹50, 20.0 for 20%, 30.0 for ₹30 off
    val startAt: Long, // timestamp ms
    val expiresAt: Long, // timestamp ms
    val isEnabled: Boolean = true,
    val displayOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun getStatus(now: Long = System.currentTimeMillis()): OfferStatus {
        if (!isEnabled) return OfferStatus.DISABLED
        if (now < startAt) return OfferStatus.SCHEDULED
        if (now > expiresAt) return OfferStatus.EXPIRED
        return OfferStatus.ACTIVE
    }
}

@Entity(
    tableName = "offer_items",
    primaryKeys = ["offerId", "itemId"]
)
data class OfferItemEntity(
    val offerId: String,
    val itemId: String
)

enum class OfferStatus {
    SCHEDULED,
    ACTIVE,
    EXPIRED,
    DISABLED
}

data class OfferWithItems(
    val offer: OfferEntity,
    val items: List<ItemEntity>
) {
    fun getStatus(now: Long = System.currentTimeMillis()): OfferStatus {
        if (!offer.isEnabled) return OfferStatus.DISABLED
        if (now < offer.startAt) return OfferStatus.SCHEDULED
        if (now > offer.expiresAt) return OfferStatus.EXPIRED
        return OfferStatus.ACTIVE
    }

    val isActive: Boolean
        get() = getStatus() == OfferStatus.ACTIVE

    fun calculateDiscountedPrice(originalPrice: Double): Double {
        return when (offer.offerType) {
            "FIXED_PRICE" -> offer.offerValue.coerceAtLeast(0.0)
            "PERCENTAGE_DISCOUNT" -> {
                val discount = (originalPrice * (offer.offerValue / 100.0))
                (originalPrice - discount).coerceAtLeast(0.0)
            }
            "FIXED_DISCOUNT" -> (originalPrice - offer.offerValue).coerceAtLeast(0.0)
            else -> originalPrice
        }
    }

    fun getDiscountLabel(originalPrice: Double? = null): String {
        return when (offer.offerType) {
            "FIXED_PRICE" -> "Special Price ₹${offer.offerValue.toInt()}"
            "PERCENTAGE_DISCOUNT" -> "${offer.offerValue.toInt()}% OFF"
            "FIXED_DISCOUNT" -> "Flat ₹${offer.offerValue.toInt()} OFF"
            else -> "Special Offer"
        }
    }
}
