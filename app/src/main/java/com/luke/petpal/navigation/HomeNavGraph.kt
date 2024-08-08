package com.luke.petpal.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import com.luke.petpal.BottomBarScreen
import com.luke.petpal.presentation.auth.AuthViewModel
import com.luke.petpal.presentation.screens.AdoptionScreen
import com.luke.petpal.presentation.screens.PersonalScreen

@Composable
fun HomeNavGraph(navController: NavHostController, paddingValues: PaddingValues, viewModel: AuthViewModel?) {

    val homeNavGraph: NavGraph = navController.createGraph(
        startDestination = BottomBarScreen.Home.route,
        route = Graph.HOME
    ) {
        composable(route = BottomBarScreen.Home.route) {
            AdoptionScreen(name = BottomBarScreen.Home.route, paddingValues)
        }
        composable(route = BottomBarScreen.Liked.route) {
            AdoptionScreen(name = BottomBarScreen.Liked.route, paddingValues)
        }
        composable(route = BottomBarScreen.Chat.route) {
            AdoptionScreen(name = BottomBarScreen.Chat.route, paddingValues)
        }
        composable(route = BottomBarScreen.Personal.route) {
            PersonalScreen(viewModel)
        }
    }

    NavHost(navController = navController, graph = homeNavGraph)
}