package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.NovelaRepository
import com.example.data.util.AuthManager
import com.example.data.util.DonoDoMorroManager
import com.example.ui.components.ExitConfirmationDialog
import com.example.ui.components.MandatoryLoginDialog
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldAccent

enum class ScreenTab(val title: String) {
    HOME("Início"),
    NOVELA("Episódios"),
    WATCH("Assistir"),
    MY_LIST("Minha Lista"),
    PROFILE("Perfil")
}

@Composable
fun MainScreen(
    repository: NovelaRepository = remember { NovelaRepository() }
) {
    val context = LocalContext.current
    var selectedTab by rememberSaveable { mutableStateOf(ScreenTab.NOVELA) }
    var selectedEpisodeIndex by rememberSaveable { mutableIntStateOf(0) }
    var showUrlConfigFromProfile by remember { mutableStateOf(false) }

    // Estado do Diálogo de confirmação de saída do aplicativo
    var showExitDialog by remember { mutableStateOf(false) }

    // Estado do Diálogo obrigatório de login no primeiro acesso
    var isUserLoggedIn by remember { mutableStateOf(AuthManager.isLoggedIn(context)) }

    // Intercepta o botão de voltar físico ou gestual do Android
    BackHandler(enabled = true) {
        if (selectedTab == ScreenTab.WATCH) {
            // Se estiver assistindo, volta para a tela de Novela
            selectedTab = ScreenTab.NOVELA
        } else if (selectedTab != ScreenTab.NOVELA) {
            // Se estiver em outra aba, volta para a aba principal da Novela
            selectedTab = ScreenTab.NOVELA
        } else {
            // Se já estiver na tela principal, abre o aviso de confirmação de saída
            showExitDialog = true
        }
    }

    // Auto-follower rule: When the user opens the app, they automatically become a follower of Harrison Ruffo
    LaunchedEffect(Unit) {
        DonoDoMorroManager.registerAppUserFollow(context)
    }

    // Exibe o diálogo de login obrigatório se o usuário ainda não tiver autenticado
    if (!isUserLoggedIn) {
        MandatoryLoginDialog(
            onLoginSuccess = { name, email, provider ->
                isUserLoggedIn = true
            }
        )
    }

    // Exibe o aviso ao apertar o botão de voltar do Android
    if (showExitDialog) {
        ExitConfirmationDialog(
            onConfirmExit = {
                showExitDialog = false
                (context as? Activity)?.finish()
            },
            onDismiss = {
                showExitDialog = false
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_screen_scaffold"),
        contentWindowInsets = WindowInsets.navigationBars,
        bottomBar = {
            // Hide bottom bar when watching in full screen vertical player for immersive experience
            if (selectedTab != ScreenTab.WATCH) {
                NavigationBar(
                    containerColor = DarkSurface,
                    tonalElevation = 8.dp
                ) {
                    ScreenTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            icon = {
                                when (tab) {
                                    ScreenTab.HOME -> Icon(
                                        imageVector = if (isSelected) Icons.Filled.Home else Icons.Outlined.Home,
                                        contentDescription = tab.title
                                    )
                                    ScreenTab.NOVELA -> Icon(
                                        imageVector = if (isSelected) Icons.Filled.Movie else Icons.Outlined.Movie,
                                        contentDescription = tab.title
                                    )
                                    ScreenTab.WATCH -> Icon(
                                        imageVector = if (isSelected) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircleOutline,
                                        contentDescription = tab.title
                                    )
                                    ScreenTab.MY_LIST -> Icon(
                                        imageVector = if (isSelected) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                        contentDescription = tab.title
                                    )
                                    ScreenTab.PROFILE -> Icon(
                                        imageVector = if (isSelected) Icons.Filled.Person else Icons.Outlined.Person,
                                        contentDescription = tab.title
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = GoldAccent,
                                indicatorColor = CrimsonPrimary,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                ScreenTab.HOME -> {
                    HomeScreen(
                        repository = repository,
                        onWatchDrama = { drama ->
                            selectedEpisodeIndex = 0
                            selectedTab = ScreenTab.NOVELA
                        }
                    )
                }

                ScreenTab.NOVELA -> {
                    NovelaScreen(
                        repository = repository,
                        onWatchEpisode = { epIndex ->
                            selectedEpisodeIndex = epIndex
                            selectedTab = ScreenTab.WATCH
                        }
                    )
                }

                ScreenTab.WATCH -> {
                    PlayerScreen(
                        repository = repository,
                        initialEpisodeIndex = selectedEpisodeIndex,
                        onBack = {
                            selectedTab = ScreenTab.NOVELA
                        }
                    )
                }

                ScreenTab.MY_LIST -> {
                    MyListScreen(
                        repository = repository,
                        onWatchDrama = { _ ->
                            selectedEpisodeIndex = 0
                            selectedTab = ScreenTab.NOVELA
                        }
                    )
                }

                ScreenTab.PROFILE -> {
                    ProfileScreen(
                        onConfigureUrl = {
                            selectedEpisodeIndex = 0
                            selectedTab = ScreenTab.WATCH
                        },
                        onOpenNovela = {
                            selectedTab = ScreenTab.NOVELA
                        },
                        onWatchEpisode = { epIndex ->
                            selectedEpisodeIndex = epIndex
                            selectedTab = ScreenTab.WATCH
                        }
                    )
                }
            }
        }
    }
}
