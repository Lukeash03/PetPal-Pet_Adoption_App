package com.luke.petpal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import com.luke.petpal.navigation.AppNavHost
import com.luke.petpal.ui.auth.AuthViewModel
import com.luke.petpal.ui.theme.AppTheme
import com.luke.petpal.ui.theme.PetPalTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewmodel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetPalTheme {
                AppNavHost(viewmodel)
            }
        }
    }
}