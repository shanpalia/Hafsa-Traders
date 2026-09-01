package com.example.data.remote

import android.content.Context
import com.example.data.local.CategoryEntity
import com.example.data.local.HafsaDao
import com.example.data.local.ItemEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Optional real-time service catalogue sync through Firestore. */
class FirebaseCatalogSync(
    private val context: Context,
    private val dao: HafsaDao,
    private val scope: CoroutineScope
) {
    private var registration: ListenerRegistration? = null

    private fun firestoreOrNull(): FirebaseFirestore? = try {
        if (FirebaseApp.getApps(context).isEmpty()) null else FirebaseFirestore.getInstance()
    } catch (_: Throwable) { null }

    fun startListening() {
        if (registration != null) return
        val firestore = firestoreOrNull() ?: return
        registration = firestore.collection("hafsa_live").document("catalog")
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                val categories = snapshot.get("categories") as? List<*> ?: emptyList<Any>()
                val items = snapshot.get("items") as? List<*> ?: emptyList<Any>()
                scope.launch(Dispatchers.IO) {
                    categories.mapNotNull { it as? Map<*, *> }.mapNotNull { map ->
                        val id = map.string("id") ?: return@mapNotNull null
                        val name = map.string("name") ?: return@mapNotNull null
                        CategoryEntity(id, name, map.string("icon") ?: "print", map.bool("isActive", true), map.int("displayOrder", 0))
                    }.forEach { dao.insertCategory(it) }

                    items.mapNotNull { it as? Map<*, *> }.mapNotNull { map ->
                        val id = map.string("id") ?: return@mapNotNull null
                        val categoryId = map.string("categoryId") ?: return@mapNotNull null
                        val name = map.string("name") ?: return@mapNotNull null
                        ItemEntity(
                            id = id, categoryId = categoryId, name = name,
                            description = map.string("description") ?: "",
                            price = map.double("price", 0.0), unit = map.string("unit") ?: "Per Item",
                            minQuantity = map.int("minQuantity", 1), maxQuantity = map.int("maxQuantity", 500),
                            uploadRequired = map.bool("uploadRequired", false), isActive = map.bool("isActive", true),
                            iconName = map.string("iconName") ?: "document",
                            createdAt = map.long("createdAt", System.currentTimeMillis())
                        )
                    }.forEach { dao.insertItem(it) }
                }
            }
    }

    fun publishFromLocal() {
        val firestore = firestoreOrNull() ?: return
        scope.launch(Dispatchers.IO) {
            val categories = dao.getAllCategories().first()
            val items = dao.getAllItems().first()
            val payload = hashMapOf<String, Any>(
                "updatedAt" to System.currentTimeMillis(),
                "categories" to categories.map { it.toMap() },
                "items" to items.map { it.toMap() }
            )
            firestore.collection("hafsa_live").document("catalog").set(payload)
        }
    }

    fun stop() { registration?.remove(); registration = null }

    private fun CategoryEntity.toMap(): Map<String, Any> = mapOf(
        "id" to id, "name" to name, "icon" to icon, "isActive" to isActive, "displayOrder" to displayOrder
    )
    private fun ItemEntity.toMap(): Map<String, Any> = mapOf(
        "id" to id, "categoryId" to categoryId, "name" to name, "description" to description,
        "price" to price, "unit" to unit, "minQuantity" to minQuantity, "maxQuantity" to maxQuantity,
        "uploadRequired" to uploadRequired, "isActive" to isActive, "iconName" to iconName, "createdAt" to createdAt
    )
    private fun Map<*, *>.string(key: String): String? = this[key] as? String
    private fun Map<*, *>.bool(key: String, fallback: Boolean): Boolean = this[key] as? Boolean ?: fallback
    private fun Map<*, *>.int(key: String, fallback: Int): Int = (this[key] as? Number)?.toInt() ?: fallback
    private fun Map<*, *>.long(key: String, fallback: Long): Long = (this[key] as? Number)?.toLong() ?: fallback
    private fun Map<*, *>.double(key: String, fallback: Double): Double = (this[key] as? Number)?.toDouble() ?: fallback
}
