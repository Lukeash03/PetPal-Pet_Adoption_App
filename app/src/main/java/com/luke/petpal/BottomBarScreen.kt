package com.luke.petpal

import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomBarScreen(
        "HOME",
        "HOME",
        Icons.Default.Home
    )

    object Liked : BottomBarScreen(
        "Liked",
        "Liked",
        Icons.Default.HeartBroken
    )

    object Chat : BottomBarScreen(
        "Chat",
        "Chat",
        Icons.AutoMirrored.Filled.Chat
    )

    object Personal : BottomBarScreen(
        "Personal",
        "Personal",
        Icons.Default.Pets
    )
}