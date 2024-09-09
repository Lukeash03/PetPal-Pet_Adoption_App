package com.luke.petpal.presentation.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults.containerColor
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.luke.petpal.navigation.BottomBarScreen
import com.luke.petpal.R
import com.luke.petpal.navigation.Graph
import com.luke.petpal.navigation.HomeNavGraph
import com.luke.petpal.presentation.HomeViewModel
import com.luke.petpal.presentation.theme.AppTheme
import com.luke.petpal.presentation.theme.PetPalTheme
import com.luke.petpal.presentation.theme.appColorPrimary

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel?,
    navController: NavHostController = rememberNavController(),
    logout: () -> Unit,
    activity: Activity,
    splashScreenCompleted: Boolean
) {

    BackHandler {
        activity.finish()
    }

    val systemUiController = rememberSystemUiController()
    val statusBarColor = appColorPrimary

    LaunchedEffect(splashScreenCompleted) {
        Log.d("HomeScreen", "Setting status bar and navigation bar color")
        if (splashScreenCompleted) {
            systemUiController.setStatusBarColor(
                color = statusBarColor,
            )
            systemUiController.setNavigationBarColor(
                color = statusBarColor,
            )
        }
    }

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            val screens = listOf(
                BottomBarScreen.Home,
                BottomBarScreen.Liked,
                BottomBarScreen.Chat,
                BottomBarScreen.Personal,
            )

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val bottomBarDestination = screens.any { it.route == currentDestination?.route }
//            if (bottomBarDestination) {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontFamily = FontFamily.Cursive,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            homeViewModel?.logout()

                            logout()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
//            }
        },
        bottomBar = {
            BottomBar(navController = navController)
        },
        floatingActionButton = {
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute == BottomBarScreen.Home.route) {
                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate("add_pet")
                    },
                    modifier = Modifier
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "")
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Add a pet")
                }
            }
        }
    ) {
        HomeNavGraph(
            navController = navController,
            paddingValues = it,
            homeViewModel = homeViewModel
        )
    }

}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Liked,
        BottomBarScreen.Chat,
        BottomBarScreen.Personal,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = screens.any { it.route == currentDestination?.route }
    if (bottomBarDestination) {
        NavigationBar(
            modifier = Modifier,
            containerColor = appColorPrimary,
            contentColor = MaterialTheme.colorScheme.contentColorFor(containerColor),
//            tonalElevation = NavigationBarDefaults.Elevation,
        ) {
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBarItem(
        modifier = Modifier
//            .padding(vertical = 4.dp)
        ,
//        label = {
//            Text(text = screen.title)
//        },
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = "Navigation Icon"
            )
        },
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HomeScreenPreviewLight() {
    PetPalTheme {
        HomeScreen(
            null,
            rememberNavController(),
            logout = { },
            activity = Activity(),
            true
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreviewDark() {
    AppTheme {
        HomeScreen(
            null,
            rememberNavController(),
            logout = { },
            activity = Activity(),
            true
        )
    }
}