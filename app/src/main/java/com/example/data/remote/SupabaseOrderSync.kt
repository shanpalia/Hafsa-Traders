package com.example.data.remote

import android.content.Context
import com.example.data.local.HafsaDao
import com.example.data.local.OrderEntity
import com.example.data.local.OrderItemEntity
import com.example.data.local.OrderStatusHistoryEntity
import com.example.data.repository.OrderWithDetails
import com.hafsatraders.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

/** Remote order sync for Supabase PostgREST. Room remains the offline cache/UI store. */
class SupabaseOrderSync(
    context: Context,
    private val dao: HafsaDao,
    private val auth: SupabaseAuthManager
) {
    private val appContext = context.applicationContext
    fun isConfigured() = auth.isConfigured()

    suspend fun pushOrder(details: OrderWithDetails) = withContext(Dispatchers.IO) {
        val token = requireToken()
        post("orders", JSONObject().apply {
            val o = details.order
            put("id", o.id); put("order_number", o.orderNumber); put("user_id", o.userId)
            put("customer_name", o.customerName); put("customer_phone", o.customerPhone)
            put("customer_email", o.customerEmail); put("customer_address", o.customerAddress)
            put("total_amount", o.totalAmount); put("subtotal", o.subtotal); put("discount", o.discount)
            put("payment_status", o.paymentStatus); put("payment_method", o.paymentMethod); put("payment_ref", o.paymentRef)
            put("order_status", o.orderStatus); put("special_instructions", o.specialInstructions)
            put("created_at", o.createdAt); put("updated_at", o.updatedAt)
        }, token)
        details.items.forEach { item ->
            post("order_items", JSONObject().apply {
                put("id", item.id); put("order_id", item.orderId); put("item_id", item.itemId)
                put("item_name", item.itemName); put("unit_price", item.unitPrice); put("unit", item.unit)
                put("quantity", item.quantity); put("subtotal", item.subtotal)
            }, token)
        }
        details.statusHistory.forEach { h ->
            post("order_status_history", JSONObject().apply {
                put("id", h.id); put("order_id", h.orderId); put("status", h.status)
                put("message", h.message); put("changed_at", h.changedAt)
            }, token)
        }
    }

    suspend fun pushOrderStatus(order: OrderEntity, history: OrderStatusHistoryEntity) = withContext(Dispatchers.IO) {
        val token = requireToken()
        patch("orders?id=eq.${enc(order.id)}", JSONObject().apply {
            put("order_status", order.orderStatus); put("updated_at", order.updatedAt)
        }, token)
        post("order_status_history", JSONObject().apply {
            put("id", history.id); put("order_id", history.orderId); put("status", history.status)
            put("message", history.message); put("changed_at", history.changedAt)
        }, token)
    }

    suspend fun pullOrdersForUser(userId: String) = withContext(Dispatchers.IO) {
        val token = requireToken()
        val orders = getArray("orders?user_id=eq.${enc(userId)}&order=created_at.desc", token)
        syncOrders(orders, token)
    }

    suspend fun pullAllOrders() = withContext(Dispatchers.IO) {
        val token = requireToken()
        syncOrders(getArray("orders?order=created_at.desc", token), token)
    }

    private fun syncOrders(orders: JSONArray, token: String) {
        for (i in 0 until orders.length()) {
            val j = orders.getJSONObject(i)
            val order = OrderEntity(
                id = j.str("id"), orderNumber = j.str("order_number"), userId = j.str("user_id"),
                customerName = j.str("customer_name"), customerPhone = j.str("customer_phone"),
                customerEmail = j.str("customer_email"), customerAddress = j.str("customer_address"),
                totalAmount = j.num("total_amount"), subtotal = j.num("subtotal"), discount = j.num("discount"),
                paymentStatus = j.str("payment_status"), paymentMethod = j.str("payment_method"), paymentRef = j.str("payment_ref"),
                orderStatus = j.str("order_status"), specialInstructions = j.str("special_instructions"),
                createdAt = j.long("created_at"), updatedAt = j.long("updated_at")
            )
            dao.insertOrder(order)
            val items = getArray("order_items?order_id=eq.${enc(order.id)}", token)
            val itemList = mutableListOf<OrderItemEntity>()
            for (k in 0 until items.length()) { val x = items.getJSONObject(k); itemList += OrderItemEntity(x.str("id"), x.str("order_id"), x.str("item_id"), x.str("item_name"), x.num("unit_price"), x.str("unit"), x.optInt("quantity",1), x.num("subtotal")) }
            if (itemList.isNotEmpty()) dao.insertOrderItems(itemList)
            val history = getArray("order_status_history?order_id=eq.${enc(order.id)}&order=changed_at.asc", token)
            for (k in 0 until history.length()) { val x = history.getJSONObject(k); dao.insertOrderStatusHistory(OrderStatusHistoryEntity(x.str("id"), x.str("order_id"), x.str("status"), x.str("message"), x.long("changed_at"))) }
        }
    }

    private fun requireToken(): String {
        check(isConfigured()) { "Supabase is not configured. Add SUPABASE_URL and SUPABASE_ANON_KEY." }
        return auth.currentSession()?.accessToken ?: throw IllegalStateException("Please login with Supabase first.")
    }
    private fun base() = BuildConfig.SUPABASE_URL.trim().removeSuffix("/") + "/rest/v1/"
    private fun enc(v: String) = URLEncoder.encode(v, "UTF-8")
    private fun post(table: String, body: JSONObject, token: String) = request("POST", table, body, token, "resolution=merge-duplicates,return=minimal")
    private fun patch(path: String, body: JSONObject, token: String) = request("PATCH", path, body, token, "return=minimal")
    private fun getArray(path: String, token: String): JSONArray {
        val c = (URL(base()+path).openConnection() as HttpURLConnection).apply { requestMethod="GET"; setRequestProperty("apikey",BuildConfig.SUPABASE_ANON_KEY); setRequestProperty("Authorization","Bearer $token") }
        val code=c.responseCode; val text=(if(code in 200..299)c.inputStream else c.errorStream)?.bufferedReader()?.use{it.readText()}.orEmpty(); if(code !in 200..299) throw IllegalStateException("Supabase order sync failed: $text"); return JSONArray(text)
    }
    private fun request(method:String,path:String,body:JSONObject,token:String,prefer:String) {
        val c=(URL(base()+path).openConnection() as HttpURLConnection).apply { requestMethod=method; doOutput=true; setRequestProperty("apikey",BuildConfig.SUPABASE_ANON_KEY); setRequestProperty("Authorization","Bearer $token"); setRequestProperty("Content-Type","application/json"); setRequestProperty("Prefer",prefer) }
        c.outputStream.use{it.write(body.toString().toByteArray())}; val code=c.responseCode; val text=(if(code in 200..299)c.inputStream else c.errorStream)?.bufferedReader()?.use{it.readText()}.orEmpty(); if(code !in 200..299) throw IllegalStateException("Supabase order sync failed: $text")
    }
    private fun JSONObject.str(k:String)=if(isNull(k))"" else optString(k,"")
    private fun JSONObject.num(k:String)=optDouble(k,0.0)
    private fun JSONObject.long(k:String)=optLong(k,0L)
}
