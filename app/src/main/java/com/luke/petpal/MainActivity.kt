package com.luke.petpal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.luke.petpal.navigation.AppNavHost
import com.luke.petpal.presentation.auth.AuthViewModel
import com.luke.petpal.presentation.theme.PetPalTheme
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