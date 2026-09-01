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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.data.update.WebsiteUpdateResult
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.NotificationEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CustomerNotificationsScreen(
    notifications: List<NotificationEntity>,
    onNotificationClick: (NotificationEntity) -> Unit,
    onMarkAllRead: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = LightTextPrimary
                )
                Text(
                    text = "Order status updates & alerts",
                    style = MaterialTheme.typography.bodySmall,
                    color = LightTextSecondary
                )
            }

            if (notifications.any { !it.isRead }) {
                OutlinedButton(
                    onClick = onMarkAllRead,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("notif_mark_all_read_btn")
                ) {
                    Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Mark all read", fontSize = 12.sp)
                }
            }
        }

        if (notifications.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = LightTextTertiary,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No notifications yet",
                    fontWeight = FontWeight.Bold,
                    color = LightTextSecondary
                )
                Text(
                    text = "You'll receive real-time push alerts when order status changes",
                    style = MaterialTheme.typography.bodySmall,
                    color = LightTextTertiary
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications) { notif ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (!notif.isRead) BrandPrimaryContainer.copy(alpha = 0.5f) else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, if (!notif.isRead) BrandPrimary.copy(alpha = 0.3f) else LightBorder, RoundedCornerShape(12.dp))
                            .clickable { onNotificationClick(notif) }
                            .testTag("notif_card_${notif.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            val isPromo = notif.type == "PROMO" || notif.title.contains("Offer", ignoreCase = true)
                            val isReady = notif.title.contains("Ready", ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isPromo -> Color(0xFFFCE7F3)
                                            isReady -> Color(0xFFDCFCE7)
                                            else -> BrandPrimaryContainer
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        isPromo -> Icons.Default.LocalOffer
                                        isReady -> Icons.Default.CheckCircle
                                        else -> Icons.Default.Print
                                    },
                                    contentDescription = null,
                                    tint = when {
                                        isPromo -> Color(0xFFBE185D)
                                        isReady -> Color(0xFF16A34A)
                                        else -> BrandPrimary
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notif.title,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = LightTextPrimary
                                    )
                                    if (!notif.isRead) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(BrandPrimary)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = notif.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LightTextSecondary
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = dateFormat.format(Date(notif.createdAt)),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = LightTextTertiary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerProfileScreen(
    customerName: String,
    customerPhone: String,
    customerEmail: String,
    customerAddress: String,
    shopName: String,
    shopSubtitle: String,
    shopAddress: String,
    currentAppVersion: String,
    updateCheckUrl: String,
    onSaveProfile: (String, String, String, String) -> Unit,
    onCheckForUpdate: (String, (WebsiteUpdateResult) -> Unit) -> Unit
) {
    var name by remember(customerName) { mutableStateOf(customerName) }
    var phone by remember(customerPhone) { mutableStateOf(customerPhone) }
    var email by remember(customerEmail) { mutableStateOf(customerEmail) }
    var address by remember(customerAddress) { mutableStateOf(customerAddress) }
    var isEditing by remember { mutableStateOf(false) }
    var checkingUpdate by remember { mutableStateOf(false) }
    var updateStatus by remember { mutableStateOf<String?>(null) }
    var updateAvailable by remember { mutableStateOf<com.example.data.update.WebsiteUpdateInfo?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Customer Profile Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = name.ifBlank { "Complete your profile" },
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = LightTextPrimary
                                )
                                Text(
                                    text = phone.ifBlank { "Add your mobile number" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LightTextSecondary
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (isEditing) {
                                    onSaveProfile(name, phone, email, address)
                                }
                                isEditing = !isEditing
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEditing) Color(0xFF16A34A) else BrandPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (isEditing) "Save" else "Edit")
                        }
                    }

                    if (isEditing) {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { },
                            label = { Text("Login Email") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Pickup / Default Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Website update check
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, LightBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(BrandPrimaryContainer), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = BrandPrimary)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("App Update", fontWeight = FontWeight.Bold, color = LightTextPrimary)
                            Text("Current version: $currentAppVersion", style = MaterialTheme.typography.bodySmall, color = LightTextSecondary)
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("Check Hafsa Traders website for the latest version.", style = MaterialTheme.typography.bodySmall, color = LightTextSecondary)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            checkingUpdate = true
                            updateStatus = null
                            updateAvailable = null
                            onCheckForUpdate(updateCheckUrl) { result ->
                                checkingUpdate = false
                                when (result) {
                                    is WebsiteUpdateResult.UpdateAvailable -> {
                                        updateAvailable = result.info
                                        updateStatus = "New version ${result.info.version} is available."
                                    }
                                    WebsiteUpdateResult.UpToDate -> updateStatus = "Your app is already up to date."
                                    is WebsiteUpdateResult.Error -> updateStatus = result.message
                                }
                            }
                        },
                        enabled = !checkingUpdate,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(if (checkingUpdate) "Checking..." else "Check for Updates")
                    }
                    updateStatus?.let { status ->
                        Spacer(Modifier.height(10.dp))
                        Text(status, style = MaterialTheme.typography.bodySmall, color = if (updateAvailable != null) Color(0xFF15803D) else LightTextSecondary)
                    }
                    updateAvailable?.let { info ->
                        Spacer(Modifier.height(10.dp))
                        Text(info.message, style = MaterialTheme.typography.bodySmall, color = LightTextSecondary)
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = { onCheckForUpdate("OPEN:${info.updateUrl}") { } }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)), modifier = Modifier.fillMaxWidth()) {
                            Text("Update Now")
                        }
                    }
                }
            }
        }

        // About Hafsa Traders
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "About HAFSA TRADERS",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = LightTextPrimary
                    )
                    Text(
                        text = "PHOTOCOPY • LAMINATION • PHOTO PRINT",
                        style = MaterialTheme.typography.bodySmall,
                        color = LightTextSecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = LightBorder)
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Shop Address",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall,
                                color = LightTextPrimary
                            )
                            Text(
                                shopAddress.ifBlank { "Shop address will be updated by the store" },
                                style = MaterialTheme.typography.bodySmall,
                                color = LightTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Profile intentionally contains no admin controls.

        item { Spacer(modifier = Modifier.height(14.dp)) }
        }
    }