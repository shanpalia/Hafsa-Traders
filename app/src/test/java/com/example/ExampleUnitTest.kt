package com.example

import com.example.data.local.ItemEntity
import com.example.data.local.OfferEntity
import com.example.data.local.OfferStatus
import com.example.data.local.OfferWithItems
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testOfferStatus_Active_Scheduled_Expired_Disabled() {
        val now = 1000000L
        val activeOffer = OfferEntity(
            id = "o1",
            title = "Test Active",
            description = "Active deal",
            offerType = "PERCENTAGE_DISCOUNT",
            offerValue = 20.0,
            startAt = now - 10000,
            expiresAt = now + 10000,
            isEnabled = true
        )
        assertEquals(OfferStatus.ACTIVE, activeOffer.getStatus(now))

        val scheduledOffer = activeOffer.copy(startAt = now + 5000)
        assertEquals(OfferStatus.SCHEDULED, scheduledOffer.getStatus(now))

        val expiredOffer = activeOffer.copy(expiresAt = now - 1000)
        assertEquals(OfferStatus.EXPIRED, expiredOffer.getStatus(now))

        val disabledOffer = activeOffer.copy(isEnabled = false)
        assertEquals(OfferStatus.DISABLED, disabledOffer.getStatus(now))
    }

    @Test
    fun testOfferDiscountCalculations() {
        val testItem = ItemEntity(
            id = "item_photo",
            categoryId = "cat_photo",
            name = "Passport Photos (8 Copies)",
            description = "8 studio passport size photos",
            price = 60.0,
            unit = "set of 8"
        )

        // 1. Percentage discount: 20% off on Rs 60 -> Rs 48
        val percentOffer = OfferEntity(
            id = "o_pct",
            title = "20% Off",
            description = "Save 20%",
            offerType = "PERCENTAGE_DISCOUNT",
            offerValue = 20.0,
            startAt = 0,
            expiresAt = Long.MAX_VALUE,
            isEnabled = true
        )
        val percentWithItems = OfferWithItems(percentOffer, listOf(testItem))
        assertEquals(48.0, percentWithItems.calculateDiscountedPrice(60.0), 0.001)

        // 2. Fixed Price discount: set price directly to Rs 35
        val fixedPriceOffer = OfferEntity(
            id = "o_fix",
            title = "Special Rs 35 Price",
            description = "Flat Rs 35",
            offerType = "FIXED_PRICE",
            offerValue = 35.0,
            startAt = 0,
            expiresAt = Long.MAX_VALUE,
            isEnabled = true
        )
        val fixedPriceWithItems = OfferWithItems(fixedPriceOffer, listOf(testItem))
        assertEquals(35.0, fixedPriceWithItems.calculateDiscountedPrice(60.0), 0.001)

        // 3. Fixed Discount: Flat Rs 15 off Rs 60 -> Rs 45
        val fixedDiscountOffer = OfferEntity(
            id = "o_flat",
            title = "Flat Rs 15 Off",
            description = "Rs 15 off",
            offerType = "FIXED_DISCOUNT",
            offerValue = 15.0,
            startAt = 0,
            expiresAt = Long.MAX_VALUE,
            isEnabled = true
        )
        val fixedDiscountWithItems = OfferWithItems(fixedDiscountOffer, listOf(testItem))
        assertEquals(45.0, fixedDiscountWithItems.calculateDiscountedPrice(60.0), 0.001)

        // 4. Clamping test: discount cannot make price negative
        val excessiveDiscountOffer = OfferEntity(
            id = "o_excess",
            title = "Excessive discount",
            description = "Too high",
            offerType = "FIXED_DISCOUNT",
            offerValue = 100.0,
            startAt = 0,
            expiresAt = Long.MAX_VALUE,
            isEnabled = true
        )
        val excessWithItems = OfferWithItems(excessiveDiscountOffer, listOf(testItem))
        assertEquals(0.0, excessWithItems.calculateDiscountedPrice(60.0), 0.001)
    }
}
