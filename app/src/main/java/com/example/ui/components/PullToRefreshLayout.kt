package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.GoldAccent
import kotlin.math.roundToInt

/**
 * Robust, gesture-based Pull-to-Refresh layout that provides tactile visual feedback,
 * dynamic pull distance, status labels, and animated spinner.
 */
@Composable
fun PullToRefreshLayout(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    lastUpdatedText: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var pullOffsetY by remember { mutableFloatStateOf(0f) }
    val maxDragDistance = 240f
    val triggerThreshold = 140f

    val animatedOffset by animateFloatAsState(
        targetValue = if (isRefreshing) 110f else pullOffsetY,
        label = "pull_offset_anim"
    )

    val draggableState = rememberDraggableState { delta ->
        if (!isRefreshing) {
            val newOffset = (pullOffsetY + delta * 0.55f).coerceIn(0f, maxDragDistance)
            pullOffsetY = newOffset
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .draggable(
                state = draggableState,
                orientation = Orientation.Vertical,
                onDragStopped = {
                    if (pullOffsetY >= triggerThreshold && !isRefreshing) {
                        onRefresh()
                    }
                    pullOffsetY = 0f
                }
            )
            .testTag("pull_to_refresh_container")
    ) {
        // Pull indicator header
        if (animatedOffset > 10f || isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(animatedOffset.dp / 2.2f)
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                    tonalElevation = 6.dp,
                    shadowElevation = 4.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Brush.horizontalGradient(listOf(CrimsonPrimary, GoldAccent))
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.5.dp,
                                color = CrimsonPrimary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Atualizando episódios...",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Buscando novidades no catálogo",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = GoldAccent
                                    )
                                )
                            }
                        } else {
                            val rotationDegree = (animatedOffset / triggerThreshold * 180f).coerceAtMost(180f)
                            val isReadyToRelease = animatedOffset >= triggerThreshold

                            Icon(
                                imageVector = if (isReadyToRelease) Icons.Default.Refresh else Icons.Default.ArrowDownward,
                                contentDescription = "Pull indicator",
                                tint = if (isReadyToRelease) GoldAccent else CrimsonPrimary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(if (isReadyToRelease) 0f else rotationDegree)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isReadyToRelease) "Solte para atualizar!" else "Puxe para atualizar novidades",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isReadyToRelease) GoldAccent else Color.White
                                    )
                                )
                                Text(
                                    text = lastUpdatedText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Scrollable content shifted by pull offset
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, animatedOffset.roundToInt()) }
        ) {
            content()
        }
    }
}
