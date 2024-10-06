package com.luke.petpal.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import com.luke.petpal.presentation.HomeViewModel
import com.luke.petpal.presentation.UserProfileViewModel
import com.luke.petpal.presentation.screens.AddPetScreen
import com.luke.petpal.presentation.screens.AdoptionScreen
import com.luke.petpal.presentation.chat.ChatListScreen
import com.luke.petpal.presentation.chat.ChatScreen
import com.luke.petpal.presentation.chat.ChatViewModel
import com.luke.petpal.presentation.screens.DetailedPetScreen
import com.luke.petpal.presentation.screens.LikedScreen
import com.luke.petpal.presentation.screens.MyAdoptionPetScreen
import com.luke.petpal.presentation.screens.PersonalScreen
import com.luke.petpal.presentation.screens.UpdatePetScreen

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    homeViewModel: HomeViewModel?,
) {

    val userProfileViewModel: UserProfileViewModel = hiltViewModel()
    val chatViewModel: ChatViewModel = hiltViewModel()

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
        composable(route = "pet_detailed/{documentId}") { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId")
            documentId?.let {
                DetailedPetScreen(
                    homeViewModel = homeViewModel,
                    paddingValues = paddingValues,
                    petId = it,
                    onChatClick = { chatId ->
                        navController.navigate("chat_screen/$chatId")
                    }
                )
            }
        }
        composable(route = "my_adoption_pets") {
            MyAdoptionPetScreen(
                homeViewModel = homeViewModel,
                paddingValues = paddingValues,
                onSeeMoreClick = { petId ->
                    navController.navigate("pet_edit/$petId")
                }
            )
        }
        composable(route = "pet_edit/{documentId}") { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId")
            documentId?.let {
                UpdatePetScreen(
                    homeViewModel = homeViewModel,
                    paddingValues = paddingValues,
                    petId = it,
                    onUpdatePet = {
                        navController.popBackStack()
//                        navigate("my_adoption_pets") {
//                            popUpTo("my_adoption_pets") {
//                                inclusive = false
//                            }
//                        }
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
            ChatListScreen(
                chatViewModel = chatViewModel,
                paddingValues = paddingValues,
                onChatClick = { chatId ->
                    navController.navigate("chat_screen/$chatId")
                }
            )
        }
        composable(route = "chat_screen/{chatId}") { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId")
            chatId?.let {
                ChatScreen(chatId = chatId, chatViewModel = chatViewModel)
            }
        }
        composable(route = BottomBarScreen.Personal.route) {
            PersonalScreen(
                userProfileViewModel,
                paddingValues,
                onAddPetClick = {
                    navController.navigate("add_pet")
                }
            )
        }
    }

    NavHost(navController = navController, graph = homeNavGraph)
}