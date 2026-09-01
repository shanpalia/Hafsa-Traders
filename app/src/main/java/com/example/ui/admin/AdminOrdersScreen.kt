package com.example.ui.admin

import android.content.Intent
import android.net.Uri

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
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
fun AdminOrdersScreen(
    orders: List<OrderEntity>,
    onSelectOrder: (String) -> Unit,
    onUpdateStatus: (String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredOrders = orders.filter { order ->
        val matchesQuery = searchQuery.isBlank() ||
            order.orderNumber.contains(searchQuery, ignoreCase = true) ||
            order.customerName.contains(searchQuery, ignoreCase = true) ||
            order.customerPhone.contains(searchQuery)
        val matchesStatus = when (selectedFilter) {
            "ALL" -> true
            "PENDING_PAY" -> order.paymentStatus == "PENDING"
            else -> order.orderStatus.equals(selectedFilter, ignoreCase = true)
        }
        matchesQuery && matchesStatus
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Order Management Queue",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = LightTextPrimary
            )
            Text(
                text = "Real-time updates, print dispatch and status changer",
                style = MaterialTheme.typography.bodySmall,
                color = LightTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by Order #HT..., Name, Phone...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrandPrimary) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = BrandPrimary,
                    unfocusedBorderColor = LightBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_orders_search"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("All (${orders.size})") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White)
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "RECEIVED",
                        onClick = { selectedFilter = "RECEIVED" },
                        label = { Text("Received (${orders.count { it.orderStatus == "RECEIVED" }})") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = StatusReceivedText, selectedLabelColor = Color.White)
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "PROCESSING",
                        onClick = { selectedFilter = "PROCESSING" },
                        label = { Text("Processing (${orders.count { it.orderStatus == "PROCESSING" }})") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = StatusProcessingText, selectedLabelColor = Color.White)
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "READY",
                        onClick = { selectedFilter = "READY" },
                        label = { Text("Ready (${orders.count { it.orderStatus == "READY" }})") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = StatusReadyText, selectedLabelColor = Color.White)
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "COMPLETED",
                        onClick = { selectedFilter = "COMPLETED" },
                        label = { Text("Completed (${orders.count { it.orderStatus == "COMPLETED" }})") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = StatusCompletedText, selectedLabelColor = Color.White)
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "PENDING_PAY",
                        onClick = { selectedFilter = "PENDING_PAY" },
                        label = { Text("Pending Pay (${orders.count { it.paymentStatus == "PENDING" }})") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = StatusPendingText, selectedLabelColor = Color.White)
                    )
                }
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
                Icon(Icons.Default.FilterList, contentDescription = null, tint = LightTextTertiary, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text("No orders match the selected filter", fontWeight = FontWeight.Bold, color = LightTextSecondary)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOrders) { order ->
                    AdminOrderCard(
                        order = order,
                        onClick = { onSelectOrder(order.id) },
                        onQuickStatusChange = { newStatus -> onUpdateStatus(order.id, newStatus) }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminOrderCard(
    order: OrderEntity,
    onClick: () -> Unit,
    onQuickStatusChange: (String) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LightBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("admin_order_card_${order.orderNumber}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "#${order.orderNumber}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = BrandPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = order.customerName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightTextPrimary
                    )
                }
                OrderStatusBadge(status = order.orderStatus)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${order.customerPhone} • ${dateFormat.format(Date(order.createdAt))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = LightTextSecondary
                )
                PaymentStatusBadge(status = order.paymentStatus)
            }

            if (order.specialInstructions.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Note: ${order.specialInstructions}",
                        color = Color(0xFF92400E),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = LightBorder, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: ₹${order.totalAmount.toInt()} (${order.paymentMethod})",
                    fontWeight = FontWeight.Bold,
                    color = LightTextPrimary
                )

                // Quick Status Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (order.orderStatus == "RECEIVED") {
                        Button(
                            onClick = { onQuickStatusChange("PROCESSING") },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusProcessingText),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Start Print", fontSize = 11.sp)
                        }
                    } else if (order.orderStatus == "PROCESSING") {
                        Button(
                            onClick = { onQuickStatusChange("READY") },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusReadyText),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Mark Ready", fontSize = 11.sp)
                        }
                    } else if (order.orderStatus == "READY") {
                        Button(
                            onClick = { onQuickStatusChange("COMPLETED") },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusCompletedText),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Complete", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderDetailBottomSheet(
    orderDetails: OrderWithDetails?,
    onDismiss: () -> Unit,
    onUpdateStatus: (String, String) -> Unit,
    onUpdatePayment: (String, String, String) -> Unit
) {
    val context = LocalContext.current

    if (orderDetails == null) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val order = orderDetails.order
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val statuses = listOf("RECEIVED", "PROCESSING", "READY", "COMPLETED", "CANCELLED")
    val paymentStatuses = listOf("PAID", "PENDING", "FAILED")

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
                            text = "Admin Order #${order.orderNumber}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = BrandPrimary
                        )
                        Text(
                            text = dateFormat.format(Date(order.createdAt)),
                            style = MaterialTheme.typography.labelSmall,
                            color = LightTextSecondary
                        )
                    }
                    OrderStatusBadge(status = order.orderStatus)
                }
            }

            // Customer Contact Info & Quick Call
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = LightSurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = order.customerName,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = order.customerPhone,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LightTextSecondary
                                )
                                if (order.customerEmail.isNotBlank()) {
                                    Text(
                                        text = order.customerEmail,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = LightTextTertiary
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Call", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Chat", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Status Changer Buttons (Triggers instant push notification)
            item {
                Text(
                    text = "Update Order Status (Notifies Customer)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = LightTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(statuses) { st ->
                        val isSelected = order.orderStatus.equals(st, ignoreCase = true)
                        Surface(
                            color = if (isSelected) BrandPrimary else LightSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .clickable { onUpdateStatus(order.id, st) }
                                .testTag("admin_status_btn_$st")
                        ) {
                            Text(
                                text = st,
                                color = if (isSelected) Color.White else LightTextPrimary,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Payment Status Changer
            item {
                Text(
                    text = "Payment Status & Reference",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = LightTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    paymentStatuses.forEach { pst ->
                        val isSelected = order.paymentStatus.equals(pst, ignoreCase = true)
                        Surface(
                            color = if (isSelected) {
                                if (pst == "PAID") Color(0xFF16A34A) else if (pst == "PENDING") Color(0xFFD97706) else Color(0xFFDC2626)
                            } else LightSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onUpdatePayment(order.id, pst, order.paymentRef) }
                        ) {
                            Text(
                                text = pst,
                                color = if (isSelected) Color.White else LightTextPrimary,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                if (order.paymentRef.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Payment Ref / Method: ${order.paymentMethod} • ${order.paymentRef}",
                        style = MaterialTheme.typography.labelSmall,
                        color = LightTextTertiary
                    )
                }
            }

            // Frozen Items List
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = LightSurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Items for Printing (${orderDetails.items.size})",
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
                                    Text(text = item.itemName, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = "₹${item.unitPrice.toInt()} × ${item.quantity} ${item.unit}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = LightTextSecondary
                                    )
                                }
                                Text("₹${item.subtotal.toInt()}", fontWeight = FontWeight.Bold)
                            }
                        }

                        Divider(color = LightBorder, modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Billing", fontWeight = FontWeight.Bold)
                            Text("₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, color = BrandPrimary, fontSize = 16.sp)
                        }
                    }
                }
            }

            // Customer Files Section
            if (orderDetails.files.isNotEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightSurfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Uploaded Documents / Photos (${orderDetails.files.size})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            orderDetails.files.forEach { file ->
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = if (file.fileType.contains("pdf")) Icons.Outlined.PictureAsPdf else Icons.Outlined.Photo,
                                                contentDescription = null,
                                                tint = if (file.fileType.contains("pdf")) Color(0xFFDC2626) else BrandSecondary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(text = file.fileName, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                                                Text(text = "${file.fileSizeKb} KB • ${file.fileType}", style = MaterialTheme.typography.labelSmall, color = LightTextTertiary)
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                try {
                                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                                        setDataAndType(Uri.parse(file.fileUri), if (file.fileType.equals("PDF", true)) "application/pdf" else "image/*")
                                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                    }
                                                    context.startActivity(Intent.createChooser(intent, "Open ${file.fileName}"))
                                                } catch (_: Exception) { }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("Open", fontSize = 11.sp)
                                        }
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
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Customer Printing Instructions", fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = order.specialInstructions, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF78350F))
                        }
                    }
                }
            }
        }
    }
}
