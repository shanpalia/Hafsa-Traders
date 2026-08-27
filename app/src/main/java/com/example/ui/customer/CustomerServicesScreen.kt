package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CategoryEntity
import com.example.data.local.ItemEntity
import com.example.data.local.OfferWithItems
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.ServiceItemCard
import com.example.ui.theme.*

@Composable
fun CustomerServicesScreen(
    categories: List<CategoryEntity>,
    items: List<ItemEntity>,
    searchQuery: String,
    selectedCategoryId: String?,
    cartCount: Int,
    onSearchChange: (String) -> Unit,
    onSelectCategory: (String?) -> Unit,
    onSelectItem: (ItemEntity) -> Unit,
    onOpenCart: () -> Unit,
    getDiscountedPrice: (ItemEntity) -> Double = { it.price },
    getActiveOffer: (String) -> OfferWithItems? = { null }
) {
    Scaffold(
        floatingActionButton = {
            if (cartCount > 0) {
                FloatingActionButton(
                    onClick = onOpenCart,
                    containerColor = BrandPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("services_cart_fab")
                ) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = Color(0xFFE11D48),
                                contentColor = Color.White
                            ) {
                                Text("$cartCount")
                            }
                        }
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "View Cart")
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
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Services & Price List",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = LightTextPrimary
                    )
                    Text(
                        text = "Real-time rates set by Hafsa Traders admin. Choose service and quantity.",
                        style = MaterialTheme.typography.bodySmall,
                        color = LightTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text("Search services (photocopy, passport, binding)...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = BrandPrimary)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = BrandPrimary,
                            unfocusedBorderColor = LightBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("services_search_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CategoryFilterRow(
                        categories = categories,
                        selectedCategoryId = selectedCategoryId,
                        onSelectCategory = onSelectCategory
                    )
                }
            }

            if (items.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = LightTextTertiary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        val isFiltered = searchQuery.isNotBlank() || selectedCategoryId != null
                        Text(
                            text = if (isFiltered) "No services match your search" else "No services available yet",
                            fontWeight = FontWeight.Bold,
                            color = LightTextSecondary
                        )
                        Text(
                            text = if (isFiltered) "Try clearing filters or search term" else "Services will appear once added by shop owner.",
                            style = MaterialTheme.typography.bodySmall,
                            color = LightTextTertiary
                        )
                    }
                }
            } else {
                items(items) { item ->
                    val catName = categories.firstOrNull { it.id == item.categoryId }?.name
                    val discounted = getDiscountedPrice(item)
                    val activeOffer = getActiveOffer(item.id)
                    val badge = activeOffer?.getDiscountLabel()

                    ServiceItemCard(
                        item = item,
                        categoryName = catName,
                        onSelect = { onSelectItem(item) },
                        discountedPrice = if (discounted < item.price) discounted else null,
                        offerBadge = badge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
