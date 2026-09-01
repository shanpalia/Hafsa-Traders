package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OrderEntity
import com.example.data.repository.OrderWithDetails
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.PaymentStatusBadge
import com.example.ui.components.VisualOrderStepper
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CustomerOrdersScreen(
    orders: List<OrderEntity>,
    onSelectOrder: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredOrders = when (selectedFilter) {
        "ACTIVE" -> orders.filter { it.orderStatus != "COMPLETED" && it.orderStatus != "CANCELLED" }
        "COMPLETED" -> orders.filter { it.orderStatus == "COMPLETED" }
        else -> orders
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = "My Print Orders",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = LightTextPrimary
            )
            Text(
                text = "Track printing progress and download receipts",
                style = MaterialTheme.typography.bodySmall,
                color = LightTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("All (${orders.size})") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White)
                )
                FilterChip(
                    selected = selectedFilter == "ACTIVE",
                    onClick = { selectedFilter = "ACTIVE" },
                    label = { Text("Active (${orders.count { it.orderStatus != "COMPLETED" && it.orderStatus != "CANCELLED" }})") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White)
                )
                FilterChip(
                    selected = selectedFilter == "COMPLETED",
                    onClick = { selectedFilter = "COMPLETED" },
                    label = { Text("Completed (${orders.count { it.orderStatus == "COMPLETED" }})") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White)
                )
            }
        }

        if (filteredOrders.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null,
                    tint = LightTextTertiary,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (orders.isEmpty()) "No orders yet" else "No orders found in this category",
                    fontWeight = FontWeight.Bold,
                    color = LightTextSecondary
                )
                if (orders.isEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Start a new order to print documents, photos, or lamination.",
                        style = MaterialTheme.typography.bodySmall,
                        color = LightTextTertiary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOrders) { order ->
                    CustomerOrderCard(
                        order = order,
                        onClick = { onSelectOrder(order.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomerOrderCard(
    order: OrderEntity,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(order.createdAt))

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LightBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("order_card_${order.orderNumber}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order #${order.orderNumber}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = BrandPrimary
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = LightTextTertiary
                    )
                }
                OrderStatusBadge(status = order.orderStatus)
            }

            Spacer(modifier = Modifier.height(12.dp))

            VisualOrderStepper(currentStatus = order.orderStatus)

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = LightBorder, thickness = 0.8.dp)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Total: ", style = MaterialTheme.typography.bodyMedium, color = LightTextSecondary)
                    Text(
                        text = "₹${order.totalAmount.toInt()}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = LightTextPrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    PaymentStatusBadge(status = order.paymentStatus)
                }

                Text(
                    text = "View Details →",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = BrandPrimary
                )
            }
        }
    }
}



@Composable
private fun OrderTrackingTimeline(
    currentStatus: String,
    history: List<com.example.data.local.OrderStatusHistoryEntity>,
    placedAt: Long
) {
    val steps = listOf("RECEIVED", "PROCESSING", "READY", "COMPLETED")
    val currentIndex = steps.indexOf(currentStatus).let { if (it < 0) 0 else it }
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    Card(
        colors = CardDefaults.cardColors(containerColor = LightSurfaceVariant),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = if (currentStatus == "CANCELLED") "Order Tracking" else "Live Order Tracking",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = LightTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (currentStatus == "CANCELLED") {
                val cancelled = history.lastOrNull { it.status == "CANCELLED" }
                Text("Order cancelled", fontWeight = FontWeight.Bold, color = Color(0xFFB42318))
                Text(
                    cancelled?.message ?: "Please contact the shop for assistance.",
                    style = MaterialTheme.typography.bodySmall,
                    color = LightTextSecondary
                )
                cancelled?.let {
                    Text(dateFormat.format(Date(it.changedAt)), style = MaterialTheme.typography.labelSmall, color = LightTextTertiary)
                }
            } else {
                steps.forEachIndexed { index, status ->
                    val event = history.lastOrNull { it.status == status }
                    val reached = index <= currentIndex
                    val title = when (status) {
                        "RECEIVED" -> "Order received"
                        "PROCESSING" -> "In progress"
                        "READY" -> "Ready for pickup"
                        else -> "Picked up / completed"
                    }
                    val message = when {
                        event != null -> event.message
                        status == "RECEIVED" -> "Order submitted to Hafsa Traders"
                        !reached -> "Waiting for this update"
                        else -> "Status updated"
                    }
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                        Text(
                            text = if (reached) "●" else "○",
                            color = if (reached) BrandPrimary else LightTextTertiary,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, fontWeight = if (reached) FontWeight.Bold else FontWeight.Medium, color = LightTextPrimary)
                            Text(message, style = MaterialTheme.typography.bodySmall, color = LightTextSecondary)
                            val timestamp = event?.changedAt ?: if (status == "RECEIVED") placedAt else null
                            if (timestamp != null) {
                                Text(dateFormat.format(Date(timestamp)), style = MaterialTheme.typography.labelSmall, color = LightTextTertiary)
                            }
                            if (index != steps.lastIndex) Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOrderDetailBottomSheet(
    orderDetails: OrderWithDetails?,
    onDismiss: () -> Unit,
    shopPhone: String,
    shopWhatsApp: String
) {
    if (orderDetails == null) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val order = orderDetails.order
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(order.createdAt))

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Order #${order.orderNumber}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = BrandPrimary
                        )
                        Text(
                            text = "Placed on $dateStr",
                            style = MaterialTheme.typography.labelSmall,
                            color = LightTextSecondary
                        )
                    }
                    OrderStatusBadge(status = order.orderStatus)
                }
            }

            // Stepper
            item {
                VisualOrderStepper(currentStatus = order.orderStatus)
            }

            // Live tracking timeline with actual status-change timestamps.
            item {
                OrderTrackingTimeline(
                    currentStatus = order.orderStatus,
                    history = orderDetails.statusHistory,
                    placedAt = order.createdAt
                )
            }

            // Ordered Items
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = LightSurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Order Items (${orderDetails.items.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        orderDetails.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.itemName, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = "₹${item.unitPrice.toInt()} × ${item.quantity} ${item.unit}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = LightTextSecondary
                                    )
                                }
                                Text(
                                    text = "₹${item.subtotal.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    color = LightTextPrimary
                                )
                            }
                        }

                        Divider(color = LightBorder, modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Paid", fontWeight = FontWeight.Bold)
                            Text("₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, color = BrandPrimary, fontSize = 16.sp)
                        }
                    }
                }
            }

            // Uploaded Files
            if (orderDetails.files.isNotEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightSurfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Uploaded Files (${orderDetails.files.size})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            orderDetails.files.forEach { file ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (file.fileType.contains("pdf")) Icons.Outlined.PictureAsPdf else Icons.Outlined.Photo,
                                        contentDescription = null,
                                        tint = if (file.fileType.contains("pdf")) Color(0xFFDC2626) else BrandSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = file.fileName, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodySmall)
                                        Text(text = "${file.fileSizeKb} KB • Stored securely on cloud", style = MaterialTheme.typography.labelSmall, color = LightTextTertiary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Special Instructions
            if (order.specialInstructions.isNotBlank()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightSurfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Special Instructions", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = order.specialInstructions, style = MaterialTheme.typography.bodySmall, color = LightTextSecondary)
                        }
                    }
                }
            }

            // Support Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call Shop")
                    }

                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp Shop")
                    }
                }
            }
        }
    }
}
