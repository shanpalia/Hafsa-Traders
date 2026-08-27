package com.example.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ItemEntity
import com.example.data.local.OfferEntity
import com.example.data.local.OfferStatus
import com.example.data.local.OfferWithItems
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminOffersScreen(
    offersWithItems: List<OfferWithItems>,
    allItems: List<ItemEntity>,
    onCreateOffer: (
        title: String,
        description: String,
        imageUrl: String,
        offerType: String,
        offerValue: Double,
        startAt: Long,
        expiresAt: Long,
        selectedItemIds: List<String>,
        displayOrder: Int,
        isEnabled: Boolean,
        notifyCustomers: Boolean
    ) -> Unit,
    onUpdateOffer: (OfferEntity, List<String>) -> Unit,
    onDeleteOffer: (String, String) -> Unit,
    onToggleEnabled: (OfferEntity) -> Unit
) {
    var showCreateEditSheet by remember { mutableStateOf(false) }
    var offerToEdit by remember { mutableStateOf<OfferWithItems?>(null) }
    var selectedFilter by remember { mutableStateOf<String>("ALL") } // ALL, ACTIVE, SCHEDULED, EXPIRED, DISABLED

    val now = System.currentTimeMillis()
    val filteredOffers = offersWithItems.filter { item ->
        val status = item.getStatus(now)
        when (selectedFilter) {
            "ACTIVE" -> status == OfferStatus.ACTIVE
            "SCHEDULED" -> status == OfferStatus.SCHEDULED
            "EXPIRED" -> status == OfferStatus.EXPIRED
            "DISABLED" -> status == OfferStatus.DISABLED
            else -> true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBackground),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFE11D48), Color(0xFF9333EA), BrandPrimary)
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.LocalOffer,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Offer Management",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        text = "${offersWithItems.size} Total",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Create special promotional discounts and flash deals for customer print services with automatic start & expiry schedules.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            // Filter Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("ALL", "ACTIVE", "SCHEDULED", "EXPIRED", "DISABLED").forEach { filter ->
                        val isSelected = selectedFilter == filter
                        val count = when (filter) {
                            "ALL" -> offersWithItems.size
                            "ACTIVE" -> offersWithItems.count { it.getStatus(now) == OfferStatus.ACTIVE }
                            "SCHEDULED" -> offersWithItems.count { it.getStatus(now) == OfferStatus.SCHEDULED }
                            "EXPIRED" -> offersWithItems.count { it.getStatus(now) == OfferStatus.EXPIRED }
                            "DISABLED" -> offersWithItems.count { it.getStatus(now) == OfferStatus.DISABLED }
                            else -> 0
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filter },
                            label = { Text("$filter ($count)", style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = LightTextSecondary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("admin_offer_filter_$filter")
                        )
                    }
                }
            }

            // Create Offer Action Button Card
            item {
                Button(
                    onClick = {
                        offerToEdit = null
                        showCreateEditSheet = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_create_offer_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("+ Create Offer", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            // List of Offers
            if (filteredOffers.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.LocalOffer,
                                    contentDescription = null,
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = if (selectedFilter == "ALL") "No Offers Created Yet" else "No $selectedFilter Offers",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = LightTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap '+ Create Offer' above to launch a special deal with start date, expiry date, and included items.",
                                style = MaterialTheme.typography.bodySmall,
                                color = LightTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredOffers, key = { it.offer.id }) { offerWithItems ->
                    AdminOfferCard(
                        offerWithItems = offerWithItems,
                        now = now,
                        onEdit = {
                            offerToEdit = offerWithItems
                            showCreateEditSheet = true
                        },
                        onToggleEnabled = { onToggleEnabled(offerWithItems.offer) },
                        onDelete = { onDeleteOffer(offerWithItems.offer.id, offerWithItems.offer.title) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showCreateEditSheet) {
        CreateEditOfferBottomSheet(
            existingOffer = offerToEdit,
            allItems = allItems,
            onDismiss = { showCreateEditSheet = false },
            onSave = { title, desc, img, type, value, start, expire, itemIds, order, enabled, notify ->
                if (offerToEdit != null) {
                    val updated = offerToEdit!!.offer.copy(
                        title = title,
                        description = desc,
                        imageUrl = img,
                        offerType = type,
                        offerValue = value,
                        startAt = start,
                        expiresAt = expire,
                        displayOrder = order,
                        isEnabled = enabled
                    )
                    onUpdateOffer(updated, itemIds)
                } else {
                    onCreateOffer(title, desc, img, type, value, start, expire, itemIds, order, enabled, notify)
                }
                showCreateEditSheet = false
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminOfferCard(
    offerWithItems: OfferWithItems,
    now: Long,
    onEdit: () -> Unit,
    onToggleEnabled: () -> Unit,
    onDelete: () -> Unit
) {
    val offer = offerWithItems.offer
    val status = offerWithItems.getStatus(now)
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LightBorder, RoundedCornerShape(16.dp))
            .testTag("admin_offer_card_${offer.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Status Badge + Enabled Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OfferStatusBadge(status = status)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (offer.isEnabled) "Enabled" else "Disabled",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (offer.isEnabled) Color(0xFF15803D) else Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = offer.isEnabled,
                        onCheckedChange = { onToggleEnabled() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF16A34A),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.testTag("admin_offer_switch_${offer.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Value
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = offer.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = LightTextPrimary
                    )
                    if (offer.description.isNotBlank()) {
                        Text(
                            text = offer.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = LightTextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Surface(
                    color = BrandPrimaryContainer,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = offerWithItems.getDiscountLabel(),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = BrandPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timestamps Card (Start & Expiry)
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Starts:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                        }
                        Text(
                            text = dateFormat.format(Date(offer.startAt)),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = LightTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Expires:", style = MaterialTheme.typography.labelSmall, color = Color(0xFFE11D48))
                        }
                        Text(
                            text = dateFormat.format(Date(offer.expiresAt)),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (now > offer.expiresAt) Color(0xFFE11D48) else LightTextPrimary
                        )
                    }

                    // Remaining time calculation
                    if (status == OfferStatus.ACTIVE) {
                        val remainingMs = (offer.expiresAt - now).coerceAtLeast(0L)
                        val days = remainingMs / (1000 * 60 * 60 * 24)
                        val hours = (remainingMs / (1000 * 60 * 60)) % 24
                        val mins = (remainingMs / (1000 * 60)) % 60
                        val countdownText = if (days > 0) "${days}d ${hours}h ${mins}m" else "${hours}h ${mins}m"

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⏳ Ends in: $countdownText",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF15803D)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Included Items
            Text(
                text = "Included Services (${offerWithItems.items.size}):",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = LightTextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))

            if (offerWithItems.items.isEmpty()) {
                Text(
                    text = "⚠️ No items attached to this offer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFB45309)
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    offerWithItems.items.forEach { item ->
                        val discounted = offerWithItems.calculateDiscountedPrice(item.price)
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = LightTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "₹${item.price.toInt()}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        textDecoration = TextDecoration.LineThrough
                                    ),
                                    color = Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "₹${discounted.toInt()}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF15803D)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = LightBorder)
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons (Edit, Delete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE11D48)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_delete_offer_${offer.id}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", style = MaterialTheme.typography.labelMedium)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_edit_offer_${offer.id}")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Offer", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun OfferStatusBadge(status: OfferStatus) {
    val (bgColor, textColor, text, icon) = when (status) {
        OfferStatus.ACTIVE -> Quadruple(Color(0xFFDCFCE7), Color(0xFF15803D), "ACTIVE", Icons.Default.CheckCircle)
        OfferStatus.SCHEDULED -> Quadruple(Color(0xFFDBEAFE), Color(0xFF1D4ED8), "SCHEDULED", Icons.Default.Schedule)
        OfferStatus.EXPIRED -> Quadruple(Color(0xFFFFE4E6), Color(0xFFBE123C), "EXPIRED", Icons.Default.HourglassBottom)
        OfferStatus.DISABLED -> Quadruple(Color(0xFFF1F5F9), Color(0xFF64748B), "DISABLED", Icons.Default.VisibilityOff)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateEditOfferBottomSheet(
    existingOffer: OfferWithItems?,
    allItems: List<ItemEntity>,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        description: String,
        imageUrl: String,
        offerType: String,
        offerValue: Double,
        startAt: Long,
        expiresAt: Long,
        selectedItemIds: List<String>,
        displayOrder: Int,
        isEnabled: Boolean,
        notifyCustomers: Boolean
    ) -> Unit
) {
    val now = System.currentTimeMillis()
    val isEdit = existingOffer != null

    var title by remember { mutableStateOf(existingOffer?.offer?.title ?: "") }
    var description by remember { mutableStateOf(existingOffer?.offer?.description ?: "") }
    var imageUrl by remember { mutableStateOf(existingOffer?.offer?.imageUrl ?: "") }
    var offerType by remember { mutableStateOf(existingOffer?.offer?.offerType ?: "FIXED_PRICE") }
    var offerValueText by remember { mutableStateOf(existingOffer?.offer?.offerValue?.let { if (it % 1 == 0.0) it.toInt().toString() else it.toString() } ?: "50") }

    // Start & Expiry Time Pickers (Default Start: Now, Default Expiry: Now + 3 days)
    var startAt by remember { mutableLongStateOf(existingOffer?.offer?.startAt ?: now) }
    var expiresAt by remember { mutableLongStateOf(existingOffer?.offer?.expiresAt ?: (now + 3 * 24 * 60 * 60 * 1000L)) }

    val selectedItemIds = remember {
        mutableStateListOf<String>().apply {
            if (existingOffer != null) {
                addAll(existingOffer.items.map { it.id })
            } else if (allItems.isNotEmpty()) {
                add(allItems.first().id)
            }
        }
    }

    var displayOrderText by remember { mutableStateOf(existingOffer?.offer?.displayOrder?.toString() ?: "0") }
    var isEnabled by remember { mutableStateOf(existingOffer?.offer?.isEnabled ?: true) }
    var notifyCustomers by remember { mutableStateOf(true) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 30.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isEdit) "Edit Offer" else "Create New Offer",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = LightTextPrimary
                    )
                    Text(
                        text = "Admin Offer Management System",
                        style = MaterialTheme.typography.bodySmall,
                        color = LightTextSecondary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                Surface(
                    color = Color(0xFFFFE4E6),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFBE123C),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Offer Title
            Text(
                text = "Offer Title *",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = LightTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    errorMessage = null
                },
                placeholder = { Text("e.g. Passport Photo Special Offer") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_offer_title_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Offer Description
            Text(
                text = "Offer Description",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = LightTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("e.g. Get passport size photos at a special discounted price.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_offer_desc_input"),
                shape = RoundedCornerShape(12.dp),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Offer Type Selector
            Text(
                text = "Offer Type *",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = LightTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("FIXED_PRICE", "Fixed Price (₹)", Icons.Default.CurrencyRupee),
                    Triple("PERCENTAGE_DISCOUNT", "% Discount", Icons.Default.Percent),
                    Triple("FIXED_DISCOUNT", "Flat Off (₹)", Icons.Default.LocalOffer)
                ).forEach { (typeKey, label, icon) ->
                    val isSelected = offerType == typeKey
                    Surface(
                        color = if (isSelected) BrandPrimaryContainer else Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) BrandPrimary else LightBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { offerType = typeKey }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                icon,
                                contentDescription = null,
                                tint = if (isSelected) BrandPrimary else LightTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) BrandPrimary else LightTextPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Offer Value
            Text(
                text = when (offerType) {
                    "FIXED_PRICE" -> "Offer Price (₹) *"
                    "PERCENTAGE_DISCOUNT" -> "Discount Percentage (%) *"
                    "FIXED_DISCOUNT" -> "Flat Discount Amount (₹) *"
                    else -> "Offer Value *"
                },
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = LightTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = offerValueText,
                onValueChange = { offerValueText = it },
                placeholder = {
                    Text(
                        when (offerType) {
                            "FIXED_PRICE" -> "e.g. 50"
                            "PERCENTAGE_DISCOUNT" -> "e.g. 20"
                            "FIXED_DISCOUNT" -> "e.g. 30"
                            else -> "50"
                        }
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_offer_value_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Start & Expiry Date/Time Preset Adjusters
            Text(
                text = "Offer Schedule (Start & Expiry Timestamps) *",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = LightTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Start Time Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Starts At", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                            Text(
                                text = dateFormat.format(Date(startAt)),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = LightTextPrimary
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(
                                color = BrandPrimaryContainer,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.clickable { startAt = System.currentTimeMillis() }
                            ) {
                                Text("Now", style = MaterialTheme.typography.labelSmall, color = BrandPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                            Surface(
                                color = Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.clickable { startAt = System.currentTimeMillis() + 24 * 60 * 60 * 1000L }
                            ) {
                                Text("+1 Day", style = MaterialTheme.typography.labelSmall, color = LightTextPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = LightBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Expiry Time Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Expires At", style = MaterialTheme.typography.labelSmall, color = Color(0xFFE11D48))
                            Text(
                                text = dateFormat.format(Date(expiresAt)),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFBE123C)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(
                                color = Color(0xFFFCE7F3),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.clickable { expiresAt = startAt + 1 * 24 * 60 * 60 * 1000L }
                            ) {
                                Text("+1 Day", style = MaterialTheme.typography.labelSmall, color = Color(0xFFBE185D), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                            Surface(
                                color = Color(0xFFFCE7F3),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.clickable { expiresAt = startAt + 3 * 24 * 60 * 60 * 1000L }
                            ) {
                                Text("+3 Days", style = MaterialTheme.typography.labelSmall, color = Color(0xFFBE185D), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                            Surface(
                                color = Color(0xFFFCE7F3),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.clickable { expiresAt = startAt + 7 * 24 * 60 * 60 * 1000L }
                            ) {
                                Text("+1 Week", style = MaterialTheme.typography.labelSmall, color = Color(0xFFBE185D), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Select Items Included in Offer
            Text(
                text = "Select Included Shop Items (${selectedItemIds.size} selected) *",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = LightTextPrimary
            )
            Text(
                text = "Choose which items receive this discounted offer price:",
                style = MaterialTheme.typography.bodySmall,
                color = LightTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (allItems.isEmpty()) {
                Text(
                    text = "No items available. Please add items in 'Manage Items' first.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFB45309)
                )
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LightBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        allItems.forEach { item ->
                            val isChecked = selectedItemIds.contains(item.id)
                            val valDouble = offerValueText.toDoubleOrNull() ?: 0.0
                            val previewDiscounted = when (offerType) {
                                "FIXED_PRICE" -> valDouble
                                "PERCENTAGE_DISCOUNT" -> (item.price - (item.price * valDouble / 100.0)).coerceAtLeast(0.0)
                                "FIXED_DISCOUNT" -> (item.price - valDouble).coerceAtLeast(0.0)
                                else -> item.price
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isChecked) selectedItemIds.remove(item.id) else selectedItemIds.add(item.id)
                                    }
                                    .padding(vertical = 6.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { checked ->
                                            if (checked) selectedItemIds.add(item.id) else selectedItemIds.remove(item.id)
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = BrandPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column {
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = LightTextPrimary
                                        )
                                        Text(
                                            text = "Normal: ₹${item.price.toInt()} / ${item.unit}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = LightTextSecondary
                                        )
                                    }
                                }

                                if (isChecked && valDouble > 0) {
                                    Surface(
                                        color = Color(0xFFDCFCE7),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "→ ₹${previewDiscounted.toInt()}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFF15803D),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Banner Image Selection / URL
            Text(
                text = "Offer Banner / Image",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = LightTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                placeholder = { Text("Image URL or leave empty for auto promo banner") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_offer_image_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Enable switch & In-app Notification checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enable this offer immediately",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = LightTextPrimary
                )
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { isEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF16A34A))
                )
            }

            if (!isEdit) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { notifyCustomers = !notifyCustomers }
                ) {
                    Checkbox(
                        checked = notifyCustomers,
                        onCheckedChange = { notifyCustomers = it },
                        colors = CheckboxDefaults.colors(checkedColor = BrandPrimary)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Send customer in-app notification ('🎉 New Offer at Hafsa Traders!')",
                        style = MaterialTheme.typography.bodySmall,
                        color = LightTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "Please enter an offer title."
                        return@Button
                    }
                    val value = offerValueText.toDoubleOrNull()
                    if (value == null || value <= 0) {
                        errorMessage = "Please enter a valid positive offer value."
                        return@Button
                    }
                    if (selectedItemIds.isEmpty()) {
                        errorMessage = "Please select at least one item included in this offer."
                        return@Button
                    }
                    if (expiresAt <= startAt) {
                        errorMessage = "Expiry date must be after the start date."
                        return@Button
                    }

                    val order = displayOrderText.toIntOrNull() ?: 0
                    onSave(
                        title.trim(),
                        description.trim(),
                        imageUrl.trim(),
                        offerType,
                        value,
                        startAt,
                        expiresAt,
                        selectedItemIds.toList(),
                        order,
                        isEnabled,
                        notifyCustomers
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("admin_save_offer_btn")
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEdit) "Save Changes" else "Create Offer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
