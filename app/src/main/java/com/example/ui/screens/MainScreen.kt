package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
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
import com.example.data.util.DonoDoMorroManager
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldAccent

enum class ScreenTab(val title: String) {
    HOME("Início"),
    WATCH("Assistir"),
    COMMUNITY("Comunidade"),
    MY_LIST("Minha Lista"),
    PROFILE("Perfil")
}

@Composable
fun MainScreen(
    repository: NovelaRepository = remember { NovelaRepository() }
) {
    val context = LocalContext.current
    var selectedTab by rememberSaveable { mutableStateOf(ScreenTab.HOME) }
    var showUrlConfigFromProfile by remember { mutableStateOf(false) }

    // Auto-follower rule: When the user opens the app, they automatically become a follower of Harrison Ruffo
    LaunchedEffect(Unit) {
        DonoDoMorroManager.registerAppUserFollow(context)
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
                                    ScreenTab.WATCH -> Icon(
                                        imageVector = if (isSelected) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircleOutline,
                                        contentDescription = tab.title
                                    )
                                    ScreenTab.COMMUNITY -> Icon(
                                        imageVector = if (isSelected) Icons.Filled.People else Icons.Outlined.People,
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
                        onWatchDrama = { _ ->
                            selectedTab = ScreenTab.WATCH
                        }
                    )
                }

                ScreenTab.WATCH -> {
                    PlayerScreen(
                        repository = repository,
                        onBack = {
                            selectedTab = ScreenTab.HOME
                        }
                    )
                }

                ScreenTab.COMMUNITY -> {
                    CommunityScreen(
                        repository = repository
                    )
                }

                ScreenTab.MY_LIST -> {
                    MyListScreen(
                        repository = repository,
                        onWatchDrama = { _ ->
                            selectedTab = ScreenTab.WATCH
                        }
                    )
                }

                ScreenTab.PROFILE -> {
                    ProfileScreen(
                        onConfigureUrl = {
                            selectedTab = ScreenTab.WATCH
                        }
                    )
                }
            }
        }
    }
}
