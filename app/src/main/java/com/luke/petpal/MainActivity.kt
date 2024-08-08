package com.luke.petpal

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.luke.petpal.navigation.RootNavGraph
import com.luke.petpal.presentation.auth.AuthViewModel
import com.luke.petpal.presentation.theme.PetPalTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewmodel by viewModels<AuthViewModel>()

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

        setContent {
            PetPalTheme {
                RootNavGraph(
                    viewModel = viewmodel,
                    navController = rememberNavController(),
                    splashScreenCompleted = splashScreenCompleted
                )
            }
        }
    }
}