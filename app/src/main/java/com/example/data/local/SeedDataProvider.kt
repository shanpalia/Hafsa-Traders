package com.example.data.local

object SeedDataProvider {

    fun getDefaultCategories(): List<CategoryEntity> {
        return emptyList()
    }

    fun getDefaultItems(): List<ItemEntity> {
        return emptyList()
    }

    fun getDefaultSettings(): List<AdminSettingEntity> {
        return listOf(
            AdminSettingEntity("shop_name", "HAFSA TRADERS"),
            AdminSettingEntity("shop_subtitle", "PHOTOCOPY • LAMINATION • PHOTO PRINT"),
            AdminSettingEntity("shop_phone", "+91 98765 43210"),
            AdminSettingEntity("shop_whatsapp", "+91 98765 43210"),
            AdminSettingEntity("shop_address", "Shop No. 4, Ground Floor, Central Market Road, Near City Post Office, Delhi - 110006"),
            AdminSettingEntity("shop_hours", "Mon - Sat: 9:00 AM - 9:30 PM | Sun: 10:00 AM - 4:00 PM"),
            AdminSettingEntity("upi_id", "hafsatraders@okhdfcbank"),
            AdminSettingEntity("upi_name", "Hafsa Traders"),
            AdminSettingEntity("admin_pin", "1234"),
            AdminSettingEntity("payment_gateway_enabled", "true"),
            AdminSettingEntity("upi_qr_enabled", "true"),
            AdminSettingEntity("counter_pay_enabled", "true")
        )
    }

    fun getDefaultOrders(): Triple<List<OrderEntity>, List<OrderItemEntity>, List<OrderFileEntity>> {
        return Triple(emptyList(), emptyList(), emptyList())
    }

    fun getDefaultNotifications(): List<NotificationEntity> {
        return emptyList()
    }
}
