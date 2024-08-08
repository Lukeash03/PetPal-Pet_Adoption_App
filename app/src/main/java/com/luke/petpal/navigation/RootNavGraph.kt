package com.luke.petpal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import com.luke.petpal.presentation.screens.HomeScreen
import com.luke.petpal.presentation.auth.AuthViewModel

@Composable
fun RootNavGraph(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    splashScreenCompleted: Boolean
) {

    val rootNavGraph: NavGraph = navController.createGraph(
        startDestination = Graph.AUTHENTICATION,
        route = Graph.ROOT
    ) {
        AuthNavGraph(
            navController = navController,
            viewModel = viewModel,
            splashScreenCompleted = splashScreenCompleted
        )
        composable(route = Graph.HOME) {
            HomeScreen(
                viewModel = viewModel,
                logout = {
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(0) {}
                    }
                }
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
    const val HOME_DETAILS = "home_details_graph"
}