package com.luke.petpal.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.luke.petpal.data.models.Resource
import com.luke.petpal.navigation.AuthScreen
import com.luke.petpal.presentation.theme.PetPalTheme
import com.luke.petpal.presentation.theme.appColorPrimary

@OptIn(ExperimentalMaterial3Api::class)
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
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.onBackground.copy(0.5f),
                    RoundedCornerShape(10.dp)
                ),
            colors = TextFieldDefaults.colors(
                focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = ShapeDefaults.Medium

        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel?.sendPasswordResetEmail(email)
            },
            Modifier
                .fillMaxWidth(fraction = 0.4f)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(appColorPrimary),
            shape = RoundedCornerShape(10.dp)
        ) {
            Box {
                Text(
                    text = "Reset password",
//                    Modifier.padding(8.dp)
                )
            }
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
                        navController.navigate(AuthScreen.Login.route) {
                            popUpTo(AuthScreen.Login.route) { inclusive = true }
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