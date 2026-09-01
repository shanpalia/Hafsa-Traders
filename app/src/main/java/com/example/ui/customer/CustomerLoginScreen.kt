package com.example.ui.customer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryContainer
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightTextPrimary
import com.example.ui.theme.LightTextSecondary
import androidx.compose.ui.platform.LocalContext
import com.example.data.remote.SupabaseAuthManager
import kotlinx.coroutines.launch

@Composable
fun CustomerLoginScreen(
    onLoginSuccess: (String, String) -> Unit,
    onBack: () -> Unit = {}
) {
    var isRegister by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val authManager = remember(context) { SupabaseAuthManager(context.applicationContext) }
    val scope = rememberCoroutineScope()

    BackHandler { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .background(BrandPrimaryContainer, RoundedCornerShape(50.dp))
                        .padding(18.dp)
                ) {
                    Icon(
                        imageVector = if (isRegister) Icons.Default.Person else Icons.Default.Lock,
                        contentDescription = null,
                        tint = BrandPrimary
                    )
                }
                Text(
                    text = "HAFSA TRADERS",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = BrandPrimary
                )
                Text(
                    text = if (isRegister) "CREATE CUSTOMER ACCOUNT" else "CUSTOMER LOGIN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = LightTextPrimary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Secure Supabase login is required to place and track your orders.",
                    style = MaterialTheme.typography.bodySmall,
                    color = LightTextSecondary
                )
                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (isRegister) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                error?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, color = Color(0xFFD32F2F), style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = {
                        val cleanEmail = email.trim().lowercase()
                        error = when {
                            cleanEmail.isBlank() -> "Enter your email address."
                            !cleanEmail.contains("@") -> "Enter a valid email address."
                            password.length < 6 -> "Password must be at least 6 characters."
                            isRegister && password != confirmPassword -> "Passwords do not match."
                            else -> null
                        }
                        if (error != null) return@Button

                        loading = true
                        scope.launch {
                            try {
                                val session = if (isRegister) {
                                    authManager.signUp(cleanEmail, password)
                                } else {
                                    authManager.signIn(cleanEmail, password)
                                }
                                loading = false
                                error = null
                                onLoginSuccess(session.userId, session.email)
                            } catch (e: Exception) {
                                loading = false
                                error = e.message ?: "Supabase authentication failed. Please try again."
                            }
                        }
                    },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text(if (loading) "PLEASE WAIT..." else if (isRegister) "CREATE ACCOUNT" else "LOGIN", fontWeight = FontWeight.Bold)
                }

                TextButton(onClick = {
                    isRegister = !isRegister
                    error = null
                }) {
                    Text(if (isRegister) "Already have an account? Login" else "New customer? Create account")
                }
            }
        }
    }
}
