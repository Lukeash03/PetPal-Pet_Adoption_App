package com.luke.petpal.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : BottomBarScreen(
        "HOME",
        "HOME",
        Icons.Default.Home
    )

    data object Liked : BottomBarScreen(
        "Liked",
        "Liked",
        Icons.Default.HeartBroken
    )

    data object Chat : BottomBarScreen(
        "Chat",
        "Chat",
        Icons.AutoMirrored.Filled.Chat
    )

    data object Personal : BottomBarScreen(
        "Personal",
        "Personal",
        Icons.Default.Pets
    )
}