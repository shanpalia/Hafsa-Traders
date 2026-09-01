package com.example.ui.admin

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OrderEntity
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.PaymentStatusBadge
import com.example.ui.theme.*

import androidx.compose.material3.CircularProgressIndicator

@Composable
fun AdminLoginScreen(
    errorMessage: String?,
    isLoading: Boolean = false,
    onLogin: (String, String) -> Unit,
    onBackToCustomer: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(BrandPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "HAFSA TRADERS",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = BrandPrimary
                )

                Text(
                    text = "ADMIN LOGIN",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = LightTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Authorized store owner credentials required. Sign in with the admin email and password configured in Admin Settings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = LightTextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text("admin@hafsatraders.com") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = LightBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_email_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    placeholder = { Text("Enter admin password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = LightBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_password_input")
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFDC2626),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            modifier = Modifier.padding(10.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onLogin(email, password) },
                    enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_login_submit_btn")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("LOGIN", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onBackToCustomer,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightSurfaceVariant,
                        contentColor = LightTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("← Back to Customer Home")
                }
            }
        }
    }
}

@Composable
fun AdminDashboardScreen(
    totalOrders: Int,
    newOrders: Int,
    processingOrders: Int,
    readyOrders: Int,
    completedOrders: Int,
    totalRevenue: Double,
    pendingRevenue: Double,
    recentOrders: List<OrderEntity>,
    onSelectOrder: (String) -> Unit,
    onNavigateTab: (com.example.ui.viewmodel.AdminTab) -> Unit,
    onAddNewItem: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BrandPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Shop Owner Dashboard",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Live store overview, customer print queues & pricing control",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Metrics 2x2 & Revenue Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminMetricCard(
                        title = "Total Orders",
                        value = "$totalOrders",
                        icon = Icons.Default.Receipt,
                        iconBg = BrandPrimaryContainer,
                        iconTint = BrandPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "New / Received",
                        value = "$newOrders",
                        icon = Icons.Default.PendingActions,
                        iconBg = Color(0xFFDBEAFE),
                        iconTint = Color(0xFF1D4ED8),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminMetricCard(
                        title = "Processing",
                        value = "$processingOrders",
                        icon = Icons.Default.Print,
                        iconBg = Color(0xFFEDE9FE),
                        iconTint = Color(0xFF6D28D9),
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Ready for Pickup",
                        value = "$readyOrders",
                        icon = Icons.Default.CheckCircle,
                        iconBg = Color(0xFFDCFCE7),
                        iconTint = Color(0xFF16A34A),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminMetricCard(
                        title = "Completed",
                        value = "$completedOrders",
                        icon = Icons.Default.DoneAll,
                        iconBg = Color(0xFFE0F2FE),
                        iconTint = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Total Paid Revenue",
                        value = "₹${totalRevenue.toInt()}",
                        icon = Icons.Default.CurrencyRupee,
                        iconBg = Color(0xFFDCFCE7),
                        iconTint = Color(0xFF15803D),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminMetricCard(
                        title = "Pending Payments",
                        value = "₹${pendingRevenue.toInt()}",
                        icon = Icons.Default.HourglassEmpty,
                        iconBg = Color(0xFFFEF3C7),
                        iconTint = Color(0xFFB45309),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Store Management Modules
        item {
            Text(
                text = "Store Management Modules",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = LightTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminNavigationModuleCard(
                        title = "Manage Orders",
                        subtitle = "$totalOrders orders • $newOrders new",
                        icon = Icons.Default.ReceiptLong,
                        iconTint = Color(0xFF2563EB),
                        iconBg = Color(0xFFDBEAFE),
                        onClick = { onNavigateTab(com.example.ui.viewmodel.AdminTab.ORDERS) },
                        modifier = Modifier.weight(1f)
                    )
                    AdminNavigationModuleCard(
                        title = "Manage Items",
                        subtitle = "Services & rates",
                        icon = Icons.Default.Inventory,
                        iconTint = Color(0xFF7C3AED),
                        iconBg = Color(0xFFEDE9FE),
                        onClick = { onNavigateTab(com.example.ui.viewmodel.AdminTab.ITEMS) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminNavigationModuleCard(
                        title = "Special Offers",
                        subtitle = "Discounts & Campaigns",
                        icon = Icons.Default.LocalOffer,
                        iconTint = Color(0xFFBE185D),
                        iconBg = Color(0xFFFCE7F3),
                        onClick = { onNavigateTab(com.example.ui.viewmodel.AdminTab.OFFERS) },
                        modifier = Modifier.weight(1f)
                    )
                    AdminNavigationModuleCard(
                        title = "Manage Categories",
                        subtitle = "Service categories",
                        icon = Icons.Default.Category,
                        iconTint = Color(0xFF0D9488),
                        iconBg = Color(0xFFCCFBF1),
                        onClick = { onNavigateTab(com.example.ui.viewmodel.AdminTab.CATEGORIES) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminNavigationModuleCard(
                        title = "Payment Settings",
                        subtitle = "UPI ID & QR preview",
                        icon = Icons.Default.QrCode,
                        iconTint = Color(0xFFD97706),
                        iconBg = Color(0xFFFEF3C7),
                        onClick = { onNavigateTab(com.example.ui.viewmodel.AdminTab.PAYMENTS) },
                        modifier = Modifier.weight(1f)
                    )
                    AdminNavigationModuleCard(
                        title = "Notifications",
                        subtitle = "Customer orders alert",
                        icon = Icons.Default.Notifications,
                        iconTint = Color(0xFFE11D48),
                        iconBg = Color(0xFFFFE4E6),
                        onClick = { onNavigateTab(com.example.ui.viewmodel.AdminTab.NOTIFICATIONS) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminNavigationModuleCard(
                        title = "Shop Settings",
                        subtitle = "Profile, phone & PIN",
                        icon = Icons.Default.Settings,
                        iconTint = Color(0xFF475569),
                        iconBg = Color(0xFFF1F5F9),
                        onClick = { onNavigateTab(com.example.ui.viewmodel.AdminTab.SETTINGS) },
                        modifier = Modifier.fillMaxWidth(0.5f)
                    )
                }
            }
        }

        // Quick Actions
        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = LightTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Button(
                        onClick = onAddNewItem,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("admin_quick_add_item")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("+ Add New Item")
                    }
                }
                item {
                    Button(
                        onClick = { onNavigateTab(com.example.ui.viewmodel.AdminTab.ITEMS) },
                        colors = ButtonDefaults.buttonColors(containerColor = LightSurfaceVariant, contentColor = LightTextPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Manage Prices")
                    }
                }
                item {
                    Button(
                        onClick = { onNavigateTab(com.example.ui.viewmodel.AdminTab.CATEGORIES) },
                        colors = ButtonDefaults.buttonColors(containerColor = LightSurfaceVariant, contentColor = LightTextPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Categories")
                    }
                }
                item {
                    Button(
                        onClick = { onNavigateTab(com.example.ui.viewmodel.AdminTab.PAYMENTS) },
                        colors = ButtonDefaults.buttonColors(containerColor = LightSurfaceVariant, contentColor = LightTextPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("UPI Settings")
                    }
                }
            }
        }

        // Recent Orders Feed
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Incoming Orders",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = LightTextPrimary
                )
                Text(
                    text = "View All Orders →",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = BrandPrimary,
                    modifier = Modifier.clickable { onNavigateTab(com.example.ui.viewmodel.AdminTab.ORDERS) }
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (recentOrders.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        text = "No orders received yet",
                        modifier = Modifier.padding(20.dp),
                        color = LightTextSecondary
                    )
                }
            }
        } else {
            items(recentOrders.take(5)) { order ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LightBorder, RoundedCornerShape(12.dp))
                        .clickable { onSelectOrder(order.id) }
                        .testTag("admin_recent_order_${order.orderNumber}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#${order.orderNumber}",
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = order.customerName,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(
                                text = "${order.customerPhone} • ₹${order.totalAmount.toInt()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = LightTextSecondary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            OrderStatusBadge(status = order.orderStatus)
                            Spacer(modifier = Modifier.height(4.dp))
                            PaymentStatusBadge(status = order.paymentStatus)
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
fun AdminMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.border(1.dp, LightBorder, RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = LightTextSecondary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = LightTextPrimary
                )
            }
        }
    }
}

@Composable
fun AdminNavigationModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color = BrandPrimary,
    iconBg: Color = BrandPrimaryContainer,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .border(1.dp, LightBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = LightTextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = LightTextSecondary
            )
        }
    }
}

