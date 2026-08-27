package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Portrait
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CategoryEntity
import com.example.data.local.ItemEntity
import com.example.data.repository.UploadedFileDraft
import com.example.ui.theme.*

@Composable
fun HafsaHeader(
    shopName: String = "HAFSA TRADERS",
    shopSubtitle: String = "PHOTOCOPY • LAMINATION • PHOTO PRINT",
    unreadNotifCount: Int = 0,
    onNotifClick: () -> Unit = {},
    onAdminToggle: () -> Unit = {},
    onSecretAdminTrigger: () -> Unit = {},
    isAdmin: Boolean = false
) {
    var tapCount by remember { mutableStateOf(0) }
    var lastTapTime by remember { mutableStateOf(0L) }

    Surface(
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrandPrimary)
                            .clickable {
                                if (!isAdmin) {
                                    val now = System.currentTimeMillis()
                                    if (now - lastTapTime > 2500L) {
                                        tapCount = 1
                                    } else {
                                        tapCount += 1
                                    }
                                    lastTapTime = now
                                    if (tapCount >= 5) {
                                        tapCount = 0
                                        onSecretAdminTrigger()
                                    }
                                }
                            }
                            .testTag("shop_logo_header_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Hafsa Traders Logo",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = shopName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = BrandPrimary
                            )
                            if (isAdmin) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = BrandTertiaryContainer,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "ADMIN",
                                        color = BrandTertiary,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = shopSubtitle,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = LightTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNotifClick,
                        modifier = Modifier
                            .testTag("header_notif_button")
                            .minimumInteractiveComponentSize()
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifCount > 0) {
                                    Badge(
                                        containerColor = Color(0xFFE11D48),
                                        contentColor = Color.White
                                    ) {
                                        Text("$unreadNotifCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = LightTextPrimary
                            )
                        }
                    }

                    if (isAdmin) {
                        Surface(
                            color = BrandTertiaryContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .clickable { onAdminToggle() }
                                .testTag("header_admin_exit_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Exit Admin",
                                    tint = BrandTertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Exit",
                                    color = BrandTertiary,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
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
fun OrderStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.uppercase()) {
        "RECEIVED" -> Pair(StatusReceivedBg, StatusReceivedText)
        "PROCESSING" -> Pair(StatusProcessingBg, StatusProcessingText)
        "READY" -> Pair(StatusReadyBg, StatusReadyText)
        "COMPLETED" -> Pair(StatusCompletedBg, StatusCompletedText)
        "CANCELLED" -> Pair(StatusCancelledBg, StatusCancelledText)
        else -> Pair(LightSurfaceVariant, LightTextSecondary)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = status.uppercase(),
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PaymentStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.uppercase()) {
        "PAID" -> Pair(StatusPaidBg, StatusPaidText)
        "PENDING" -> Pair(StatusPendingBg, StatusPendingText)
        "FAILED" -> Pair(StatusFailedBg, StatusFailedText)
        else -> Pair(LightSurfaceVariant, LightTextSecondary)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = status.uppercase(),
                color = textColor,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun VisualOrderStepper(
    currentStatus: String,
    modifier: Modifier = Modifier
) {
    val steps = listOf("RECEIVED", "PROCESSING", "READY", "COMPLETED")
    val currentIndex = when (currentStatus.uppercase()) {
        "RECEIVED" -> 0
        "PROCESSING" -> 1
        "READY" -> 2
        "COMPLETED" -> 3
        "CANCELLED" -> -1
        else -> 0
    }

    if (currentIndex == -1) {
        Card(
            colors = CardDefaults.cardColors(containerColor = StatusCancelledBg),
            shape = RoundedCornerShape(12.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = StatusCancelledText
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Order Cancelled",
                        fontWeight = FontWeight.Bold,
                        color = StatusCancelledText
                    )
                    Text(
                        text = "This order was cancelled. Please contact shop owner for queries.",
                        style = MaterialTheme.typography.bodySmall,
                        color = StatusCancelledText
                    )
                }
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LightSurfaceVariant)
            .padding(16.dp)
    ) {
        Text(
            text = "Live Order Tracking",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = LightTextSecondary
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEachIndexed { index, step ->
                val isCompleted = index <= currentIndex
                val isCurrent = index == currentIndex

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCurrent -> BrandPrimary
                                    isCompleted -> Color(0xFF16A34A)
                                    else -> Color(0xFFCBD5E1)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted && !isCurrent) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = "${index + 1}",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = step.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp
                        ),
                        color = if (isCurrent) BrandPrimary else if (isCompleted) Color(0xFF16A34A) else LightTextTertiary,
                        textAlign = TextAlign.Center
                    )
                }

                if (index < steps.size - 1) {
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .weight(0.6f)
                            .background(
                                if (index < currentIndex) Color(0xFF16A34A) else Color(0xFFCBD5E1)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryFilterRow(
    categories: List<CategoryEntity>,
    selectedCategoryId: String?,
    onSelectCategory: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategoryId == null,
                onClick = { onSelectCategory(null) },
                label = { Text("All Services") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandPrimary,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.testTag("cat_chip_all")
            )
        }
        items(categories) { cat ->
            FilterChip(
                selected = selectedCategoryId == cat.id,
                onClick = { onSelectCategory(cat.id) },
                label = { Text(cat.name) },
                leadingIcon = {
                    Icon(
                        imageVector = getCategoryIcon(cat.icon),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandPrimary,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.testTag("cat_chip_${cat.id}")
            )
        }
    }
}

@Composable
fun ServiceItemCard(
    item: ItemEntity,
    categoryName: String?,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    discountedPrice: Double? = null,
    offerBadge: String? = null
) {
    val hasDiscount = discountedPrice != null && discountedPrice < item.price

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (hasDiscount) 1.5.dp else 1.dp,
                color = if (hasDiscount) Color(0xFF16A34A).copy(alpha = 0.5f) else LightBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onSelect() }
            .testTag("item_card_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (hasDiscount) Color(0xFFDCFCE7) else BrandPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (hasDiscount) Icons.Default.LocalOffer else getCategoryIcon(item.iconName),
                    contentDescription = item.name,
                    tint = if (hasDiscount) Color(0xFF15803D) else BrandPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = LightTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (hasDiscount) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = offerBadge ?: "OFFER",
                                color = Color(0xFF15803D),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = LightTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (hasDiscount) {
                        Text(
                            text = "₹${item.price.toInt()}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                textDecoration = TextDecoration.LineThrough
                            ),
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "₹${discountedPrice!!.toInt()}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        )
                    } else {
                        Text(
                            text = "₹${item.price.toInt()}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "• ${item.unit}",
                        style = MaterialTheme.typography.labelSmall,
                        color = LightTextTertiary
                    )
                    if (item.uploadRequired) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = BrandSecondaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "File Required",
                                color = BrandSecondary,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                color = if (hasDiscount) Color(0xFF16A34A) else BrandPrimary,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.clickable { onSelect() }
            ) {
                Text(
                    text = "Add +",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun FileUploadSection(
    uploadedFiles: List<UploadedFileDraft>,
    onAddFile: (String, String, Long, String) -> Unit,
    onRemoveFile: (String) -> Unit,
    onReplaceFile: (String, String, String, Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, LightBorder, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.UploadFile,
                    contentDescription = null,
                    tint = BrandPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Upload Documents / Photos",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Supports JPG, PNG, PDF (Camera / Gallery / Files)",
                        style = MaterialTheme.typography.bodySmall,
                        color = LightTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Upload Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val time = System.currentTimeMillis().toString().takeLast(4)
                    onAddFile("camera_snap_$time.jpg", "content://camera/snap_$time.jpg", 1850L, "image/jpeg")
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("upload_camera_btn"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Camera", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = {
                    val time = System.currentTimeMillis().toString().takeLast(4)
                    onAddFile("photo_gallery_$time.png", "content://gallery/photo_$time.png", 2400L, "image/png")
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("upload_gallery_btn"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Gallery", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = {
                    val time = System.currentTimeMillis().toString().takeLast(4)
                    onAddFile("document_print_$time.pdf", "content://docs/print_$time.pdf", 3600L, "application/pdf")
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("upload_file_btn"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("PDF File", fontSize = 12.sp)
            }
        }

        if (uploadedFiles.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Uploaded Files (${uploadedFiles.size})",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = LightTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            uploadedFiles.forEach { file ->
                Surface(
                    color = LightSurfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (file.fileType.contains("pdf")) Icons.Outlined.PictureAsPdf else Icons.Outlined.Photo,
                            contentDescription = null,
                            tint = if (file.fileType.contains("pdf")) Color(0xFFDC2626) else BrandSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = file.fileName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${file.fileSizeKb} KB • Ready for cloud sync",
                                style = MaterialTheme.typography.labelSmall,
                                color = LightTextTertiary
                            )
                        }
                        IconButton(
                            onClick = {
                                val time = System.currentTimeMillis().toString().takeLast(4)
                                onReplaceFile(file.fileName, "updated_doc_$time.pdf", "content://docs/updated_$time.pdf", 2800L, "application/pdf")
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Replace file", tint = LightTextSecondary, modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = { onRemoveFile(file.fileName) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Remove file", tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InAppNotificationBanner(
    message: String?,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = message != null,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        message?.let { text ->
            Surface(
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(10.dp),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = text,
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

fun getCategoryIcon(name: String): ImageVector {
    return when (name.lowercase()) {
        "content_copy", "copy", "photocopy" -> Icons.Default.ContentCopy
        "palette", "color", "colour" -> Icons.Default.Palette
        "photo_library", "photo" -> Icons.Default.PhotoLibrary
        "portrait", "passport" -> Icons.Default.Portrait
        "layers", "lamination" -> Icons.Default.Layers
        "description", "doc", "document" -> Icons.Default.Description
        "auto_stories", "binding", "book" -> Icons.Default.AutoStories
        else -> Icons.Default.Print
    }
}
