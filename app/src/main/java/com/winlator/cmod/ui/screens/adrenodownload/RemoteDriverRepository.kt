package com.winlator.cmod.ui.screens.adrenodownload

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class RemoteDriverRepository(private val context: Context) {

    private val cacheDir: File by lazy {
        File(context.cacheDir, "adreno_driver_dl").apply { if (!exists()) mkdirs() }
    }

    suspend fun fetchReleases(source: RemoteDriverSource): Result<List<RemoteDriverRelease>> =
        withContext(Dispatchers.IO) {
            try {
                val body = httpGetText(source.releasesApiUrl)
                val arr = JSONArray(body)
                val out = ArrayList<RemoteDriverRelease>(arr.length())
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val assetsJson = o.optJSONArray("assets") ?: JSONArray()
                    val assets = ArrayList<RemoteDriverAsset>(assetsJson.length())
                    for (j in 0 until assetsJson.length()) {
                        val a = assetsJson.getJSONObject(j)
                        val name = a.optString("name", "")
                        if (!name.endsWith(".zip", ignoreCase = true)) continue
                        assets.add(
                            RemoteDriverAsset(
                                name = name,
                                downloadUrl = a.optString("browser_download_url", ""),
                                sizeBytes = a.optLong("size", 0L),
                            )
                        )
                    }
                    if (assets.isEmpty()) continue
                    out.add(
                        RemoteDriverRelease(
                            source = source.name,
                            tagName = o.optString("tag_name", ""),
                            displayName = o.optString("name", "").ifBlank { o.optString("tag_name", "") },
                            publishedAt = o.optString("published_at", "").take(10),
                            notes = o.optString("body", "").takeIf { it.isNotBlank() },
                            assets = assets,
                        )
                    )
                }
                Result.success(out)
            } catch (t: Throwable) {
                Log.w(TAG, "fetchReleases failed for ${source.name}", t)
                Result.failure(t)
            }
        }

    /** Downloads to internal cache. Returns the local file. Calls progress(0..100). */
    suspend fun downloadAsset(
        asset: RemoteDriverAsset,
        progress: (Int) -> Unit,
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val safeName = asset.name.replace(Regex("[^A-Za-z0-9._-]"), "_")
            val outFile = File(cacheDir, "${System.currentTimeMillis()}_$safeName")
            val conn = (URL(asset.downloadUrl).openConnection() as HttpURLConnection).apply {
                connectTimeout = 15_000
                readTimeout = 30_000
                setRequestProperty("User-Agent", "star-android")
                instanceFollowRedirects = true
            }
            try {
                if (conn.responseCode !in 200..299) {
                    return@withContext Result.failure(RuntimeException("HTTP ${conn.responseCode}"))
                }
                val totalBytes = if (asset.sizeBytes > 0) asset.sizeBytes else conn.contentLengthLong
                conn.inputStream.use { input ->
                    FileOutputStream(outFile).use { output ->
                        val buf = ByteArray(64 * 1024)
                        var written = 0L
                        var lastPct = -1
                        while (true) {
                            val n = input.read(buf)
                            if (n <= 0) break
                            output.write(buf, 0, n)
                            written += n
                            if (totalBytes > 0) {
                                val pct = ((written * 100) / totalBytes).toInt().coerceIn(0, 100)
                                if (pct != lastPct) {
                                    lastPct = pct
                                    progress(pct)
                                }
                            }
                        }
                    }
                }
                progress(100)
                Result.success(outFile)
            } finally {
                conn.disconnect()
            }
        } catch (t: Throwable) {
            Log.w(TAG, "downloadAsset failed for ${asset.name}", t)
            Result.failure(t)
        }
    }

    fun clearCache() {
        cacheDir.listFiles()?.forEach { it.delete() }
    }

    private fun httpGetText(url: String): String {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15_000
            readTimeout = 30_000
            setRequestProperty("User-Agent", "star-android")
            setRequestProperty("Accept", "application/vnd.github+json")
            instanceFollowRedirects = true
        }
        try {
            if (conn.responseCode !in 200..299) error("HTTP ${conn.responseCode}")
            return conn.inputStream.bufferedReader().use { it.readText() }
        } finally {
            conn.disconnect()
        }
    }

    companion object {
        private const val TAG = "RemoteDriverRepo"
    }
}
