package com.example.data.update

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class WebsiteUpdateInfo(
    val version: String,
    val updateUrl: String,
    val required: Boolean,
    val message: String
)

sealed class WebsiteUpdateResult {
    data class UpdateAvailable(val info: WebsiteUpdateInfo) : WebsiteUpdateResult()
    data object UpToDate : WebsiteUpdateResult()
    data class Error(val message: String) : WebsiteUpdateResult()
}

object WebsiteUpdateChecker {
    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun check(endpoint: String, currentVersion: String): WebsiteUpdateResult = withContext(Dispatchers.IO) {
        if (endpoint.isBlank()) return@withContext WebsiteUpdateResult.Error("Update check URL is not configured by the store yet.")
        try {
            val request = Request.Builder().url(endpoint).get().build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext WebsiteUpdateResult.Error("Could not check for updates (HTTP ${response.code}).")
                val body = response.body?.string().orEmpty()
                val json = JSONObject(body)
                val latest = json.optString("version").trim()
                val url = json.optString("updateUrl", json.optString("apkUrl")).trim()
                val required = json.optBoolean("required", json.optBoolean("force", false))
                val message = json.optString("message", "A new version of Hafsa Traders is available.")
                if (latest.isBlank()) return@withContext WebsiteUpdateResult.Error("The update file does not contain a version number.")
                if (isVersionNewer(latest, currentVersion)) {
                    if (url.isBlank()) return@withContext WebsiteUpdateResult.Error("A newer version was found, but no update link is configured.")
                    WebsiteUpdateResult.UpdateAvailable(WebsiteUpdateInfo(latest, url, required, message))
                } else WebsiteUpdateResult.UpToDate
            }
        } catch (e: Exception) {
            WebsiteUpdateResult.Error(e.message ?: "Unable to check for updates. Please check your internet connection.")
        }
    }

    private fun isVersionNewer(latest: String, current: String): Boolean {
        fun parts(value: String) = value.substringBefore("-").split(".").map { it.toIntOrNull() ?: 0 }
        val a = parts(latest); val b = parts(current)
        for (i in 0 until maxOf(a.size, b.size)) {
            val x = a.getOrElse(i) { 0 }; val y = b.getOrElse(i) { 0 }
            if (x != y) return x > y
        }
        return false
    }
}
