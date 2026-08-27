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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OrderEntity
import com.example.data.repository.CartItem
import com.example.data.repository.UploadedFileDraft
import com.example.ui.components.FileUploadSection
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.PaymentStatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartItems: List<CartItem>,
    totalAmount: Double,
    draftFiles: List<UploadedFileDraft>,
    customerName: String,
    customerPhone: String,
    customerEmail: String,
    customerAddress: String,
    specialInstructions: String,
    upiId: String,
    upiName: String,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onAddFile: (String, String, Long, String) -> Unit,
    onRemoveFile: (String) -> Unit,
    onReplaceFile: (String, String, String, Long, String) -> Unit,
    onCustomerDetailsChange: (String, String, String, String) -> Unit,
    onSpecialInstructionsChange: (String) -> Unit,
    onBack: () -> Unit,
    onConfirmOrder: (paymentMethod: String, paymentStatus: String, paymentRef: String) -> Unit
) {
    var name by remember { mutableStateOf(customerName) }
    var phone by remember { mutableStateOf(customerPhone) }
    var email by remember { mutableStateOf(customerEmail) }
    var address by remember { mutableStateOf(customerAddress) }
    var instructions by remember { mutableStateOf(specialInstructions) }

    var selectedPaymentMethod by remember { mutableStateOf("UPI QR Scan") }
    var transactionRef by remember { mutableStateOf("") }
    var isProcessingPayment by remember { mutableStateOf(false) }

    val quickInstructionChips = listOf(
        "Print on A4 colour",
        "Glossy photo paper",
        "Cut passport photos",
        "Lamination required",
        "Spiral bind after print",
        "High quality black & white"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout & Payment", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = LightTextPrimary
                )
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Amount", style = MaterialTheme.typography.labelSmall, color = LightTextSecondary)
                        Text(
                            text = "₹${totalAmount.toInt()}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary
                            )
                        )
                    }

                    Button(
                        onClick = {
                            if (name.isBlank() || phone.isBlank()) return@Button
                            isProcessingPayment = true
                            val paymentStatus = if (selectedPaymentMethod == "Cash on Counter") "PENDING" else "PAID"
                            val ref = if (transactionRef.isNotBlank()) transactionRef else "UPI/HT/" + System.currentTimeMillis().toString().takeLast(6)
                            onCustomerDetailsChange(name, phone, email, address)
                            onSpecialInstructionsChange(instructions)
                            onConfirmOrder(selectedPaymentMethod, paymentStatus, ref)
                        },
                        enabled = cartItems.isNotEmpty() && name.isNotBlank() && phone.isNotBlank() && !isProcessingPayment,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("checkout_confirm_pay_btn")
                    ) {
                        if (isProcessingPayment) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Processing...")
                        } else {
                            Text(
                                text = if (selectedPaymentMethod == "Cash on Counter") "Place Order (Pay ₹${totalAmount.toInt()})" else "Pay ₹${totalAmount.toInt()} & Place Order",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        containerColor = LightBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Order Items Summary
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.border(1.dp, LightBorder, RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Order Summary (${cartItems.size} items)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LightTextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        cartItems.forEachIndexed { index, cartItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = cartItem.item.name,
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "₹${cartItem.item.price.toInt()} × ${cartItem.quantity} ${cartItem.item.unit}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = LightTextTertiary
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onUpdateQuantity(cartItem.item.id, cartItem.quantity - 1) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                    }
                                    Text(
                                        text = "${cartItem.quantity}",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    IconButton(
                                        onClick = { onUpdateQuantity(cartItem.item.id, cartItem.quantity + 1) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "₹${cartItem.subtotal.toInt()}",
                                        fontWeight = FontWeight.Bold,
                                        color = BrandPrimary
                                    )
                                    IconButton(
                                        onClick = { onRemoveItem(cartItem.item.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            if (index < cartItems.size - 1) {
                                Divider(color = LightBorder, thickness = 0.8.dp)
                            }
                        }

                        Divider(color = LightBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal", color = LightTextSecondary)
                            Text("₹${totalAmount.toInt()}", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. File Upload Section
            item {
                FileUploadSection(
                    uploadedFiles = draftFiles,
                    onAddFile = onAddFile,
                    onRemoveFile = onRemoveFile,
                    onReplaceFile = onReplaceFile
                )
            }

            // 3. Special Instructions
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.border(1.dp, LightBorder, RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Special Printing Instructions (Optional)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = LightTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(quickInstructionChips) { chip ->
                                Surface(
                                    color = LightSurfaceVariant,
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.clickable {
                                        instructions = if (instructions.isBlank()) chip else "$instructions, $chip"
                                    }
                                ) {
                                    Text(
                                        text = "+ $chip",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BrandPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = instructions,
                            onValueChange = { instructions = it },
                            placeholder = { Text("E.g., Print back to back, cut passport photos, glossy paper for cover...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_special_instructions_input"),
                            minLines = 2,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = LightBackground,
                                unfocusedContainerColor = LightBackground,
                                focusedBorderColor = BrandPrimary,
                                unfocusedBorderColor = LightBorder
                            )
                        )
                    }
                }
            }

            // 4. Customer Details Form
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.border(1.dp, LightBorder, RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Customer Information",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = LightTextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Customer Name *") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_name_input"),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Mobile Number (for pickup / SMS) *") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_phone_input"),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Shop Pickup or Delivery Note (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // 5. Payment Selection (Admin Configured)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.border(1.dp, LightBorder, RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Select Payment Option",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = LightTextPrimary
                            )
                            Surface(
                                color = Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("100% Secure", color = Color(0xFF16A34A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Option 1: UPI QR Scan
                        Surface(
                            color = if (selectedPaymentMethod == "UPI QR Scan") BrandPrimaryContainer else LightSurfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPaymentMethod = "UPI QR Scan" }
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedPaymentMethod == "UPI QR Scan",
                                    onClick = { selectedPaymentMethod = "UPI QR Scan" },
                                    colors = RadioButtonDefaults.colors(selectedColor = BrandPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.QrCode2, contentDescription = null, tint = BrandPrimary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("UPI QR Code Scan & Pay", fontWeight = FontWeight.SemiBold)
                                    Text("Scan with GPay, PhonePe, Paytm, BHIM", style = MaterialTheme.typography.labelSmall, color = LightTextSecondary)
                                }
                            }
                        }

                        // UPI QR Details preview when selected
                        if (selectedPaymentMethod == "UPI QR Scan") {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = LightSurfaceVariant),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White)
                                            .border(1.dp, LightBorder, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.QrCode,
                                            contentDescription = "Hafsa Traders UPI QR",
                                            tint = BrandPrimary,
                                            modifier = Modifier.size(90.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("UPI ID: $upiId", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandPrimary)
                                    Text("Payee: $upiName", style = MaterialTheme.typography.labelSmall, color = LightTextSecondary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = transactionRef,
                                        onValueChange = { transactionRef = it },
                                        placeholder = { Text("Enter UPI UTR / Transaction No. (Optional)") },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                }
                            }
                        }

                        // Option 2: UPI Direct Intent
                        Surface(
                            color = if (selectedPaymentMethod == "UPI Direct") BrandPrimaryContainer else LightSurfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPaymentMethod = "UPI Direct" }
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedPaymentMethod == "UPI Direct",
                                    onClick = { selectedPaymentMethod = "UPI Direct" },
                                    colors = RadioButtonDefaults.colors(selectedColor = BrandPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.Payment, contentDescription = null, tint = BrandSecondary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("UPI Instant App Pay", fontWeight = FontWeight.SemiBold)
                                    Text("Direct checkout to GPay / PhonePe / Paytm", style = MaterialTheme.typography.labelSmall, color = LightTextSecondary)
                                }
                            }
                        }

                        // Option 3: Online Payment Gateway
                        Surface(
                            color = if (selectedPaymentMethod == "Online Payment Gateway") BrandPrimaryContainer else LightSurfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPaymentMethod = "Online Payment Gateway" }
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedPaymentMethod == "Online Payment Gateway",
                                    onClick = { selectedPaymentMethod = "Online Payment Gateway" },
                                    colors = RadioButtonDefaults.colors(selectedColor = BrandPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.CreditCard, contentDescription = null, tint = BrandTertiary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Online Gateway (Cards / NetBanking)", fontWeight = FontWeight.SemiBold)
                                    Text("All Debit/Credit Cards & NetBanking", style = MaterialTheme.typography.labelSmall, color = LightTextSecondary)
                                }
                            }
                        }

                        // Option 4: Cash on Counter
                        Surface(
                            color = if (selectedPaymentMethod == "Cash on Counter") BrandPrimaryContainer else LightSurfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPaymentMethod = "Cash on Counter" }
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedPaymentMethod == "Cash on Counter",
                                    onClick = { selectedPaymentMethod = "Cash on Counter" },
                                    colors = RadioButtonDefaults.colors(selectedColor = BrandPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.Money, contentDescription = null, tint = Color(0xFF16A34A))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Pay at Shop Counter (Cash on Pickup)", fontWeight = FontWeight.SemiBold)
                                    Text("Pay cash when collecting your ready prints", style = MaterialTheme.typography.labelSmall, color = LightTextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderSuccessScreen(
    order: OrderEntity?,
    onTrackOrder: (String) -> Unit,
    onBackHome: () -> Unit
) {
    if (order == null) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .border(1.dp, LightBorder, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Order Placed Successfully!",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = LightTextPrimary
                )

                Text(
                    text = "Hafsa Traders has received your order.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightTextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    color = LightSurfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Order ID", color = LightTextSecondary)
                            Text("#${order.orderNumber}", fontWeight = FontWeight.Bold, color = BrandPrimary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Amount", color = LightTextSecondary)
                            Text("₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Order Status", color = LightTextSecondary)
                            OrderStatusBadge(status = order.orderStatus)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Payment", color = LightTextSecondary)
                            PaymentStatusBadge(status = order.paymentStatus)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onTrackOrder(order.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("success_track_order_btn")
                ) {
                    Text("Track Live Order Progress", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onBackHome,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightSurfaceVariant,
                        contentColor = LightTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("success_back_home_btn")
                ) {
                    Text("Back to Home")
                }
            }
        }
    }
}
