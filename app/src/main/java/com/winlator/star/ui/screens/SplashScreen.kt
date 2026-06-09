package com.winlator.star.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.winlator.star.R

private val PurpleGradient = Brush.horizontalGradient(
    listOf(Color(0xFF8B6BE0), Color(0xFFBB86FC), Color(0xFFD0BBFF))
)

@Composable
fun SplashScreen(
    progress: Int,
    showProceed: Boolean = false,
    onProceed: () -> Unit = {},
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")

    val displayedProgress by animateIntAsState(
        targetValue = progress,
        animationSpec = tween(300),
        label = "counter",
    )

    val shimmerPos by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D)),
    ) {
        // Star logo at top right
        Image(
            painter = painterResource(R.mipmap.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 32.dp)
                .size(72.dp),
        )

        // Center content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 48.dp),
        ) {
            Text(
                text = "Installing the required files,\nplease wait!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                style = TextStyle(brush = PurpleGradient),
                lineHeight = 30.sp,
            )

            Spacer(Modifier.height(36.dp))

            // Progress track
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
            ) {
                val barW = size.width
                val barH = size.height
                val fillW = (barW * (displayedProgress / 100f)).coerceIn(0f, barW)
                val radius = barH / 2f

                // Track
                drawRoundRect(
                    color = Color(0xFF333333),
                    size = Size(barW, barH),
                    cornerRadius = CornerRadius(radius),
                )

                if (fillW > 0f) {
                    // Glow layers
                    listOf(6f to 0.12f, 4f to 0.18f, 2f to 0.28f).forEach { (expand, alpha) ->
                        drawRoundRect(
                            color = Color(0xFF8B6BE0).copy(alpha = alpha),
                            topLeft = Offset(-expand / 2f, -expand / 2f),
                            size = Size(fillW + expand, barH + expand),
                            cornerRadius = CornerRadius(radius + expand / 2f),
                        )
                    }

                    // Fill
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF6A4FC4), Color(0xFF8B6BE0), Color(0xFFB49BF5)),
                            endX = fillW,
                        ),
                        size = Size(fillW, barH),
                        cornerRadius = CornerRadius(radius),
                    )

                    // Shimmer
                    val shimX = shimmerPos * fillW
                    val shimHalf = barH * 4f
                    clipRect(right = fillW) {
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.5f),
                                    Color.Transparent,
                                ),
                                startX = shimX - shimHalf,
                                endX = shimX + shimHalf,
                            ),
                            size = Size(fillW, barH),
                            cornerRadius = CornerRadius(radius),
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "$displayedProgress%",
                fontSize = 13.sp,
                color = Color(0xFFAAAAAA),
                textAlign = TextAlign.Center,
            )

            AnimatedVisibility(
                visible = showProceed,
                enter = fadeIn(tween(400)) + scaleIn(tween(400), initialScale = 0.92f),
            ) {
                Column {
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = onProceed,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B6BE0)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Continue", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
