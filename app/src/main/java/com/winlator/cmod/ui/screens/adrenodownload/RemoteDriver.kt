package com.winlator.cmod.ui.screens.adrenodownload

data class RemoteDriverSource(
    val name: String,
    val owner: String,
    val repo: String,
) {
    val releasesApiUrl: String get() = "https://api.github.com/repos/$owner/$repo/releases?per_page=20"
    val webUrl: String get() = "https://github.com/$owner/$repo"
}

data class RemoteDriverAsset(
    val name: String,
    val downloadUrl: String,
    val sizeBytes: Long,
)

data class RemoteDriverRelease(
    val source: String,
    val tagName: String,
    val displayName: String,
    val publishedAt: String,
    val notes: String?,
    val assets: List<RemoteDriverAsset>,
)

object DriverSources {
    val ALL: List<RemoteDriverSource> = listOf(
        RemoteDriverSource("Banners-Turnip", "The412Banner", "Banners-Turnip"),
        RemoteDriverSource("StevenMXZ", "StevenMXZ", "Adreno-Tools-Drivers"),
        RemoteDriverSource("whitebelyash", "whitebelyash", "freedreno_turnip-CI"),
    )
}

fun formatSize(bytes: Long): String = when {
    bytes >= 1_073_741_824L -> "%.1f GB".format(bytes / 1_073_741_824.0)
    bytes >= 1_048_576L     -> "%.1f MB".format(bytes / 1_048_576.0)
    bytes >= 1024L          -> "%.1f KB".format(bytes / 1024.0)
    else                    -> "$bytes B"
}
