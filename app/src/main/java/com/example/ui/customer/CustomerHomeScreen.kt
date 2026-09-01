package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CategoryEntity
import com.example.data.local.ItemEntity
import com.example.data.local.OfferWithItems
import com.example.data.local.OrderEntity
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.*

@Composable
fun CustomerHomeScreen(
    categories: List<CategoryEntity>,
    popularItems: List<ItemEntity>,
    activeOrders: List<OrderEntity>,
    activeOffers: List<OfferWithItems> = emptyList(),
    searchQuery: String,
    selectedCategoryId: String?,
    onSearchChange: (String) -> Unit,
    onSelectCategory: (String?) -> Unit,
    onSelectItem: (ItemEntity) -> Unit,
    onSelectOffer: (OfferWithItems) -> Unit = {},
    getDiscountedPrice: (ItemEntity) -> Double = { it.price },
    getActiveOffer: (String) -> OfferWithItems? = { null },
    onViewOrder: (String) -> Unit,
    onViewAllServices: () -> Unit,
    onStartOrder: () -> Unit,
    shopAddress: String
) {
    // Homepage intentionally shows only service categories. Service prices/options open after a category is selected.
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().background(LightBackground),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = BrandPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Text("HAFSA TRADERS", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Online Print & Document Services", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 29.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Choose a category, upload your file or photo, place your order and collect it from our shop.", color = Color.White.copy(alpha = .9f))
                    Spacer(Modifier.height(14.dp))
                    Surface(color = Color.White.copy(alpha = .16f), shape = RoundedCornerShape(12.dp)) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = Color.White)
                            Spacer(Modifier.width(7.dp))
                            Text("SHOP PICKUP ONLY • NO HOME DELIVERY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
            Text("What do you want to order?", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = LightTextPrimary)
        }
        items(categories.filter { it.isActive }, key = { it.id }) { category ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().height(150.dp).clickable {
                    onSelectCategory(category.id)
                    onViewAllServices()
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(color = BrandPrimaryContainer, shape = RoundedCornerShape(16.dp)) {
                        Icon(getCategoryIcon(category.icon), null, tint = BrandPrimary, modifier = Modifier.padding(12.dp).size(30.dp))
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(category.name, fontWeight = FontWeight.Bold, color = LightTextPrimary, textAlign = TextAlign.Center)
                    Text("Tap to choose service", fontSize = 11.sp, color = LightTextSecondary, textAlign = TextAlign.Center)
                }
            }
        }
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.UploadFile, null, tint = BrandPrimary)
                        Spacer(Modifier.width(10.dp))
                        Text("How it works", fontWeight = FontWeight.Bold, color = LightTextPrimary)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("1. Choose category  •  2. Select service  •  3. Upload photo/PDF if needed  •  4. Pay or choose counter payment  •  5. Pick up from Hafsa Traders", color = LightTextSecondary, lineHeight = 20.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Pickup address: $shopAddress", color = BrandPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
            }
        }
    }
}
