package com.luke.petpal.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.luke.petpal.data.Resource
import com.luke.petpal.navigation.ROUTE_LOGIN
import com.luke.petpal.ui.theme.PetPalTheme

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel?,
    navController: NavController
) {
    val context = LocalContext.current
    val passwordResetFlow = viewModel?.passwordResetFlow?.collectAsState()

    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel?.sendPasswordResetEmail(email) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Reset Password")
        }

        passwordResetFlow?.value?.let {
            when (it) {
                is Resource.Loading -> {
                    CircularProgressIndicator()
                }
                is Resource.Success -> {
                    Toast.makeText(context, "Password reset email sent", Toast.LENGTH_SHORT).show()
                    // Navigate back to login screen
                    LaunchedEffect(Unit) {
                        viewModel.resetPasswordResetFlow()
                        navController.navigate(ROUTE_LOGIN) {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
                is Resource.Failure -> {
                    val exception = (passwordResetFlow as Resource.Failure).exception
                    Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordPreview() {
    PetPalTheme {
        ForgotPasswordScreen(null, rememberNavController())
    }
}