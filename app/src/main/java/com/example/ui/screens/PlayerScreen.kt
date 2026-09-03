package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.Episode
import com.example.data.repository.NovelaRepository
import com.example.data.util.AuthManager
import com.example.data.util.DonoDoMorroManager
import com.example.data.util.InteractionManager
import com.example.data.util.YouTubeHelper
import com.example.ui.components.CamouflagedPlayerView
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    repository: NovelaRepository,
    initialEpisodeIndex: Int = 0,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var episodes by remember { mutableStateOf(DonoDoMorroManager.getEpisodes(context)) }
    var currentEpisodeIndex by remember(initialEpisodeIndex) {
        mutableIntStateOf(initialEpisodeIndex.coerceIn(0, (episodes.size - 1).coerceAtLeast(0)))
    }
    val currentEpisode = episodes.getOrElse(currentEpisodeIndex) { episodes.first() }

    var isLiked by remember(currentEpisode.episodeNumber) {
        mutableStateOf(InteractionManager.isEpisodeLikedByUser(context, currentEpisode.episodeNumber))
    }
    var likesCount by remember(currentEpisode.episodeNumber) {
        mutableIntStateOf(InteractionManager.getEpisodeLikes(context, currentEpisode.episodeNumber, currentEpisode.likesCount))
    }
    var showEpisodesSheet by remember { mutableStateOf(false) }
    var showCommentsSheet by remember { mutableStateOf(false) }
    var showUrlTestDialog by remember { mutableStateOf(false) }
    var newCommentText by remember { mutableStateOf("") }
    var realComments by remember { mutableStateOf(InteractionManager.getComments(context)) }

    val sheetState = rememberModalBottomSheetState()
    val commentsSheetState = rememberModalBottomSheetState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("player_screen_root")
    ) {
        // Camouflaged Player View (WebView for YouTube or native stream)
        CamouflagedPlayerView(
            videoUrl = currentEpisode.videoUrl,
            modifier = Modifier.fillMaxSize(),
            isPlaying = true
        )

        // Top Header Overlay (Back Button + Camouflage Status Badge + Test URL Button)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .testTag("player_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }

            // Playback Badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.75f),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.7f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Proteção de Reprodução",
                        tint = EmeraldGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reprodução HD ✅",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Link Manager & Test Tool
            IconButton(
                onClick = { showUrlTestDialog = true },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .testTag("player_url_test_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = "Configurar Link",
                    tint = GoldAccent
                )
            }
        }

        // Right-side Actions (TikTok / Reels Style)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Like Action
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        val result = InteractionManager.toggleEpisodeLike(context, currentEpisode.episodeNumber, likesCount)
                        isLiked = result.first
                        likesCount = result.second
                        val msg = if (isLiked) "Episódio curtido! Registrado em nuvem ❤️" else "Curtida removida"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .testTag("like_button")
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Curtir",
                        tint = if (isLiked) CrimsonPrimary else Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$likesCount",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Comments Action
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        realComments = InteractionManager.getComments(context)
                        showCommentsSheet = true
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .testTag("comments_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = "Comentários",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${realComments.size}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Episodes List Sheet Action
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { showEpisodesSheet = true },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .testTag("episodes_list_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = "Episódios",
                        tint = GoldAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Episódios",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldAccent,
                        fontSize = 10.sp
                    )
                )
            }

            // Share Action
            IconButton(
                onClick = {
                    Toast.makeText(context, "Link do episódio copiado!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Compartilhar",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Bottom Info Overlay & Navigation Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                    )
                )
                .padding(start = 16.dp, end = 80.dp, bottom = 28.dp, top = 20.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = CrimsonPrimary
            ) {
                Text(
                    text = "O DONO DO MORRO • EP ${currentEpisode.episodeNumber}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = currentEpisode.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = currentEpisode.synopsis,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.LightGray,
                    fontSize = 11.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation between episodes
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        if (currentEpisodeIndex > 0) {
                            currentEpisodeIndex--
                        }
                    },
                    enabled = currentEpisodeIndex > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkSurfaceElevated,
                        disabledContainerColor = DarkSurfaceElevated.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Episódio Anterior",
                        tint = if (currentEpisodeIndex > 0) Color.White else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Anterior", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        if (currentEpisodeIndex < episodes.size - 1) {
                            val nextEp = episodes[currentEpisodeIndex + 1]
                            if (nextEp.isUnlocked) {
                                currentEpisodeIndex++
                            } else {
                                Toast.makeText(
                                    context,
                                    "Episódio ${nextEp.episodeNumber} desbloqueado com Moedas VIP!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                episodes = episodes.toMutableList().also {
                                    it[currentEpisodeIndex + 1] = nextEp.copy(isUnlocked = true)
                                }
                                currentEpisodeIndex++
                            }
                        }
                    },
                    enabled = currentEpisodeIndex < episodes.size - 1,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CrimsonPrimary,
                        disabledContainerColor = CrimsonPrimary.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(text = "Próximo Ep", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Próximo Episódio",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Episode Selection Bottom Sheet
        if (showEpisodesSheet) {
            ModalBottomSheet(
                onDismissRequest = { showEpisodesSheet = false },
                sheetState = sheetState,
                containerColor = DarkSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("episodes_sheet_content")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Episódios - O Dono do Morro",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        IconButton(onClick = { showEpisodesSheet = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.height(380.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(episodes) { ep ->
                            val isCurrent = ep.episodeNumber == currentEpisode.episodeNumber
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentEpisodeIndex = ep.episodeNumber - 1
                                        showEpisodesSheet = false
                                    },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCurrent) CrimsonDark.copy(alpha = 0.7f) else DarkSurfaceElevated
                                ),
                                border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.dp, GoldAccent) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Episode Thumbnail
                                        val epThumb = YouTubeHelper.getThumbnailUrl(ep.videoUrl)
                                        Box(
                                            modifier = Modifier
                                                .width(68.dp)
                                                .height(42.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color.Black),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (epThumb != null) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(LocalContext.current)
                                                        .data(epThumb)
                                                        .crossfade(true)
                                                        .error(R.drawable.img_dono_morro_cover)
                                                        .placeholder(R.drawable.img_dono_morro_cover)
                                                        .fallback(R.drawable.img_dono_morro_cover)
                                                        .build(),
                                                    contentDescription = "Capa ${ep.title}",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else {
                                                androidx.compose.foundation.Image(
                                                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.img_dono_morro_cover),
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }

                                            // Badge icon overlay
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .background(
                                                        if (isCurrent) CrimsonPrimary else Color.Black.copy(alpha = 0.6f),
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (ep.isUnlocked) {
                                                    Icon(
                                                        imageVector = Icons.Default.PlayArrow,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                } else {
                                                    Icon(
                                                        imageVector = Icons.Default.Lock,
                                                        contentDescription = "Bloqueado",
                                                        tint = GoldAccent,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Episódio ${ep.episodeNumber}: ${ep.title}",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "Duração: ${ep.duration} • ${if (ep.isUnlocked) "Liberado" else "Custa ${ep.coinsCost} moedas"}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (ep.isUnlocked) EmeraldGreen else GoldAccent,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Comments Bottom Sheet
        if (showCommentsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showCommentsSheet = false },
                sheetState = commentsSheetState,
                containerColor = DarkSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Comentários (${realComments.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(realComments) { comment ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            if (comment.isOfficial) GoldAccent else CrimsonPrimary,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (comment.isOfficial) "👑" else comment.author.firstOrNull()?.toString() ?: "U",
                                        fontSize = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = comment.author,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (comment.isOfficial) GoldAccent else Color.White
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = comment.timeAgo,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                    Text(
                                        text = comment.content,
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Add comment input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newCommentText,
                            onValueChange = { newCommentText = it },
                            placeholder = { Text("Escreva um comentário...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CrimsonPrimary,
                                unfocusedBorderColor = Color.DarkGray
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (newCommentText.isNotBlank()) {
                                    val currentUser = AuthManager.getCurrentUser(context)
                                    val updated = InteractionManager.addComment(
                                        context = context,
                                        content = newCommentText,
                                        author = currentUser.name,
                                        avatarUrl = currentUser.photoUrl
                                    )
                                    realComments = updated
                                    newCommentText = ""
                                    Toast.makeText(context, "Comentário publicado com sucesso! Registrado em nuvem.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .background(CrimsonPrimary, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Enviar",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // URL Test & Playback Setting Dialog
        if (showUrlTestDialog) {
            var inputUrl1 by remember { mutableStateOf(DonoDoMorroManager.getEpisode1Url(context)) }
            var inputUrl2 by remember { mutableStateOf(DonoDoMorroManager.getEpisode2Url(context)) }
            var inputUrl3 by remember { mutableStateOf(DonoDoMorroManager.getEpisode3Url(context)) }
            var inputUrl4 by remember { mutableStateOf(DonoDoMorroManager.getEpisode4Url(context)) }
            var inputUrl5 by remember { mutableStateOf(DonoDoMorroManager.getEpisode5Url(context)) }
            var inputUrl6 by remember { mutableStateOf(DonoDoMorroManager.getEpisode6Url(context)) }
            var inputUrl7 by remember { mutableStateOf(DonoDoMorroManager.getEpisode7Url(context)) }

            AlertDialog(
                onDismissRequest = { showUrlTestDialog = false },
                title = {
                    Text(
                        text = "Configurar Links dos Episódios",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Insira os links de vídeo ou YouTube. A reprodução é otimizada diretamente no player do app.",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )

                        // Episódio 1
                        OutlinedTextField(
                            value = inputUrl1,
                            onValueChange = { inputUrl1 = it },
                            label = { Text("URL do Episódio 1") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        val detectedId1 = YouTubeHelper.extractVideoId(inputUrl1)
                        if (detectedId1 != null) {
                            Text(
                                text = "✅ Ep 1 Detectado (ID: $detectedId1)",
                                fontSize = 11.sp,
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Episódio 2
                        OutlinedTextField(
                            value = inputUrl2,
                            onValueChange = { inputUrl2 = it },
                            label = { Text("URL do Episódio 2") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        val detectedId2 = YouTubeHelper.extractVideoId(inputUrl2)
                        if (detectedId2 != null) {
                            Text(
                                text = "✅ Ep 2 Detectado (ID: $detectedId2)",
                                fontSize = 11.sp,
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Episódio 3
                        OutlinedTextField(
                            value = inputUrl3,
                            onValueChange = { inputUrl3 = it },
                            label = { Text("URL do Episódio 3") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        val detectedId3 = YouTubeHelper.extractVideoId(inputUrl3)
                        if (detectedId3 != null) {
                            Text(
                                text = "✅ Ep 3 Detectado (ID: $detectedId3)",
                                fontSize = 11.sp,
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Episódio 4
                        OutlinedTextField(
                            value = inputUrl4,
                            onValueChange = { inputUrl4 = it },
                            label = { Text("URL do Episódio 4") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        val detectedId4 = YouTubeHelper.extractVideoId(inputUrl4)
                        if (detectedId4 != null) {
                            Text(
                                text = "✅ Ep 4 Detectado (ID: $detectedId4)",
                                fontSize = 11.sp,
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Episódio 5
                        OutlinedTextField(
                            value = inputUrl5,
                            onValueChange = { inputUrl5 = it },
                            label = { Text("URL do Episódio 5") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        val detectedId5 = YouTubeHelper.extractVideoId(inputUrl5)
                        if (detectedId5 != null) {
                            Text(
                                text = "✅ Ep 5 Detectado (ID: $detectedId5)",
                                fontSize = 11.sp,
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Episódio 6
                        OutlinedTextField(
                            value = inputUrl6,
                            onValueChange = { inputUrl6 = it },
                            label = { Text("URL do Episódio 6") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        val detectedId6 = YouTubeHelper.extractVideoId(inputUrl6)
                        if (detectedId6 != null) {
                            Text(
                                text = "✅ Ep 6 Detectado (ID: $detectedId6)",
                                fontSize = 11.sp,
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Episódio 7
                        OutlinedTextField(
                            value = inputUrl7,
                            onValueChange = { inputUrl7 = it },
                            label = { Text("URL do Episódio 7") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        val detectedId7 = YouTubeHelper.extractVideoId(inputUrl7)
                        if (detectedId7 != null) {
                            Text(
                                text = "✅ Ep 7 Detectado (ID: $detectedId7)",
                                fontSize = 11.sp,
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            DonoDoMorroManager.setEpisode1Url(context, inputUrl1)
                            DonoDoMorroManager.setEpisode2Url(context, inputUrl2)
                            DonoDoMorroManager.setEpisode3Url(context, inputUrl3)
                            DonoDoMorroManager.setEpisode4Url(context, inputUrl4)
                            DonoDoMorroManager.setEpisode5Url(context, inputUrl5)
                            DonoDoMorroManager.setEpisode6Url(context, inputUrl6)
                            DonoDoMorroManager.setEpisode7Url(context, inputUrl7)
                            episodes = DonoDoMorroManager.getEpisodes(context)
                            showUrlTestDialog = false
                            Toast.makeText(context, "Links dos episódios salvos com sucesso!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                    ) {
                        Text("Salvar Links")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            DonoDoMorroManager.resetEpisode1Url(context)
                            DonoDoMorroManager.resetEpisode2Url(context)
                            DonoDoMorroManager.resetEpisode3Url(context)
                            DonoDoMorroManager.resetEpisode4Url(context)
                            DonoDoMorroManager.resetEpisode5Url(context)
                            DonoDoMorroManager.resetEpisode6Url(context)
                            DonoDoMorroManager.resetEpisode7Url(context)
                            episodes = DonoDoMorroManager.getEpisodes(context)
                            showUrlTestDialog = false
                            Toast.makeText(context, "Links restaurados para o padrão oficial", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Text("Restaurar Padrões")
                    }
                },
                containerColor = DarkSurface
            )
        }
    }
}
