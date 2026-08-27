package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.local.ItemEntity
import com.example.ui.admin.*
import com.example.ui.components.HafsaHeader
import com.example.ui.components.InAppNotificationBanner
import com.example.ui.customer.*
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryContainer
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandTertiary
import com.example.ui.theme.HafsaTradersTheme
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightTextPrimary
import com.example.ui.theme.LightTextSecondary
import com.example.ui.viewmodel.AdminTab
import com.example.ui.viewmodel.CustomerTab
import com.example.ui.viewmodel.HafsaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HafsaTradersTheme {
                val navController = rememberNavController()
                val viewModel: HafsaViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = "/splash"
                ) {
                    // Splash Screen: Professional branding screen on app launch
                    composable("/splash") {
                        SplashScreen(
                            onContinue = {
                                navController.navigate("/") {
                                    popUpTo("/splash") { inclusive = true }
                                }
                            }
                        )
                    }

                    // Customer App Route: Completely separate, zero admin navigation
                    composable("/") {
                        CustomerAppContainer(
                            viewModel = viewModel,
                            navController = navController
                        )
                    }

                    // Dedicated Admin Panel Route: Protected with authentication & role verification
                    composable("/admin") {
                        AdminAppContainer(
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dedicated Customer Application Container
 * Zero Admin entry points, tabs, buttons, or hidden gestures.
 */
@Composable
fun CustomerAppContainer(
    viewModel: HafsaViewModel,
    navController: NavHostController
) {
    var isCheckingOut by remember { mutableStateOf(false) }
    var showOrderSuccess by remember { mutableStateOf(false) }
    var itemForDetailSheet by remember { mutableStateOf<ItemEntity?>(null) }
    var offerForDetailSheet by remember { mutableStateOf<com.example.data.local.OfferWithItems?>(null) }

    val customerTab by viewModel.customerTab.collectAsState()

    // Data from ViewModel
    val categories by viewModel.allCategories.collectAsState()
    val activeCategories by viewModel.activeCategories.collectAsState()
    val filteredItems by viewModel.filteredItems.collectAsState()
    val activeOffers by viewModel.activeOffersWithItems.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCatId by viewModel.selectedCategoryId.collectAsState()

    val cartItems by viewModel.cartItems.collectAsState()
    val cartTotal by viewModel.cartTotal.collectAsState()
    val cartCount by viewModel.cartItemCount.collectAsState()
    val draftFiles by viewModel.draftFiles.collectAsState()

    val custName by viewModel.customerName.collectAsState()
    val custPhone by viewModel.customerPhone.collectAsState()
    val custEmail by viewModel.customerEmail.collectAsState()
    val custAddress by viewModel.customerAddress.collectAsState()
    val specialInstructions by viewModel.specialInstructions.collectAsState()

    val customerOrders by viewModel.customerOrders.collectAsState()
    val selectedOrderDetail by viewModel.selectedOrderDetail.collectAsState()
    val lastPlacedOrder by viewModel.lastPlacedOrder.collectAsState()
    val customerNotifications by viewModel.customerNotifications.collectAsState()
    val unreadCustNotifCount = customerNotifications.count { !it.isRead }

    val shopName = viewModel.getSettingValue("shop_name", "HAFSA TRADERS")
    val shopSubtitle = viewModel.getSettingValue("shop_subtitle", "PHOTOCOPY • LAMINATION • PHOTO PRINT")
    val shopPhone = viewModel.getSettingValue("shop_phone", "+91 98765 43210")
    val shopWhatsApp = viewModel.getSettingValue("shop_whatsapp", "+91 98765 43210")
    val shopAddress = viewModel.getSettingValue("shop_address", "Shop No. 4, Main Market, Opp. City College, New Delhi - 110001")
    val shopHours = viewModel.getSettingValue("shop_hours", "Mon-Sat: 8:30 AM - 9:30 PM | Sun: 10:00 AM - 6:00 PM")
    val upiId = viewModel.getSettingValue("merchant_upi_id", "hafsatraders@okhdfcbank")
    val upiName = viewModel.getSettingValue("merchant_upi_name", "Hafsa Traders Print Shop")

    val bannerMessage by viewModel.bannerMessage.collectAsState()

    // System back: close transient UI first, then return to Home; Home lets Android exit normally.
    BackHandler(enabled = isCheckingOut || showOrderSuccess || selectedOrderDetail != null || itemForDetailSheet != null || offerForDetailSheet != null || customerTab != CustomerTab.HOME) {
        when {
            itemForDetailSheet != null -> itemForDetailSheet = null
            offerForDetailSheet != null -> offerForDetailSheet = null
            selectedOrderDetail != null -> viewModel.closeOrderDetails()
            isCheckingOut -> isCheckingOut = false
            showOrderSuccess -> {
                showOrderSuccess = false
                viewModel.setCustomerTab(CustomerTab.HOME)
            }
            customerTab != CustomerTab.HOME -> viewModel.setCustomerTab(CustomerTab.HOME)
        }
    }

    Scaffold(
        topBar = {
            if (!isCheckingOut && !showOrderSuccess) {
                Column(modifier = Modifier.statusBarsPadding()) {
                    HafsaHeader(
                        shopName = shopName,
                        shopSubtitle = shopSubtitle,
                        unreadNotifCount = unreadCustNotifCount,
                        onNotifClick = {
                            viewModel.setCustomerTab(CustomerTab.NOTIFICATIONS)
                        },
                        onAdminToggle = {},
                        onSecretAdminTrigger = {
                            navController.navigate("/admin")
                        },
                        isAdmin = false
                    )
                    InAppNotificationBanner(
                        message = bannerMessage,
                        onDismiss = { viewModel.clearBanner() }
                    )
                }
            }
        },
        bottomBar = {
            if (!isCheckingOut && !showOrderSuccess) {
                CustomerBottomNavigation(
                    selectedTab = customerTab,
                    cartCount = cartCount,
                    unreadNotifCount = unreadCustNotifCount,
                    onSelectTab = { viewModel.setCustomerTab(it) },
                    onNewOrderClick = { viewModel.setCustomerTab(CustomerTab.SERVICES) }
                )
            }
        },
        containerColor = LightBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isCheckingOut) {
                CheckoutScreen(
                    cartItems = cartItems,
                    totalAmount = cartTotal,
                    draftFiles = draftFiles,
                    customerName = custName,
                    customerPhone = custPhone,
                    customerEmail = custEmail,
                    customerAddress = custAddress,
                    specialInstructions = specialInstructions,
                    upiId = upiId,
                    upiName = upiName,
                    onUpdateQuantity = { id, qty -> viewModel.updateCartQuantity(id, qty) },
                    onRemoveItem = { id -> viewModel.removeFromCart(id) },
                    onAddFile = { name, uri, size, type -> viewModel.addDraftFile(name, uri, size, type) },
                    onRemoveFile = { name -> viewModel.removeDraftFile(name) },
                    onReplaceFile = { old, new, uri, size, type -> viewModel.replaceDraftFile(old, new, uri, size, type) },
                    onCustomerDetailsChange = { n, p, e, a -> viewModel.setCustomerDetails(n, p, e, a) },
                    onSpecialInstructionsChange = { viewModel.setSpecialInstructions(it) },
                    onBack = { isCheckingOut = false },
                    onConfirmOrder = { method, status, ref ->
                        viewModel.placeOrder(method, status, ref) {
                            isCheckingOut = false
                            showOrderSuccess = true
                        }
                    }
                )
            } else if (showOrderSuccess) {
                OrderSuccessScreen(
                    order = lastPlacedOrder,
                    onTrackOrder = { orderId ->
                        showOrderSuccess = false
                        viewModel.openOrderDetails(orderId)
                        viewModel.setCustomerTab(CustomerTab.ORDERS)
                    },
                    onBackHome = {
                        showOrderSuccess = false
                        viewModel.setCustomerTab(CustomerTab.HOME)
                    }
                )
            } else {
                when (customerTab) {
                    CustomerTab.HOME -> {
                        CustomerHomeScreen(
                            categories = activeCategories,
                            popularItems = filteredItems,
                            activeOrders = customerOrders,
                            activeOffers = activeOffers,
                            searchQuery = searchQuery,
                            selectedCategoryId = selectedCatId,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onSelectCategory = { viewModel.setSelectedCategory(it) },
                            onSelectItem = { itemForDetailSheet = it },
                            onSelectOffer = { offerForDetailSheet = it },
                            getDiscountedPrice = { viewModel.getItemDiscountedPrice(it) },
                            getActiveOffer = { viewModel.getActiveOfferForItem(it) },
                            onViewOrder = { orderId -> viewModel.openOrderDetails(orderId) },
                            onViewAllServices = { viewModel.setCustomerTab(CustomerTab.SERVICES) },
                            onStartOrder = { viewModel.setCustomerTab(CustomerTab.SERVICES) },
                            shopPhone = shopPhone,
                            shopAddress = shopAddress,
                            shopHours = shopHours
                        )
                    }
                    CustomerTab.SERVICES -> {
                        CustomerServicesScreen(
                            categories = activeCategories,
                            items = filteredItems,
                            searchQuery = searchQuery,
                            selectedCategoryId = selectedCatId,
                            cartCount = cartCount,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onSelectCategory = { viewModel.setSelectedCategory(it) },
                            onSelectItem = { itemForDetailSheet = it },
                            onOpenCart = { isCheckingOut = true },
                            getDiscountedPrice = { viewModel.getItemDiscountedPrice(it) },
                            getActiveOffer = { viewModel.getActiveOfferForItem(it) }
                        )
                    }
                    CustomerTab.ORDERS -> {
                        CustomerOrdersScreen(
                            orders = customerOrders,
                            onSelectOrder = { viewModel.openOrderDetails(it) }
                        )
                    }
                    CustomerTab.NOTIFICATIONS -> {
                        CustomerNotificationsScreen(
                            notifications = customerNotifications,
                            onNotificationClick = { notif ->
                                viewModel.markNotificationRead(notif.id)
                                notif.orderId?.let { viewModel.openOrderDetails(it) }
                            },
                            onMarkAllRead = { viewModel.markAllNotificationsRead("CUSTOMER") }
                        )
                    }
                    CustomerTab.PROFILE -> {
                        CustomerProfileScreen(
                            customerName = custName,
                            customerPhone = custPhone,
                            customerEmail = custEmail,
                            customerAddress = custAddress,
                            shopName = shopName,
                            shopSubtitle = shopSubtitle,
                            shopPhone = shopPhone,
                            shopWhatsApp = shopWhatsApp,
                            shopAddress = shopAddress,
                            shopHours = shopHours,
                            onSaveProfile = { n, p, e, a -> viewModel.setCustomerDetails(n, p, e, a) },
                            onAdminLogin = { navController.navigate("/admin") }
                        )
                    }
                }
            }
        }
    }

    // Customer Item Detail Sheet
    ItemDetailBottomSheet(
        item = itemForDetailSheet,
        onDismiss = { itemForDetailSheet = null },
        onAddToCart = { item, qty -> viewModel.addToCart(item, qty) },
        onDirectOrder = { item, qty ->
            viewModel.addToCart(item, qty)
            isCheckingOut = true
        }
    )

    // Customer Offer Detail Bottom Sheet
    if (offerForDetailSheet != null) {
        CustomerOfferDetailBottomSheet(
            offerWithItems = offerForDetailSheet!!,
            onDismiss = { offerForDetailSheet = null },
            onSelectItem = { item ->
                itemForDetailSheet = item
            }
        )
    }

    // Customer Order Details Sheet
    if (selectedOrderDetail != null) {
        CustomerOrderDetailBottomSheet(
            orderDetails = selectedOrderDetail,
            onDismiss = { viewModel.closeOrderDetails() },
            shopPhone = shopPhone,
            shopWhatsApp = shopWhatsApp
        )
    }
}

/**
 * Dedicated Hafsa Traders Admin Panel Container
 * Protected via PIN Authentication.
 */
@Composable
fun AdminAppContainer(
    viewModel: HafsaViewModel,
    navController: NavHostController
) {
    val isAdminAuth by viewModel.isAdminAuthenticated.collectAsState()
    val adminLoginError by viewModel.adminLoginError.collectAsState()
    val adminTab by viewModel.adminTab.collectAsState()

    var itemForAdminAddEdit by remember { mutableStateOf<ItemEntity?>(null) }
    var showAdminAddEditSheet by remember { mutableStateOf(false) }

    // Admin Metrics (Real Data from Database)
    val totalOrders by viewModel.totalOrdersCount.collectAsState()
    val newOrders by viewModel.newOrdersCount.collectAsState()
    val processingOrders by viewModel.processingOrdersCount.collectAsState()
    val readyOrders by viewModel.readyOrdersCount.collectAsState()
    val completedOrders by viewModel.completedOrdersCount.collectAsState()
    val totalRevenue by viewModel.totalRevenue.collectAsState()
    val pendingRevenue by viewModel.pendingRevenue.collectAsState()

    val allOrders by viewModel.allOrders.collectAsState()
    val allItems by viewModel.allItems.collectAsState()
    val categories by viewModel.allCategories.collectAsState()
    val allOffers by viewModel.offersWithItems.collectAsState()
    val adminNotifications by viewModel.adminNotifications.collectAsState()
    val unreadAdminNotifCount = adminNotifications.count { !it.isRead }
    val selectedOrderDetail by viewModel.selectedOrderDetail.collectAsState()

    val shopName = viewModel.getSettingValue("shop_name", "HAFSA TRADERS")
    val shopSubtitle = viewModel.getSettingValue("shop_subtitle", "PHOTOCOPY • LAMINATION • PHOTO PRINT")
    val shopPhone = viewModel.getSettingValue("shop_phone", "+91 98765 43210")
    val shopWhatsApp = viewModel.getSettingValue("shop_whatsapp", "+91 98765 43210")
    val shopAddress = viewModel.getSettingValue("shop_address", "Shop No. 4, Main Market, Opp. City College, New Delhi - 110001")
    val shopHours = viewModel.getSettingValue("shop_hours", "Mon-Sat: 8:30 AM - 9:30 PM | Sun: 10:00 AM - 6:00 PM")
    val upiId = viewModel.getSettingValue("merchant_upi_id", "hafsatraders@okhdfcbank")
    val upiName = viewModel.getSettingValue("merchant_upi_name", "Hafsa Traders Print Shop")

    val bannerMessage by viewModel.bannerMessage.collectAsState()
    val adminEmail = viewModel.getSettingValue("admin_email", "admin@hafsatraders.com")
    val isAdminLoading by viewModel.isAdminLoading.collectAsState()

    // System back in admin returns to the previous admin section, then exits admin.
    BackHandler(enabled = isAdminAuth) {
        if (selectedOrderDetail != null) {
            viewModel.closeOrderDetails()
        } else if (showAdminAddEditSheet) {
            showAdminAddEditSheet = false
        } else if (adminTab != AdminTab.DASHBOARD) {
            viewModel.setAdminTab(AdminTab.DASHBOARD)
        } else {
            viewModel.logoutAdmin()
            navController.navigate("/") { launchSingleTop = true }
        }
    }

    if (!isAdminAuth) {
        BackHandler { navController.navigate("/") { launchSingleTop = true } }
        AdminLoginScreen(
            errorMessage = adminLoginError,
            isLoading = isAdminLoading,
            onLogin = { email, password ->
                viewModel.loginAdmin(
                    email = email,
                    password = password,
                    onSuccess = { /* navigated inside admin */ },
                    onUnauthorized = { navController.navigate("/") }
                )
            },
            onBackToCustomer = { navController.navigate("/") }
        )
        return
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                HafsaHeader(
                    shopName = "HAFSA TRADERS — ADMIN",
                    shopSubtitle = "Live Operations & Store Control",
                    unreadNotifCount = unreadAdminNotifCount,
                    onNotifClick = {
                        viewModel.setAdminTab(AdminTab.NOTIFICATIONS)
                    },
                    onAdminToggle = {
                        viewModel.logoutAdmin()
                        navController.navigate("/")
                    },
                    isAdmin = true
                )
                InAppNotificationBanner(
                    message = bannerMessage,
                    onDismiss = { viewModel.clearBanner() }
                )
            }
        },
        bottomBar = {
            AdminBottomNavigation(
                selectedTab = adminTab,
                newOrdersCount = newOrders,
                onSelectTab = { viewModel.setAdminTab(it) }
            )
        },
        containerColor = LightBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (adminTab) {
                AdminTab.DASHBOARD -> {
                    AdminDashboardScreen(
                        totalOrders = totalOrders,
                        newOrders = newOrders,
                        processingOrders = processingOrders,
                        readyOrders = readyOrders,
                        completedOrders = completedOrders,
                        totalRevenue = totalRevenue,
                        pendingRevenue = pendingRevenue,
                        recentOrders = allOrders,
                        onSelectOrder = { viewModel.openOrderDetails(it) },
                        onNavigateTab = { viewModel.setAdminTab(it) },
                        onAddNewItem = {
                            itemForAdminAddEdit = null
                            showAdminAddEditSheet = true
                        }
                    )
                }
                AdminTab.ORDERS -> {
                    AdminOrdersScreen(
                        orders = allOrders,
                        onSelectOrder = { viewModel.openOrderDetails(it) },
                        onUpdateStatus = { id, st -> viewModel.updateOrderStatus(id, st) }
                    )
                }
                AdminTab.ITEMS -> {
                    AdminItemsScreen(
                        items = allItems,
                        categories = categories,
                        onAddNewItem = {
                            itemForAdminAddEdit = null
                            showAdminAddEditSheet = true
                        },
                        onEditItem = { item ->
                            itemForAdminAddEdit = item
                            showAdminAddEditSheet = true
                        },
                        onQuickPriceUpdate = { id, price -> viewModel.updateItemPrice(id, price) },
                        onToggleActive = { item -> viewModel.toggleItemActive(item) },
                        onDeleteItem = { id, name -> viewModel.deleteItem(id, name) }
                    )
                }
                AdminTab.CATEGORIES -> {
                    AdminCategoriesScreen(
                        categories = categories,
                        onAddCategory = { name, icon -> viewModel.addCategory(name, icon) },
                        onUpdateCategory = { cat -> viewModel.updateCategory(cat) },
                        onDeleteCategory = { id, name -> viewModel.deleteCategory(id, name) }
                    )
                }
                AdminTab.OFFERS -> {
                    AdminOffersScreen(
                        offersWithItems = allOffers,
                        allItems = allItems,
                        onCreateOffer = { title, desc, img, type, valD, start, exp, items, order, enabled, notif ->
                            viewModel.createOffer(
                                title = title,
                                description = desc,
                                imageUrl = img,
                                offerType = type,
                                offerValue = valD,
                                startAt = start,
                                expiresAt = exp,
                                selectedItemIds = items,
                                displayOrder = order,
                                isEnabled = enabled,
                                notifyCustomers = notif
                            )
                        },
                        onUpdateOffer = { offer, items ->
                            viewModel.updateOffer(offer, items)
                        },
                        onDeleteOffer = { id, title ->
                            viewModel.deleteOffer(id, title)
                        },
                        onToggleEnabled = { offer ->
                            viewModel.toggleOfferEnabled(offer)
                        }
                    )
                }
                AdminTab.PAYMENTS -> {
                    AdminPaymentSettingsScreen(
                        currentUpiId = upiId,
                        currentUpiName = upiName,
                        onSaveUpi = { id, name ->
                            viewModel.updateSetting("merchant_upi_id", id)
                            viewModel.updateSetting("merchant_upi_name", name)
                        }
                    )
                }
                AdminTab.NOTIFICATIONS -> {
                    AdminNotificationsScreen(
                        notifications = adminNotifications,
                        onNotificationClick = { notif ->
                            viewModel.markNotificationRead(notif.id)
                            notif.orderId?.let { viewModel.openOrderDetails(it) }
                        },
                        onMarkAllRead = { viewModel.markAllNotificationsRead("ADMIN") }
                    )
                }
                AdminTab.SETTINGS -> {
                    AdminShopSettingsScreen(
                        shopName = shopName,
                        shopSubtitle = shopSubtitle,
                        shopPhone = shopPhone,
                        shopWhatsApp = shopWhatsApp,
                        shopAddress = shopAddress,
                        shopHours = shopHours,
                        adminEmail = adminEmail,
                        onSaveShopInfo = { n, s, p, w, a, h ->
                            viewModel.updateSetting("shop_name", n)
                            viewModel.updateSetting("shop_subtitle", s)
                            viewModel.updateSetting("shop_phone", p)
                            viewModel.updateSetting("shop_whatsapp", w)
                            viewModel.updateSetting("shop_address", a)
                            viewModel.updateSetting("shop_hours", h)
                        },
                        onUpdateAdminCredentials = { email, _ ->
                            viewModel.updateSetting("admin_email", email)
                        },
                        onLogout = {
                            viewModel.logoutAdmin()
                            navController.navigate("/")
                        }
                    )
                }
            }
        }
    }

    // Admin Order Details Sheet
    if (selectedOrderDetail != null) {
        AdminOrderDetailBottomSheet(
            orderDetails = selectedOrderDetail,
            onDismiss = { viewModel.closeOrderDetails() },
            onUpdateStatus = { id, st -> viewModel.updateOrderStatus(id, st) },
            onUpdatePayment = { id, pst, ref -> viewModel.updatePaymentStatus(id, pst, ref) }
        )
    }

    // Admin Add/Edit Item Sheet
    if (showAdminAddEditSheet) {
        AddEditItemBottomSheet(
            itemToEdit = itemForAdminAddEdit,
            categories = categories,
            onDismiss = { showAdminAddEditSheet = false },
            onSave = { catId, name, desc, price, unit, minQ, maxQ, upload, icon ->
                if (itemForAdminAddEdit == null) {
                    viewModel.addNewItem(catId, name, desc, price, unit, minQ, maxQ, upload, icon)
                } else {
                    viewModel.updateItem(
                        itemForAdminAddEdit!!.copy(
                            categoryId = catId,
                            name = name,
                            description = desc,
                            price = price,
                            unit = unit,
                            minQuantity = minQ,
                            maxQuantity = maxQ,
                            uploadRequired = upload,
                            iconName = icon
                        )
                    )
                }
                showAdminAddEditSheet = false
            }
        )
    }
}

@Composable
fun CustomerBottomNavigation(
    selectedTab: CustomerTab,
    cartCount: Int,
    unreadNotifCount: Int,
    onSelectTab: (CustomerTab) -> Unit,
    onNewOrderClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        NavigationBarItem(
            selected = selectedTab == CustomerTab.HOME,
            onClick = { onSelectTab(CustomerTab.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                selectedTextColor = BrandPrimary,
                indicatorColor = BrandPrimaryContainer
            ),
            modifier = Modifier.testTag("nav_customer_home")
        )
        NavigationBarItem(
            selected = selectedTab == CustomerTab.SERVICES,
            onClick = { onSelectTab(CustomerTab.SERVICES) },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(containerColor = Color(0xFFE11D48), contentColor = Color.White) {
                                Text("$cartCount")
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Description, contentDescription = "Services")
                }
            },
            label = { Text("Services") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                selectedTextColor = BrandPrimary,
                indicatorColor = BrandPrimaryContainer
            ),
            modifier = Modifier.testTag("nav_customer_services")
        )
        NavigationBarItem(
            selected = false,
            onClick = onNewOrderClick,
            icon = {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(BrandPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = "New Order",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = { Text("New Order", fontWeight = FontWeight.Bold, color = BrandPrimary) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                selectedTextColor = BrandPrimary,
                indicatorColor = Color.Transparent
            ),
            modifier = Modifier.testTag("nav_customer_new_order")
        )
        NavigationBarItem(
            selected = selectedTab == CustomerTab.ORDERS,
            onClick = { onSelectTab(CustomerTab.ORDERS) },
            icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Orders") },
            label = { Text("Orders") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                selectedTextColor = BrandPrimary,
                indicatorColor = BrandPrimaryContainer
            ),
            modifier = Modifier.testTag("nav_customer_orders")
        )
        NavigationBarItem(
            selected = selectedTab == CustomerTab.PROFILE,
            onClick = { onSelectTab(CustomerTab.PROFILE) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                selectedTextColor = BrandPrimary,
                indicatorColor = BrandPrimaryContainer
            ),
            modifier = Modifier.testTag("nav_customer_profile")
        )
    }
}

@Composable
fun AdminBottomNavigation(
    selectedTab: AdminTab,
    newOrdersCount: Int,
    onSelectTab: (AdminTab) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        NavigationBarItem(
            selected = selectedTab == AdminTab.DASHBOARD,
            onClick = { onSelectTab(AdminTab.DASHBOARD) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Overview") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                selectedTextColor = BrandPrimary,
                indicatorColor = BrandPrimaryContainer
            ),
            modifier = Modifier.testTag("nav_admin_dashboard")
        )
        NavigationBarItem(
            selected = selectedTab == AdminTab.ORDERS,
            onClick = { onSelectTab(AdminTab.ORDERS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (newOrdersCount > 0) {
                            Badge(containerColor = Color(0xFFE11D48), contentColor = Color.White) {
                                Text("$newOrdersCount")
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = "Orders")
                }
            },
            label = { Text("Orders") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                selectedTextColor = BrandPrimary,
                indicatorColor = BrandPrimaryContainer
            ),
            modifier = Modifier.testTag("nav_admin_orders")
        )
        NavigationBarItem(
            selected = selectedTab == AdminTab.ITEMS,
            onClick = { onSelectTab(AdminTab.ITEMS) },
            icon = { Icon(Icons.Default.Inventory2, contentDescription = "Items") },
            label = { Text("Items") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                selectedTextColor = BrandPrimary,
                indicatorColor = BrandPrimaryContainer
            ),
            modifier = Modifier.testTag("nav_admin_items")
        )
        NavigationBarItem(
            selected = selectedTab == AdminTab.OFFERS,
            onClick = { onSelectTab(AdminTab.OFFERS) },
            icon = { Icon(Icons.Default.LocalOffer, contentDescription = "Offers") },
            label = { Text("Offers") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                selectedTextColor = BrandPrimary,
                indicatorColor = BrandPrimaryContainer
            ),
            modifier = Modifier.testTag("nav_admin_offers")
        )
        NavigationBarItem(
            selected = selectedTab == AdminTab.CATEGORIES,
            onClick = { onSelectTab(AdminTab.CATEGORIES) },
            icon = { Icon(Icons.Default.Category, contentDescription = "Categories") },
            label = { Text("Categories") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                selectedTextColor = BrandPrimary,
                indicatorColor = BrandPrimaryContainer
            ),
            modifier = Modifier.testTag("nav_admin_categories")
        )
        NavigationBarItem(
            selected = selectedTab == AdminTab.PAYMENTS,
            onClick = { onSelectTab(AdminTab.PAYMENTS) },
            icon = { Icon(Icons.Default.QrCode, contentDescription = "Payments") },
            label = { Text("UPI QR") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                selectedTextColor = BrandPrimary,
                indicatorColor = BrandPrimaryContainer
            ),
            modifier = Modifier.testTag("nav_admin_payments")
        )
        NavigationBarItem(
            selected = selectedTab == AdminTab.SETTINGS,
            onClick = { onSelectTab(AdminTab.SETTINGS) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                selectedTextColor = BrandPrimary,
                indicatorColor = BrandPrimaryContainer
            ),
            modifier = Modifier.testTag("nav_admin_settings")
        )
    }
}
