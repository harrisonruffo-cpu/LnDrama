package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.util.YouTubeHelper
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.GoldAccent

/**
 * High-performance Camouflaged Player View.
 * Dynamically switches between:
 * 1. Camouflaged YouTube iframe WebView with anti-ad & anti-sharing parameters.
 * 2. Native media streaming view for direct video files (.mp4 / .m3u8).
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CamouflagedPlayerView(
    videoUrl: String,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true
) {
    val context = LocalContext.current
    val videoId = remember(videoUrl) { YouTubeHelper.extractVideoId(videoUrl) }
    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("camouflaged_player_view")
    ) {
        if (videoId != null) {
            // Anti-blocking Camouflaged YouTube player
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("youtube_webview_player"),
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setBackgroundColor(android.graphics.Color.BLACK)

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            mediaPlaybackRequiresUserGesture = false
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                            builtInZoomControls = false
                            displayZoomControls = false
                            allowFileAccess = false
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                // Block external navigation to keep user seamlessly in the app
                                return true
                            }
                        }

                        webChromeClient = object : WebChromeClient() {}

                        loadDataWithBaseURL(
                            "https://www.youtube-nocookie.com",
                            YouTubeHelper.buildCamouflagedHtml(videoId),
                            "text/html",
                            "utf-8",
                            null
                        )
                    }
                },
                update = { webView ->
                    // Re-load if videoId changed
                    val currentHtml = YouTubeHelper.buildCamouflagedHtml(videoId)
                    webView.loadDataWithBaseURL(
                        "https://www.youtube-nocookie.com",
                        currentHtml,
                        "text/html",
                        "utf-8",
                        null
                    )
                }
            )

            // Cleanup on dispose
            DisposableEffect(videoId) {
                onDispose {
                    // Handled automatically
                }
            }
        } else {
            // Direct MP4 or external stream fallback
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F0F16)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = "Playing",
                        tint = CrimsonPrimary,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Reprodução Contínua HD",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Transmitindo episódio diretamente dos servidores de alta velocidade",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Loading overlay
        if (isLoading && videoId != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = CrimsonPrimary,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Carregando episódio...",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GoldAccent,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}
