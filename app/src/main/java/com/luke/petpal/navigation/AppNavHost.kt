package com.luke.petpal.navigation

import android.window.SplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.luke.petpal.presentation.screens.HomeScreen
import com.luke.petpal.presentation.auth.AuthViewModel
import com.luke.petpal.presentation.auth.ForgotPasswordScreen
import com.luke.petpal.presentation.auth.LoginScreen
import com.luke.petpal.presentation.auth.SignUpDetailScreen
import com.luke.petpal.presentation.auth.SignUpScreen

@Composable
fun AppNavHost(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_LOGIN,
    splashScreenCompleted: Boolean
) {

    val navGraph: NavGraph = navController.createGraph(startDestination = startDestination) {
        composable(ROUTE_LOGIN) {
            val state by viewModel.googleSignInFlow.collectAsStateWithLifecycle()

            LoginScreen(viewModel, navController = navController, splashScreenCompleted)
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
        composable(ROUTE_SIGNUP_DETAILED) {
            SignUpDetailScreen(viewModel = viewModel, navController = navController)
        }
    }
    NavHost(
        modifier = modifier,
        navController = navController,
        graph = navGraph
    )
}