package com.winlator.star.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.winlator.star.R
import com.winlator.star.ui.theme.GlowPurple
import com.winlator.star.ui.theme.Primary
import com.winlator.star.ui.theme.PrimaryDim

private val PureBlack = Color(0xFF000000)
private val DarkSurface = Color(0xFF0D0D0D)
private val MutedWhite = Color(0xFF999999)
private val DimWhite = Color(0xFFE8E8E8)

private fun iconFor(screen: Screen): ImageVector = when (screen) {
    Screen.Containers    -> Icons.Filled.FolderOpen
    Screen.Games         -> Icons.Filled.OpenInNew
    Screen.InputControls -> Icons.Filled.SportsEsports
    Screen.AdrenoTools   -> Icons.Filled.Memory
    Screen.Saves         -> Icons.Filled.Save
    Screen.FileManager   -> Icons.Filled.FolderOpen
    Screen.Settings      -> Icons.Filled.Settings
    Screen.Appearance    -> Icons.Filled.Palette
    Screen.LsfgSettings  -> Icons.Filled.Settings
    else                 -> Icons.Filled.Storefront
}

@Composable
fun AppDrawerContent(
    currentRoute: String,
    onNavigate: (Screen) -> Unit,
    onAbout: () -> Unit,
) {
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

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(PureBlack)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            Image(
                painter = painterResource(R.mipmap.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(44.dp),
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Star Bionic",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DimWhite,
            )
        }

        Spacer(Modifier.height(16.dp))

        SectionLabel("EMULATION")
        Spacer(Modifier.height(8.dp))
        PurpleDrawerItem(Screen.Games,          currentRoute, onNavigate)
        Spacer(Modifier.height(6.dp))
        PurpleDrawerItem(Screen.Containers,    currentRoute, onNavigate)
        Spacer(Modifier.height(6.dp))
        PurpleDrawerItem(Screen.Settings,      currentRoute, onNavigate)

        Spacer(Modifier.height(20.dp))
        SectionLabel("TOOLS")
        Spacer(Modifier.height(8.dp))
        PurpleDrawerItem(Screen.InputControls, currentRoute, onNavigate)
        Spacer(Modifier.height(6.dp))
        PurpleDrawerItem(Screen.AdrenoTools,   currentRoute, onNavigate)
        Spacer(Modifier.height(6.dp))
        PurpleDrawerItem(Screen.Saves,         currentRoute, onNavigate)
        Spacer(Modifier.height(6.dp))
        PurpleDrawerItem(Screen.LsfgSettings,  currentRoute, onNavigate)

        Spacer(Modifier.height(20.dp))
        SectionLabel("ABOUT & SUPPORT")
        Spacer(Modifier.height(8.dp))
        PurpleIconItem(
            label = "About",
            icon = Icons.Filled.Info,
            onClick = onAbout,
        )
        Spacer(Modifier.height(6.dp))
        PurpleIconItem(
            label = "Help and Support",
            icon = Icons.Filled.HelpOutline,
            onClick = { showHelp = true },
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = MutedWhite,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 20.dp),
    )
}

@Composable
private fun PurpleDrawerItem(screen: Screen, currentRoute: String, onNavigate: (Screen) -> Unit) {
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
            .padding(horizontal = 12.dp)
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
private fun PurpleIconItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
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
