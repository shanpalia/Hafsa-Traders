package com.example.data.local

object SeedDataProvider {

    fun getDefaultCategories(): List<CategoryEntity> = listOf(
        CategoryEntity("photocopy", "Photocopy", "photocopy", displayOrder = 1),
        CategoryEntity("print", "Document Print", "document", displayOrder = 2),
        CategoryEntity("photo_print", "Photo Print", "photo", displayOrder = 3),
        CategoryEntity("passport", "Passport Photo", "passport", displayOrder = 4),
        CategoryEntity("lamination", "Lamination", "lamination", displayOrder = 5),
        CategoryEntity("binding", "Binding", "binding", displayOrder = 6),
        CategoryEntity("scan", "Scan & PDF", "document", displayOrder = 7)
    )

    fun getDefaultItems(): List<ItemEntity> = listOf(
        ItemEntity("bw_copy", "photocopy", "Black & White Photocopy", "A4 photocopy", 1.0, "Per Page"),
        ItemEntity("color_copy", "photocopy", "Colour Photocopy", "A4 colour photocopy", 5.0, "Per Page"),
        ItemEntity("bw_print", "print", "Black & White Print", "Print PDF or document", 2.0, "Per Page"),
        ItemEntity("color_print", "print", "Colour Print", "Colour document print", 8.0, "Per Page"),
        ItemEntity("photo_4x6", "photo_print", "4 x 6 Photo Print", "Glossy photo print", 15.0, "Per Photo"),
        ItemEntity("passport_set", "passport", "Passport Photo Set", "Standard passport photo set", 50.0, "Per Set", uploadRequired = true),
        ItemEntity("lam_a4", "lamination", "A4 Lamination", "Transparent A4 lamination", 30.0, "Per Sheet"),
        ItemEntity("spiral", "binding", "Spiral Binding", "Document spiral binding", 40.0, "Per Document"),
        ItemEntity("scan_pdf", "scan", "Document Scan to PDF", "Scan documents and create PDF", 10.0, "Per Page")
    )

    fun getDefaultSettings(): List<AdminSettingEntity> {
        return listOf(
            AdminSettingEntity("shop_name", "HAFSA TRADERS"),
            AdminSettingEntity("shop_subtitle", "PHOTOCOPY • LAMINATION • PHOTO PRINT"),
            AdminSettingEntity("shop_phone", "+91 98765 43210"),
            AdminSettingEntity("shop_whatsapp", "+91 98765 43210"),
            AdminSettingEntity("shop_address", "Chaman Chauraha Palia Kalan - 262902"),
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
