package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Episode
import com.example.data.repository.NovelaRepository
import com.example.data.util.DonoDoMorroManager
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent

/**
 * Aba de Novela:
 * Exibe a Série Brasileira Ação Drama Favela ("O Dono do Morro").
 * 1. Informações completas da série (Título, Gênero, Sinopse, Estatísticas).
 * 2. Imagem principal centralizada logo abaixo das informações.
 * 3. 10 episódios com os 7 primeiros disponíveis e os episódios 8, 9 e 10 com cadeado.
 */
@Composable
fun NovelaScreen(
    repository: NovelaRepository,
    onWatchEpisode: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var episodes by remember { mutableStateOf(DonoDoMorroManager.getEpisodes(context)) }
    var isFavorite by remember { mutableStateOf(true) }
    var selectedLockedEpisode by remember { mutableStateOf<Episode?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("novela_screen_root"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CrimsonPrimary,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "🇧🇷", fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "NOVELA BRASILEIRA",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Produção Nacional Exclusiva",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GoldAccent,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier
                            .size(38.dp)
                            .background(DarkSurfaceElevated, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favoritar",
                            tint = if (isFavorite) CrimsonPrimary else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Link da série copiado!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .background(DarkSurfaceElevated, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartilhar",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 1. INFORMAÇÕES DA SÉRIE
        // ==========================================
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("series_info_section")
            ) {
                // Category & Badge tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CrimsonPrimary
                    ) {
                        Text(
                            text = "🇧🇷 SÉRIE BRASILEIRA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "AÇÃO • DRAMA • FAVELA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = DonoDoMorroManager.SERIES_TITLE,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                )

                Text(
                    text = "Ação, Drama, Romance & Redenção no Rio de Janeiro",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.LightGray,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Meta row: Rating, Views, 10 Episodes, HD, Age 16+
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "4.9",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = " (58k)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                        )
                    }

                    Text(text = "•", color = Color.DarkGray)

                    Text(
                        text = "10 Episódios",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(text = "•", color = Color.DarkGray)

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFE53935)
                    ) {
                        Text(
                            text = "16+",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(text = "•", color = Color.DarkGray)

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = DarkSurfaceElevated
                    ) {
                        Text(
                            text = "FULL HD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Synopsis
                Text(
                    text = DonoDoMorroManager.SERIES_SYNOPSIS,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFD1D5DB),
                        lineHeight = 18.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ADM & Developer Credits
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceElevated.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "👑", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Direção & ADM Oficial: Harrison Ruffo",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    }
                    Text(text = "💎 OFICIAL", fontSize = 10.sp, color = Color(0xFF80D8FF), fontWeight = FontWeight.Bold)
                }
            }
        }

        // ==========================================
        // 2. IMAGEM PRINCIPAL CENTRALIZADA LOGO ABAIXO
        // ==========================================
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            2.dp,
                            Brush.linearGradient(listOf(GoldAccent, CrimsonPrimary, Color(0xFF00E5FF))),
                            RoundedCornerShape(16.dp)
                        )
                        .shadow(12.dp, RoundedCornerShape(16.dp))
                        .clickable { onWatchEpisode(0) }
                        .testTag("series_main_image_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Image from Google Drive link provided by user
                        AsyncImage(
                            model = DonoDoMorroManager.OFFICIAL_SERIES_IMAGE_URL,
                            contentDescription = "Imagem Principal da Série Brasileira Ação Drama Favela",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Vignette and Gradient Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.3f),
                                            Color.Black.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                        )

                        // Top Badges on Image
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .align(Alignment.TopCenter),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Black.copy(alpha = 0.75f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "👑", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "CAPA OFICIAL",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = EmeraldGreen.copy(alpha = 0.9f)
                            ) {
                                Text(
                                    text = "7 EPS LIBERADOS ✅",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Bottom Center Action on Image
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = { onWatchEpisode(0) },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Assistir Série Agora (Episódio 1)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. EPISÓDIOS DA SÉRIE (10 EPISÓDIOS)
        // 7 DISPONÍVEIS E OS EPISÓDIOS 8, 9 E 10 COM CADEADO
        // ==========================================
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("series_episodes_section")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Episódios da Novela",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Episódios 1 a 7 liberados • Episódios 8, 9 e 10 com cadeado 🔒",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "10 EPISÓDIOS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Lista fluida, limpa e única com os 10 episódios
        items(episodes) { ep ->
            val isAvailable = ep.isUnlocked

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
                    .clickable {
                        if (isAvailable) {
                            onWatchEpisode(ep.episodeNumber - 1)
                        } else {
                            selectedLockedEpisode = ep
                        }
                    }
                    .testTag("episode_item_${ep.episodeNumber}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAvailable) DarkSurfaceElevated else Color(0xFF1B1212)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isAvailable) GoldAccent.copy(alpha = 0.3f) else Color(0xFFFF5252).copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Episode Number & Play / Lock box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isAvailable) CrimsonPrimary else Color(0xFF3B1515),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isAvailable) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Assistir",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Cadeado",
                                    tint = Color(0xFFFF5252),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Title, duration and synopsis
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isAvailable) EmeraldGreen.copy(alpha = 0.2f) else Color(0xFF4A1212)
                            ) {
                                Text(
                                    text = "EPISÓDIO ${ep.episodeNumber}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isAvailable) EmeraldGreen else Color(0xFFFF8A80),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Text(
                                text = "• ${ep.duration}",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = ep.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isAvailable) Color.White else Color.LightGray
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = ep.synopsis,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Right status button
                    if (isAvailable) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = EmeraldGreen.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = "Assistir ▶️",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF3B1515),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.7f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Cadeado",
                                    tint = Color(0xFFFF5252),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Cadeado 🔒",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF8A80)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog for Locked Episode (Episódios 8, 9, 10)
    selectedLockedEpisode?.let { lockedEp ->
        AlertDialog(
            onDismissRequest = { selectedLockedEpisode = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Cadeado",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Episódio ${lockedEp.episodeNumber} com Cadeado",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "O Episódio ${lockedEp.episodeNumber} (${lockedEp.title}) está bloqueado para os lançamentos da temporada.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray)
                    )
                    Text(
                        text = "Você pode desbloquear este episódio utilizando ${lockedEp.coinsCost} Moedas VIP ou como presente do Desenvolvedor & ADM Oficial Harrison Ruffo!",
                        style = MaterialTheme.typography.bodySmall.copy(color = GoldAccent)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Unlock episode
                        episodes = episodes.map {
                            if (it.id == lockedEp.id) it.copy(isUnlocked = true) else it
                        }
                        Toast.makeText(
                            context,
                            "Episódio ${lockedEp.episodeNumber} desbloqueado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        selectedLockedEpisode = null
                        onWatchEpisode(lockedEp.episodeNumber - 1)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                ) {
                    Text(text = "Desbloquear Cadeado 🔓", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedLockedEpisode = null }) {
                    Text(text = "Fechar", color = Color.White)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}
