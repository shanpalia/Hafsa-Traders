package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AdminSettingEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.HafsaDatabase
import com.example.data.local.ItemEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.OfferEntity
import com.example.data.local.OfferItemEntity
import com.example.data.local.OfferWithItems
import com.example.data.local.OrderEntity
import com.example.data.repository.CartItem
import com.example.data.repository.HafsaRepository
import com.example.data.repository.OrderWithDetails
import com.example.data.repository.UploadedFileDraft
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppRole {
    CUSTOMER, ADMIN
}

enum class CustomerTab {
    HOME, SERVICES, ORDERS, NOTIFICATIONS, PROFILE
}

enum class AdminTab {
    DASHBOARD, ORDERS, ITEMS, CATEGORIES, OFFERS, PAYMENTS, NOTIFICATIONS, SETTINGS
}

class HafsaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HafsaRepository

    init {
        val db = HafsaDatabase.getDatabase(application, viewModelScope)
        repository = HafsaRepository(db.hafsaDao())
    }

    // Role & Navigation
    private val _currentRole = MutableStateFlow(AppRole.CUSTOMER)
    val currentRole: StateFlow<AppRole> = _currentRole.asStateFlow()

    private val _customerTab = MutableStateFlow(CustomerTab.HOME)
    val customerTab: StateFlow<CustomerTab> = _customerTab.asStateFlow()

    private val _adminTab = MutableStateFlow(AdminTab.DASHBOARD)
    val adminTab: StateFlow<AdminTab> = _adminTab.asStateFlow()

    // Admin Authentication
    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    private val _adminLoginError = MutableStateFlow<String?>(null)
    val adminLoginError: StateFlow<String?> = _adminLoginError.asStateFlow()
    private val _isAdminLoading = MutableStateFlow(false)
    val isAdminLoading: StateFlow<Boolean> = _isAdminLoading.asStateFlow()

    // Categories & Items from DB
    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCategories: StateFlow<List<CategoryEntity>> = repository.activeCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allItems: StateFlow<List<ItemEntity>> = repository.allItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeItems: StateFlow<List<ItemEntity>> = repository.activeItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Offers from DB
    val offersWithItems: StateFlow<List<OfferWithItems>> = repository.getOffersWithItemsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeOffersWithItems: StateFlow<List<OfferWithItems>> = repository.getActiveOffersWithItemsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedOfferForDetail = MutableStateFlow<OfferWithItems?>(null)
    val selectedOfferForDetail: StateFlow<OfferWithItems?> = _selectedOfferForDetail.asStateFlow()

    // Search & Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    // Filtered Items for Customer
    val filteredItems: StateFlow<List<ItemEntity>> = combine(
        activeItems,
        _searchQuery,
        _selectedCategoryId
    ) { items, query, catId ->
        items.filter { item ->
            val matchesQuery = query.isBlank() ||
                item.name.contains(query, ignoreCase = true) ||
                item.description.contains(query, ignoreCase = true)
            val matchesCategory = catId == null || item.categoryId == catId
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart & Order Building
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    val cartTotal: StateFlow<Double> = _cartItems.map { list ->
        list.sumOf { it.subtotal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartItemCount: StateFlow<Int> = _cartItems.map { list ->
        list.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Uploaded Files for Current Order
    private val _draftFiles = MutableStateFlow<List<UploadedFileDraft>>(emptyList())
    val draftFiles: StateFlow<List<UploadedFileDraft>> = _draftFiles.asStateFlow()

    // Order Instructions & Customer Details
    private val _specialInstructions = MutableStateFlow("")
    val specialInstructions: StateFlow<String> = _specialInstructions.asStateFlow()

    private val _customerName = MutableStateFlow("Rahul Sharma")
    val customerName: StateFlow<String> = _customerName.asStateFlow()

    private val _customerPhone = MutableStateFlow("+91 98112 34567")
    val customerPhone: StateFlow<String> = _customerPhone.asStateFlow()

    private val _customerEmail = MutableStateFlow("rahul.sharma@example.com")
    val customerEmail: StateFlow<String> = _customerEmail.asStateFlow()

    private val _customerAddress = MutableStateFlow("Shop pickup / Delhi")
    val customerAddress: StateFlow<String> = _customerAddress.asStateFlow()

    // Orders Flow
    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Customer Orders (Current User)
    val customerOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Order Detail Inspection
    private val _selectedOrderDetail = MutableStateFlow<OrderWithDetails?>(null)
    val selectedOrderDetail: StateFlow<OrderWithDetails?> = _selectedOrderDetail.asStateFlow()

    // Latest Created Order (for Confirmation Screen)
    private val _lastPlacedOrder = MutableStateFlow<OrderEntity?>(null)
    val lastPlacedOrder: StateFlow<OrderEntity?> = _lastPlacedOrder.asStateFlow()

    // Notifications
    val customerNotifications: StateFlow<List<NotificationEntity>> = repository.getCustomerNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminNotifications: StateFlow<List<NotificationEntity>> = repository.getAdminNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Dashboard Metrics
    val totalOrdersCount = repository.totalOrdersCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val newOrdersCount = repository.newOrdersCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val processingOrdersCount = repository.processingOrdersCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val readyOrdersCount = repository.readyOrdersCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val completedOrdersCount = repository.completedOrdersCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val totalRevenue = repository.totalRevenue.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    val pendingRevenue = repository.pendingRevenue.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Admin Settings
    val adminSettings: StateFlow<List<AdminSettingEntity>> = repository.allSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Toast / In-App Notification Banner
    private val _bannerMessage = MutableStateFlow<String?>(null)
    val bannerMessage: StateFlow<String?> = _bannerMessage.asStateFlow()

    // --- ACTIONS ---

    fun setRole(role: AppRole) {
        _currentRole.value = role
    }

    fun setCustomerTab(tab: CustomerTab) {
        _customerTab.value = tab
    }

    fun setAdminTab(tab: AdminTab) {
        _adminTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(categoryId: String?) {
        _selectedCategoryId.value = categoryId
    }

    fun setCustomerDetails(name: String, phone: String, email: String, address: String) {
        _customerName.value = name
        _customerPhone.value = phone
        _customerEmail.value = email
        _customerAddress.value = address
    }

    fun setSpecialInstructions(instructions: String) {
        _specialInstructions.value = instructions
    }

    // Cart Management
    fun addToCart(item: ItemEntity, quantity: Int = 1) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.item.id == item.id }
        val validQty = quantity.coerceIn(item.minQuantity, item.maxQuantity)
        if (index >= 0) {
            val existing = current[index]
            val newQty = (existing.quantity + quantity).coerceIn(item.minQuantity, item.maxQuantity)
            current[index] = existing.copy(quantity = newQty)
        } else {
            current.add(CartItem(item, validQty))
        }
        _cartItems.value = current
        showBanner("Added ${item.name} to cart")
    }

    fun updateCartQuantity(itemId: String, quantity: Int) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.item.id == itemId }
        if (index >= 0) {
            val item = current[index].item
            if (quantity <= 0) {
                current.removeAt(index)
            } else {
                val clamped = quantity.coerceIn(item.minQuantity, item.maxQuantity)
                current[index] = current[index].copy(quantity = clamped)
            }
            _cartItems.value = current
        }
    }

    fun removeFromCart(itemId: String) {
        _cartItems.value = _cartItems.value.filterNot { it.item.id == itemId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _draftFiles.value = emptyList()
        _specialInstructions.value = ""
    }

    // File Upload Management
    fun addDraftFile(name: String, uri: String, sizeKb: Long, type: String) {
        val draft = UploadedFileDraft(
            fileName = name,
            fileUri = uri,
            fileSizeKb = sizeKb,
            fileType = type
        )
        _draftFiles.value = _draftFiles.value + draft
        showBanner("Uploaded: $name")
    }

    fun removeDraftFile(fileName: String) {
        _draftFiles.value = _draftFiles.value.filterNot { it.fileName == fileName }
    }

    fun replaceDraftFile(oldName: String, newName: String, uri: String, sizeKb: Long, type: String) {
        val updated = _draftFiles.value.map {
            if (it.fileName == oldName) {
                UploadedFileDraft(newName, uri, sizeKb, type)
            } else it
        }
        _draftFiles.value = updated
        showBanner("Replaced: $newName")
    }

    // Order Placement (with Real Backend Price Verification & Sequential HT1000X generation)
    fun placeOrder(
        paymentMethod: String,
        paymentStatus: String,
        paymentRef: String,
        onSuccess: (OrderEntity) -> Unit
    ) {
        if (_cartItems.value.isEmpty()) {
            showBanner("Cart is empty!")
            return
        }

        viewModelScope.launch {
            try {
                val order = repository.placeOrder(
                    userId = "cust_" + _customerPhone.value.filter { it.isDigit() }.takeLast(4).ifEmpty { "1001" },
                    customerName = _customerName.value.ifBlank { "Customer" },
                    customerPhone = _customerPhone.value.ifBlank { "+91 98765 43210" },
                    customerEmail = _customerEmail.value,
                    customerAddress = _customerAddress.value,
                    specialInstructions = _specialInstructions.value,
                    cartItems = _cartItems.value,
                    uploadedFiles = _draftFiles.value,
                    paymentMethod = paymentMethod,
                    paymentStatus = paymentStatus,
                    paymentRef = paymentRef
                )
                _lastPlacedOrder.value = order
                clearCart()
                onSuccess(order)
                showBanner("Order #${order.orderNumber} placed successfully!")
            } catch (e: Exception) {
                showBanner("Error placing order: ${e.message}")
            }
        }
    }

    // Order Inspection
    fun openOrderDetails(orderId: String) {
        viewModelScope.launch {
            val details = repository.getOrderWithDetails(orderId)
            _selectedOrderDetail.value = details
        }
    }

    fun closeOrderDetails() {
        _selectedOrderDetail.value = null
    }

    // Admin Status Update (triggers instant push notification to customer)
    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
            // Refresh current inspection view if opened
            _selectedOrderDetail.value?.let { current ->
                if (current.order.id == orderId) {
                    _selectedOrderDetail.value = repository.getOrderWithDetails(orderId)
                }
            }
            showBanner("Order status updated to $newStatus (Customer notified)")
        }
    }

    fun updatePaymentStatus(orderId: String, paymentStatus: String, paymentRef: String) {
        viewModelScope.launch {
            repository.updatePaymentStatus(orderId, paymentStatus, paymentRef)
            _selectedOrderDetail.value?.let { current ->
                if (current.order.id == orderId) {
                    _selectedOrderDetail.value = repository.getOrderWithDetails(orderId)
                }
            }
            showBanner("Payment updated to $paymentStatus")
        }
    }

    // Admin Authentication: Firebase Email/Password + Firestore admin role.
    // Required Firestore document: admins/{uid} with { role: "admin", active: true }.
    fun loginAdmin(
        email: String,
        password: String,
        onSuccess: () -> Unit = {},
        onUnauthorized: () -> Unit = {}
    ) {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank() || password.isBlank()) {
            _adminLoginError.value = "Enter your admin email and password."
            return
        }

        _isAdminLoading.value = true
        _adminLoginError.value = null

        try {
            val auth = FirebaseAuth.getInstance()
            auth.signInWithEmailAndPassword(cleanEmail, password)
                .addOnCompleteListener { authTask ->
                    if (!authTask.isSuccessful) {
                        _isAdminLoading.value = false
                        _isAdminAuthenticated.value = false
                        _adminLoginError.value = authTask.exception?.localizedMessage
                            ?: "Invalid admin email or password."
                        showBanner("Admin login failed")
                        return@addOnCompleteListener
                    }

                    val user = auth.currentUser
                    if (user == null) {
                        _isAdminLoading.value = false
                        _isAdminAuthenticated.value = false
                        _adminLoginError.value = "Firebase authentication failed. Please try again."
                        return@addOnCompleteListener
                    }

                    FirebaseFirestore.getInstance()
                        .collection("admins")
                        .document(user.uid)
                        .get()
                        .addOnSuccessListener { document ->
                            val role = document.getString("role")?.trim()?.lowercase()
                            val active = document.getBoolean("active") ?: false

                            if (role == "admin" && active) {
                                _isAdminAuthenticated.value = true
                                _adminLoginError.value = null
                                _currentRole.value = AppRole.ADMIN
                                _adminTab.value = AdminTab.DASHBOARD
                                _isAdminLoading.value = false
                                showBanner("Admin authenticated successfully")
                                onSuccess()
                            } else {
                                auth.signOut()
                                _isAdminAuthenticated.value = false
                                _isAdminLoading.value = false
                                _adminLoginError.value = "This Firebase account is not authorized as an active admin."
                                showBanner("Unauthorized admin account")
                                onUnauthorized()
                            }
                        }
                        .addOnFailureListener { error ->
                            auth.signOut()
                            _isAdminAuthenticated.value = false
                            _isAdminLoading.value = false
                            _adminLoginError.value = error.localizedMessage
                                ?: "Unable to verify admin role."
                            showBanner("Admin role verification failed")
                        }
                }
                .addOnFailureListener { error ->
                    _isAdminLoading.value = false
                    _isAdminAuthenticated.value = false
                    _adminLoginError.value = error.localizedMessage
                        ?: "Unable to connect to Firebase Authentication."
                }
        } catch (error: Exception) {
            _isAdminLoading.value = false
            _isAdminAuthenticated.value = false
            _adminLoginError.value = "Firebase is not configured. Add google-services.json to the app."
        }
    }

    fun logoutAdmin() {
        runCatching { FirebaseAuth.getInstance().signOut() }
        _isAdminAuthenticated.value = false
        _currentRole.value = AppRole.CUSTOMER
        _customerTab.value = CustomerTab.HOME
        showBanner("Logged out of Admin")
    }

    // Item Management (Admin)
    fun addNewItem(
        categoryId: String,
        name: String,
        description: String,
        price: Double,
        unit: String,
        minQuantity: Int,
        maxQuantity: Int,
        uploadRequired: Boolean,
        iconName: String
    ) {
        viewModelScope.launch {
            repository.addItem(
                categoryId = categoryId,
                name = name,
                description = description,
                price = price,
                unit = unit,
                minQuantity = minQuantity,
                maxQuantity = maxQuantity,
                uploadRequired = uploadRequired,
                iconName = iconName
            )
            showBanner("Service '$name' added successfully")
        }
    }

    fun updateItem(item: ItemEntity) {
        viewModelScope.launch {
            repository.updateItem(item)
            showBanner("Service '${item.name}' updated")
        }
    }

    fun updateItemPrice(itemId: String, newPrice: Double) {
        viewModelScope.launch {
            repository.updateItemPrice(itemId, newPrice)
            showBanner("Price updated to ₹${newPrice.toInt()}")
        }
    }

    fun toggleItemActive(item: ItemEntity) {
        viewModelScope.launch {
            val newStatus = !item.isActive
            repository.setItemActive(item.id, newStatus)
            showBanner("${item.name} is now ${if (newStatus) "Active" else "Disabled"}")
        }
    }

    fun deleteItem(itemId: String, itemName: String) {
        viewModelScope.launch {
            repository.deleteItem(itemId)
            showBanner("Deleted '$itemName'")
        }
    }

    // Category Management (Admin)
    fun addCategory(name: String, icon: String) {
        viewModelScope.launch {
            repository.addCategory(name, icon)
            showBanner("Category '$name' created")
        }
    }

    fun updateCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.updateCategory(category)
            showBanner("Category '${category.name}' updated")
        }
    }

    fun deleteCategory(categoryId: String, categoryName: String) {
        viewModelScope.launch {
            repository.deleteCategory(categoryId)
            showBanner("Category '$categoryName' deleted")
        }
    }

    // Settings Management (Admin)
    fun updateSetting(key: String, value: String) {
        viewModelScope.launch {
            repository.updateSetting(key, value)
            showBanner("Setting updated")
        }
    }

    // Offer Management (Admin & Customer)
    fun openOfferDetail(offer: OfferWithItems) {
        _selectedOfferForDetail.value = offer
    }

    fun closeOfferDetail() {
        _selectedOfferForDetail.value = null
    }

    fun createOffer(
        title: String,
        description: String,
        imageUrl: String,
        offerType: String,
        offerValue: Double,
        startAt: Long,
        expiresAt: Long,
        selectedItemIds: List<String>,
        displayOrder: Int = 0,
        isEnabled: Boolean = true,
        notifyCustomers: Boolean = false
    ) {
        viewModelScope.launch {
            repository.createOffer(
                title = title,
                description = description,
                imageUrl = imageUrl,
                offerType = offerType,
                offerValue = offerValue,
                startAt = startAt,
                expiresAt = expiresAt,
                selectedItemIds = selectedItemIds,
                displayOrder = displayOrder,
                isEnabled = isEnabled,
                notifyCustomers = notifyCustomers
            )
            showBanner("Offer '$title' created successfully")
        }
    }

    fun updateOffer(offer: OfferEntity, selectedItemIds: List<String>) {
        viewModelScope.launch {
            repository.updateOffer(offer, selectedItemIds)
            showBanner("Offer '${offer.title}' updated")
        }
    }

    fun deleteOffer(offerId: String, title: String) {
        viewModelScope.launch {
            repository.deleteOffer(offerId)
            showBanner("Offer '$title' deleted")
        }
    }

    fun toggleOfferEnabled(offer: OfferEntity) {
        viewModelScope.launch {
            val newStatus = !offer.isEnabled
            repository.toggleOfferEnabled(offer.id, newStatus)
            showBanner("Offer '${offer.title}' is now ${if (newStatus) "Enabled" else "Disabled"}")
        }
    }

    fun getItemDiscountedPrice(item: ItemEntity): Double {
        val now = System.currentTimeMillis()
        val activeOffers = activeOffersWithItems.value.filter { it.offer.isEnabled && it.offer.startAt <= now && it.offer.expiresAt >= now }
        var bestPrice = item.price
        for (offerWithItems in activeOffers) {
            if (offerWithItems.items.any { it.id == item.id }) {
                val discounted = offerWithItems.calculateDiscountedPrice(item.price)
                if (discounted < bestPrice) {
                    bestPrice = discounted
                }
            }
        }
        return bestPrice
    }

    fun getActiveOfferForItem(itemId: String): OfferWithItems? {
        val now = System.currentTimeMillis()
        return activeOffersWithItems.value.firstOrNull { offerWithItems ->
            offerWithItems.offer.isEnabled &&
                offerWithItems.offer.startAt <= now &&
                offerWithItems.offer.expiresAt >= now &&
                offerWithItems.items.any { it.id == itemId }
        }
    }

    // Notifications
    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead(role: String) {
        viewModelScope.launch {
            repository.markAllNotificationsRead(role)
            showBanner("All notifications marked as read")
        }
    }

    fun showBanner(message: String) {
        _bannerMessage.value = message
    }

    fun clearBanner() {
        _bannerMessage.value = null
    }

    fun getSettingValue(key: String, defaultVal: String = ""): String {
        return adminSettings.value.firstOrNull { it.key == key }?.value ?: defaultVal
    }
}
