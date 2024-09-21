package com.luke.petpal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.luke.petpal.presentation.UserProfileViewModel
import com.luke.petpal.presentation.auth.AuthViewModel
import com.luke.petpal.presentation.auth.ForgotPasswordScreen
import com.luke.petpal.presentation.auth.LoginScreen
import com.luke.petpal.presentation.auth.SignUpDetailScreen
import com.luke.petpal.presentation.auth.SignUpScreen

@Composable
fun NavGraphBuilder.AuthNavGraph(
    authViewModel: AuthViewModel,
    userProfileViewModel: UserProfileViewModel,
    navController: NavController,
    splashScreenCompleted: Boolean
) {

    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AuthScreen.Login.route
    ) {
        composable(route = AuthScreen.Login.route) {
            LoginScreen(authViewModel, navController = navController, splashScreenCompleted)
        }
        composable(route = AuthScreen.SignUp.route) {
            SignUpScreen(authViewModel, navController = navController)
        }
        composable(route = AuthScreen.Forgot.route) {
            ForgotPasswordScreen(authViewModel = authViewModel, navController = navController)
        }
        composable(route = AuthScreen.SignUpDetailed.route) {
            SignUpDetailScreen(
                userProfileViewModel = userProfileViewModel,
                navController = navController
            )
        }
    }
}

sealed class AuthScreen(val route: String) {
    object Login : AuthScreen(route = "LOGIN")
    object SignUp : AuthScreen(route = "SIGN_UP")
    object SignUpDetailed : AuthScreen(route = "SIGN_UP_DETAILED")
    object Forgot : AuthScreen(route = "FORGOT")
}