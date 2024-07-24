package com.luke.petpal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.luke.petpal.ui.screens.HomeScreen
import com.luke.petpal.ui.auth.AuthViewModel
import com.luke.petpal.ui.auth.ForgotPasswordScreen
import com.luke.petpal.ui.auth.LoginScreen
import com.luke.petpal.ui.auth.SignUpScreen

@Composable
fun AppNavHost(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_LOGIN
) {

    val navGraph: NavGraph = navController.createGraph(startDestination = startDestination) {
        composable(ROUTE_LOGIN) {
            LoginScreen(viewModel, navController = navController)
        }
        composable(ROUTE_SIGNUP) {
            SignUpScreen(viewModel, navController = navController)
        }
        composable(ROUTE_HOME) {
            HomeScreen(viewModel, navController)
        }
        composable(ROUTE_FORGOT_PASSWORD) {
            ForgotPasswordScreen(viewModel = viewModel, navController = navController)
        }
    }
    NavHost(
        modifier = modifier,
        navController = navController,
        graph = navGraph
    )
}