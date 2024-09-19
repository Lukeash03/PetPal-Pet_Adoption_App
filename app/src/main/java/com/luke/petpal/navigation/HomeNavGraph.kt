package com.luke.petpal.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import com.luke.petpal.presentation.HomeViewModel
import com.luke.petpal.presentation.screens.AddPetScreen
import com.luke.petpal.presentation.screens.AdoptionScreen
import com.luke.petpal.presentation.chat.ChatHomeScreen
import com.luke.petpal.presentation.screens.DetailedPetScreen
import com.luke.petpal.presentation.screens.LikedScreen
import com.luke.petpal.presentation.screens.MyAdoptionPetScreen
import com.luke.petpal.presentation.screens.PersonalScreen

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    homeViewModel: HomeViewModel?
) {

    val homeNavGraph: NavGraph = navController.createGraph(
        startDestination = BottomBarScreen.Home.route,
        route = Graph.HOME
    ) {
        composable(route = BottomBarScreen.Home.route) {
            AdoptionScreen(
                homeViewModel = homeViewModel,
                paddingValues = paddingValues,
                onMyPostClick = {
                    navController.navigate("my_adoption_pets")
                },
                onSeeMoreClick = { documentId ->
                    navController.navigate("pet_detailed/$documentId")
                }
            )
    }
        composable(route = "add_pet") {
            AddPetScreen(
                homeViewModel = homeViewModel,
                paddingValues = paddingValues,
                onAddPet = {
                    navController.navigate(Graph.HOME)
                }
            )
        }
        composable(route = "my_adoption_pets") {
            MyAdoptionPetScreen(
                homeViewModel = homeViewModel,
                paddingValues = paddingValues,
                onSeeMoreClick = { documentId ->
                    navController.navigate("pet_detailed/$documentId")
                }
            )
        }
        composable(route = "pet_detailed/{documentId}") { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId")
            documentId?.let {
                DetailedPetScreen(
                    homeViewModel = homeViewModel,
                    paddingValues = paddingValues,
                    petDocumentId = it,
                    onChatClick = { chatId ->
                        navController.navigate("chat_screen/$chatId")
                    }
                )
            }
        }
        composable(route = BottomBarScreen.Liked.route) {
            LikedScreen(
                homeViewModel = homeViewModel,
                paddingValues = paddingValues,
                onSeeMoreClick = { documentId ->
                    navController.navigate("pet_detailed/$documentId")
                }
            )
        }
        composable(route = BottomBarScreen.Chat.route) {
            ChatHomeScreen(
                navController = navController,
                paddingValues = paddingValues
            )
        }
        composable(route = "chat_screen/{chatId}") {

        }
        composable(route = BottomBarScreen.Personal.route) {
            PersonalScreen(
                homeViewModel,
                paddingValues,
//                onAdoptionPetClick = {
//                    navController.navigate("my_adoption_pets")
//                }
            )
        }
    }

    NavHost(navController = navController, graph = homeNavGraph)
}