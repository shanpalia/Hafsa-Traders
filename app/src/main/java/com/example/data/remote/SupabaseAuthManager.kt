package com.example.data.remote

import android.content.Context
import com.hafsatraders.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Lightweight Supabase Auth client using Supabase's official REST endpoints.
 * Credentials are supplied at build time through gradle.properties / CI secrets.
 */
data class SupabaseUserSession(
    val userId: String,
    val email: String,
    val accessToken: String,
    val refreshToken: String = ""
)

class SupabaseAuthManager(context: Context) {
    private val prefs = context.getSharedPreferences("supabase_session", Context.MODE_PRIVATE)

    fun isConfigured(): Boolean = BuildConfig.SUPABASE_URL.isNotBlank() && BuildConfig.SUPABASE_ANON_KEY.isNotBlank()

    fun currentSession(): SupabaseUserSession? {
        val userId = prefs.getString("user_id", "").orEmpty()
        val email = prefs.getString("email", "").orEmpty()
        val accessToken = prefs.getString("access_token", "").orEmpty()
        val refreshToken = prefs.getString("refresh_token", "").orEmpty()
        return if (userId.isNotBlank() && accessToken.isNotBlank()) SupabaseUserSession(userId, email, accessToken, refreshToken) else null
    }

    suspend fun signIn(email: String, password: String): SupabaseUserSession = withContext(Dispatchers.IO) {
        requestAuth("/auth/v1/token?grant_type=password", JSONObject().apply {
            put("email", email)
            put("password", password)
        })
    }

    suspend fun signUp(email: String, password: String): SupabaseUserSession = withContext(Dispatchers.IO) {
        requestAuth("/auth/v1/signup", JSONObject().apply {
            put("email", email)
            put("password", password)
        })
    }

    /** Returns the authenticated user's Hafsa Traders role from Supabase. */
    suspend fun currentUserRole(session: SupabaseUserSession = currentSession() ?: throw IllegalStateException("Not signed in")): String = withContext(Dispatchers.IO) {
        check(isConfigured()) { "Supabase is not configured. Add SUPABASE_URL and SUPABASE_ANON_KEY." }
        val encodedId = java.net.URLEncoder.encode(session.userId, "UTF-8")
        val url = URL(baseUrl() + "/rest/v1/hafsa_profiles?select=role&id=eq." + encodedId)
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 20_000
            readTimeout = 20_000
            setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY)
            setRequestProperty("Authorization", "Bearer ${session.accessToken}")
            setRequestProperty("Accept", "application/json")
        }
        val code = connection.responseCode
        val text = (if (code in 200..299) connection.inputStream else connection.errorStream)?.bufferedReader()?.use { it.readText() }.orEmpty()
        if (code !in 200..299) throw IllegalStateException("Could not verify account role ($code)")
        val rows = org.json.JSONArray(text)
        if (rows.length() == 0) throw IllegalStateException("Your Hafsa Traders profile was not found.")
        rows.getJSONObject(0).optString("role").trim().lowercase().ifBlank { "customer" }
    }

    private fun requestAuth(path: String, body: JSONObject): SupabaseUserSession {
        check(isConfigured()) { "Supabase is not configured. Add SUPABASE_URL and SUPABASE_ANON_KEY." }
        val connection = (URL(baseUrl() + path).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = 20_000
            readTimeout = 20_000
            setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY)
            setRequestProperty("Content-Type", "application/json")
        }
        connection.outputStream.use { it.write(body.toString().toByteArray()) }
        val code = connection.responseCode
        val text = (if (code in 200..299) connection.inputStream else connection.errorStream)?.bufferedReader()?.use { it.readText() }.orEmpty()
        if (code !in 200..299) {
            val message = runCatching { JSONObject(text).optString("msg").ifBlank { JSONObject(text).optString("message") } }.getOrDefault("")
            throw IllegalStateException(message.ifBlank { "Supabase authentication failed ($code)" })
        }
        val json = JSONObject(text)
        val user = json.optJSONObject("user") ?: throw IllegalStateException("Account was created. Please confirm your email, then login.")
        val token = json.optString("access_token")
        if (token.isBlank()) throw IllegalStateException("Account created. Please confirm your email, then login.")
        val session = SupabaseUserSession(
            userId = user.optString("id"),
            email = user.optString("email"),
            accessToken = token,
            refreshToken = json.optString("refresh_token")
        )
        if (session.userId.isBlank()) throw IllegalStateException("Supabase did not return a user ID.")
        save(session)
        return session
    }

    private fun save(session: SupabaseUserSession) {
        prefs.edit()
            .putString("user_id", session.userId)
            .putString("email", session.email)
            .putString("access_token", session.accessToken)
            .putString("refresh_token", session.refreshToken)
            .apply()
    }

    fun signOut() { prefs.edit().clear().apply() }

    private fun baseUrl(): String = BuildConfig.SUPABASE_URL.trim().removeSuffix("/")
}
