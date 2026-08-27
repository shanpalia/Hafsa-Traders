package com.example.ui.customer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

private data class SplashServiceItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun SplashScreen(
    onContinue: () -> Unit
) {
    val scaleAnim = remember { Animatable(0.88f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
        // 2 seconds display time before smooth transition
        delay(1400)
        onContinue()
    }

    val services = remember {
        listOf(
            SplashServiceItem("PHOTOCOPY", Icons.Default.ContentCopy),
            SplashServiceItem("PRINT OUT", Icons.Default.Print),
            SplashServiceItem("PASSPORT PHOTO", Icons.Default.CameraAlt),
            SplashServiceItem("STATIONERY", Icons.Default.Create),
            SplashServiceItem("SPIRAL BINDING", Icons.Default.AutoStories),
            SplashServiceItem("LAMINATION", Icons.Default.Layers)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("splash_screen_container")
    ) {
        // Decorative subtle light-blue background curves/waves
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Top-Right decorative soft wave
            val topPath = Path().apply {
                moveTo(w * 0.4f, 0f)
                cubicTo(
                    w * 0.7f, h * 0.08f,
                    w * 0.85f, h * 0.03f,
                    w, h * 0.18f
                )
                lineTo(w, 0f)
                close()
            }
            drawPath(
                path = topPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFE0F2FE).copy(alpha = 0.7f), Color(0xFFF0F9FF).copy(alpha = 0.3f)),
                    start = Offset(w * 0.5f, 0f),
                    end = Offset(w, h * 0.2f)
                )
            )

            // Bottom-Left decorative soft wave
            val bottomPath = Path().apply {
                moveTo(0f, h * 0.82f)
                cubicTo(
                    w * 0.15f, h * 0.95f,
                    w * 0.4f, h * 0.92f,
                    w * 0.65f, h
                )
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = bottomPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFE0F2FE).copy(alpha = 0.8f), Color(0xFFF0F9FF).copy(alpha = 0.2f)),
                    start = Offset(0f, h * 0.8f),
                    end = Offset(w * 0.6f, h)
                )
            )

            // Subtle circular decorative orbits
            drawCircle(
                color = Color(0xFF0284C7).copy(alpha = 0.04f),
                radius = w * 0.42f,
                center = Offset(w / 2f, h * 0.46f),
                style = Stroke(width = 2f)
            )
            drawCircle(
                color = Color(0xFF0284C7).copy(alpha = 0.08f),
                radius = w * 0.48f,
                center = Offset(w / 2f, h * 0.46f),
                style = Stroke(width = 1.5f)
            )
        }

        // Main Scaled Content
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
        ) {
            val screenWidth = maxWidth
            val screenHeight = maxHeight
            val radius = (screenWidth.value * 0.36f).coerceIn(110f, 150f).dp

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Spacer for vertical balance
                Spacer(modifier = Modifier.height(16.dp))

                // Central Circular Orbit Cluster
                Box(
                    modifier = Modifier
                        .size(radius * 2 + 100.dp)
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    // 1. Central Core Hafsa Traders Branding Badge
                    Surface(
                        modifier = Modifier
                            .size(150.dp)
                            .shadow(elevation = 8.dp, shape = CircleShape, ambientColor = Color(0x330284C7), spotColor = Color(0x330284C7)),
                        shape = CircleShape,
                        color = Color.White
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(width = 2.5.dp, color = Color(0xFF0284C7), shape = CircleShape)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Subtle top mini icon
                                Icon(
                                    imageVector = Icons.Default.Print,
                                    contentDescription = null,
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "HAFSA",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 21.sp,
                                        letterSpacing = 1.8.sp,
                                        color = Color(0xFF0284C7)
                                    ),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "TRADERS",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.sp,
                                        letterSpacing = 2.2.sp,
                                        color = Color(0xFF0F172A)
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // 2. 6 Surrounding Circular Service Badges in balanced radial positions
                    services.forEachIndexed { index, service ->
                        // Position around the circle (start at top: -90 degrees)
                        val angleDeg = -90.0 + (index * 60.0)
                        val angleRad = Math.toRadians(angleDeg)
                        val offsetX = (radius.value * cos(angleRad)).dp
                        val offsetY = (radius.value * sin(angleRad)).dp

                        ServiceCircularBadge(
                            service = service,
                            modifier = Modifier.offset(x = offsetX, y = offsetY)
                        )
                    }
                }

                // Bottom Developer Attribution Footer
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    Text(
                        text = "DEVELOPER BY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 2.sp,
                            color = Color(0xFF64748B)
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "SHANPALIA",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            letterSpacing = 2.5.sp,
                            color = Color(0xFF0284C7)
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceCircularBadge(
    service: SplashServiceItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier
                .size(54.dp)
                .shadow(elevation = 4.dp, shape = CircleShape, ambientColor = Color(0x220284C7), spotColor = Color(0x220284C7)),
            shape = CircleShape,
            color = Color.White
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(width = 1.5.dp, color = Color(0xFF0284C7), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = service.icon,
                    contentDescription = service.title,
                    tint = Color(0xFF0284C7),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = service.title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 8.5.sp,
                letterSpacing = 0.4.sp,
                color = Color(0xFF0F172A)
            ),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

