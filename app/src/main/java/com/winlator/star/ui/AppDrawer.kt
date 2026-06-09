package com.winlator.star.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.winlator.star.ui.theme.GlowPurple
import com.winlator.star.ui.theme.Primary
import com.winlator.star.ui.theme.PrimaryDim

private val PureBlack = Color(0xFF000000)
private val DarkSurface = Color(0xFF0D0D0D)
private val MutedWhite = Color(0xFF999999)
private val DimWhite = Color(0xFFE8E8E8)

enum class MenuTab { EMULATION, TOOLS, INFORMATION }
enum class StoreTab { LOCAL, STEAM, EPIC, GOG }

private val PurpleGradient = Brush.horizontalGradient(listOf(Color(0xFF8B6BE0), GlowPurple))

private fun iconFor(screen: Screen): ImageVector = when (screen) {
    Screen.Containers    -> Icons.Filled.FolderOpen
    Screen.Games         -> Icons.Filled.OpenInNew
    Screen.InputControls -> Icons.Filled.SportsEsports
    Screen.AdrenoTools   -> Icons.Filled.Memory
    Screen.Saves         -> Icons.Filled.Save
    Screen.FileManager   -> Icons.Filled.FolderOpen
    Screen.Settings      -> Icons.Filled.Settings
    Screen.Appearance    -> Icons.Filled.Settings
    Screen.LsfgSettings  -> Icons.Filled.Settings
    else                 -> Icons.Filled.OpenInNew
}

// ───── Store Pill ─────

@Composable
fun StorePill(selectedTab: StoreTab, onSelect: (StoreTab) -> Unit) {
    val pillHeight = 40.dp
    val storeLabels = listOf("LOCAL", "STEAM", "EPIC", "GOG")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(pillHeight),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                brush = Brush.horizontalGradient(listOf(Color(0xFF8B6BE0), Color(0xFFBB86FC), Color(0xFF8B6BE0))),
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(size.height / 2f),
                style = Stroke(width = 2.dp.toPx()),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .clip(RoundedCornerShape(pillHeight / 2))
                .background(DarkSurface),
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                StoreTab.entries.forEachIndexed { index, tab ->
                    val isSelected = tab == selectedTab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onSelect(tab) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = storeLabels[index],
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            style = if (isSelected) TextStyle(brush = PurpleGradient) else TextStyle(color = MutedWhite),
                        )
                    }
                    if (index < StoreTab.entries.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .padding(vertical = 10.dp)
                                .background(Color(0xFF333333)),
                        )
                    }
                }
            }
        }
    }
}

// ───── Full-Screen Menu ─────

@Composable
fun MenuScreen(
    currentRoute: String,
    onNavigate: (Screen) -> Unit,
    onLaunchStore: (Screen) -> Unit,
    onAbout: () -> Unit,
    onClose: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(MenuTab.EMULATION) }
    var showHelp by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showHelp) {
        HelpSupportDialog(
            onDismiss = { showHelp = false },
            onOpenUrl = { url ->
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
    ) {
        // ── LEFT: Tab bar (centered vertically, XServerDrawer style) ──
        Column(
            modifier = Modifier
                .width(64.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1f))

            MenuTabIconButton(
                icon = Icons.Filled.FolderOpen,
                isSelected = selectedTab == MenuTab.EMULATION,
                onClick = { selectedTab = MenuTab.EMULATION },
            )
            Spacer(Modifier.height(6.dp))
            MenuTabIconButton(
                icon = Icons.Filled.Settings,
                isSelected = selectedTab == MenuTab.TOOLS,
                onClick = { selectedTab = MenuTab.TOOLS },
            )
            Spacer(Modifier.height(6.dp))
            MenuTabIconButton(
                icon = Icons.Filled.Info,
                isSelected = selectedTab == MenuTab.INFORMATION,
                onClick = { selectedTab = MenuTab.INFORMATION },
            )

            Spacer(Modifier.weight(1f))
        }

        // ── RIGHT: Content area ──
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(top = 12.dp, end = 16.dp, bottom = 12.dp),
        ) {
            // Section header row with close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = when (selectedTab) {
                        MenuTab.EMULATION -> "EMULATION"
                        MenuTab.TOOLS -> "TOOLS"
                        MenuTab.INFORMATION -> "INFORMATION"
                    },
                    color = MutedWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close menu",
                        tint = DimWhite,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tab content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                when (selectedTab) {
                    MenuTab.EMULATION -> {
                        MenuCardItem(Screen.Containers, currentRoute, onNavigate)
                        MenuCardItem(Screen.Settings, currentRoute, onNavigate)
                    }
                    MenuTab.TOOLS -> {
                        MenuCardItem(Screen.InputControls, currentRoute, onNavigate)
                        MenuCardItem(Screen.AdrenoTools, currentRoute, onNavigate)
                        MenuCardItem(Screen.Saves, currentRoute, onNavigate)
                        MenuCardItem(Screen.LsfgSettings, currentRoute, onNavigate)
                        MenuCardItem(Screen.FileManager, currentRoute, onNavigate)
                    }
                    MenuTab.INFORMATION -> {
                        MenuIconItem(label = "About", icon = Icons.Filled.Info, onClick = onAbout)
                        MenuIconItem(label = "Help and Support", icon = Icons.Filled.HelpOutline, onClick = { showHelp = true })
                    }
                }
            }
        }
    }
}

// ───── Tab Icon Button (XServerDrawer style) ─────

@Composable
private fun MenuTabIconButton(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    val bgBrush = if (isSelected)
        Brush.verticalGradient(listOf(PrimaryDim, Primary.copy(alpha = 0.3f)))
    else
        Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))

    val borderColor = if (isSelected) GlowPurple.copy(alpha = 0.6f) else Color(0xFF333333)
    val tintColor = if (isSelected) Color.White else MutedWhite

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgBrush, RoundedCornerShape(12.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Canvas(Modifier.size(44.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(GlowPurple.copy(alpha = 0.25f), Color.Transparent),
                        radius = size.minDimension / 2f
                    ),
                    radius = size.minDimension / 2f
                )
            }
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(22.dp),
        )
    }
}

// ───── Menu Items ─────

@Composable
private fun MenuCardItem(screen: Screen, currentRoute: String, onNavigate: (Screen) -> Unit) {
    val selected = currentRoute == screen.route
    val bgBrush = if (selected)
        Brush.horizontalGradient(listOf(PrimaryDim, Primary.copy(alpha = 0.15f)))
    else
        Brush.horizontalGradient(listOf(DarkSurface, DarkSurface))
    val borderColor = if (selected) GlowPurple.copy(alpha = 0.5f) else Color(0xFF222222)
    val tintColor = if (selected) Color.White else MutedWhite

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgBrush, RoundedCornerShape(12.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onNavigate(screen) }
            .padding(horizontal = 14.dp, vertical = 13.dp),
    ) {
        Icon(
            imageVector = iconFor(screen),
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = screen.label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = tintColor,
        )
    }
}

@Composable
private fun MenuIconItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface, RoundedCornerShape(12.dp))
            .border(1.5.dp, Color(0xFF222222), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MutedWhite,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = DimWhite,
        )
    }
}

// ───── Help / Support ─────

@Composable
private fun HelpSupportDialog(onDismiss: () -> Unit, onOpenUrl: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        title = { Text("Help & Support", color = DimWhite) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "For bug reports, feature requests, and support, visit the GitHub repository.",
                    color = DimWhite
                )
                SupportLink(
                    label = "GitHub Repository",
                    url = "https://github.com/The412Banner/star-compose",
                    onOpenUrl = onOpenUrl
                )
                SupportLink(
                    label = "Report an Issue",
                    url = "https://github.com/The412Banner/star-compose/issues",
                    onOpenUrl = onOpenUrl
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
private fun SupportLink(label: String, url: String, onOpenUrl: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenUrl(url) }
            .padding(vertical = 4.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.OpenInNew,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            color = Primary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
