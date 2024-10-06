package com.luke.petpal

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.luke.petpal.navigation.RootNavGraph
import com.luke.petpal.presentation.HomeViewModel
import com.luke.petpal.presentation.theme.PetPalTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            // Location permissions granted
        } else {
            // Handle permission denied
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var splashScreenCompleted by mutableStateOf(false)

        val splashScreen = installSplashScreen()
        splashScreen.apply {
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.7f,
                    0.0f
                )
                zoomX.interpolator = OvershootInterpolator()
                zoomX.duration = 500L

                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.7f,
                    0.0f
                )
                zoomY.interpolator = OvershootInterpolator()
                zoomY.duration = 500L

                zoomX.start()
                zoomY.start()

                zoomX.doOnEnd {
                    screen.remove()
                    splashScreenCompleted = true
                }
                zoomY.doOnEnd {
                    screen.remove()
                    splashScreenCompleted = true
                }
            }
        }

//        enableEdgeToEdge()
        setContent {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val isDarkModeActive by homeViewModel.isDarkModeActive.collectAsState(initial = false)
            PetPalTheme(
                darkTheme = isDarkModeActive
            ) {
                RootNavGraph(
                    navController = rememberNavController(),
                    splashScreenCompleted = splashScreenCompleted,
                    isDarkModeActive = isDarkModeActive
                )
            }
        }

        requestLocationPermissions()
    }

    private fun requestLocationPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}