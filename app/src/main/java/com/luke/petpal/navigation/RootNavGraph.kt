package com.luke.petpal.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import com.luke.petpal.presentation.HomeViewModel
import com.luke.petpal.presentation.screens.HomeScreen
import com.luke.petpal.presentation.auth.AuthViewModel

@Composable
fun RootNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    splashScreenCompleted: Boolean
) {

    val authViewModel: AuthViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()

    val rootNavGraph: NavGraph = navController.createGraph(
        startDestination = Graph.AUTHENTICATION,
        route = Graph.ROOT
    ) {
        AuthNavGraph(
            navController = navController,
            viewModel = authViewModel,
            splashScreenCompleted = splashScreenCompleted
        )
        composable(route = Graph.HOME) {
            val activity = LocalContext.current as Activity
            HomeScreen(
                homeViewModel = homeViewModel,
                logout = {
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(0) {}
                    }
                },
                activity = activity
            )
        }
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        graph = rootNavGraph
    )
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
    const val HOME_ADD_PET = "home_add_pet_graph"
}