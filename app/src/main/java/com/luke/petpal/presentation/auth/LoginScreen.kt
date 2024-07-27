package com.luke.petpal.presentation.auth

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.luke.petpal.R
import com.luke.petpal.presentation.components.EmailInput
import com.luke.petpal.presentation.components.PasswordInput
import com.luke.petpal.data.models.Resource
import com.luke.petpal.navigation.ROUTE_FORGOT_PASSWORD
import com.luke.petpal.navigation.ROUTE_HOME
import com.luke.petpal.navigation.ROUTE_SIGNUP
import com.luke.petpal.presentation.theme.AppIcons
import com.luke.petpal.presentation.theme.PetPalTheme
import com.luke.petpal.presentation.theme.appColorPrimary

@Composable
fun LoginScreen(viewModel: AuthViewModel?, navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginFlow = viewModel?.loginFlow?.collectAsState()

    val passwordFocusRequester = FocusRequester()
    val focusManager: FocusManager = LocalFocusManager.current

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Box(
            modifier = Modifier
                .weight(3f),
//                .padding(bottom = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.login_pic_large_2),
                contentDescription = "logo",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = 10.dp)
//                    .graphicsLayer(
//                        scaleX = 1.05f,
//                        scaleY = 1.05f
//                    )
            )
        }

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
//                .background(
//                    MaterialTheme.colorScheme.background.copy(0.5f),
//                    RoundedCornerShape(15.dp, 15.dp)
//                ),
        ) {
            Row(
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Welcome ",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Back!",
                    color = appColorPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
            ) {
                Text(
                    text = "Continue your PetPal journey with us",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(3f)
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp),
        ) {
//            Spacer(modifier = Modifier.height(20.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                EmailInput(
                    label = stringResource(id = R.string.label_email),
                    icon = AppIcons.Email,
                    currentValue = email,
                    keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() }),
                    onValueChange = { email = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                PasswordInput(
                    label = stringResource(id = R.string.label_password),
                    icon = AppIcons.Password,
                    currentValue = password,
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    onValueChange = { password = it },
                    focusRequester = passwordFocusRequester
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "forgot password?",
                    Modifier
                        .padding(start = 8.dp)
                        .clickable {
                            navController.navigate(ROUTE_FORGOT_PASSWORD) {
                                popUpTo(ROUTE_FORGOT_PASSWORD) { inclusive = true }
                            }
                        },
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        viewModel?.login(email, password)
                    },
                    Modifier
                        .fillMaxWidth(fraction = 0.5f)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(appColorPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Box {
                        Text(text = "Log In", Modifier.padding(8.dp))
                    }
                }
            }
        }

        Box(
            modifier = Modifier.weight(0.5f),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account?",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Sign Up",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            navController.navigate(ROUTE_SIGNUP) {
                                popUpTo(ROUTE_SIGNUP)
                            }
                        }
                        .padding(5.dp)
                )
            }
        }

        loginFlow?.value?.let {
            when(it) {
                is Resource.Failure -> {
                    val context = LocalContext.current
                    LaunchedEffect(it) {
                        Toast.makeText(context, it.exception.message, Toast.LENGTH_SHORT).show()
                    }
                }
                Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier)
                }
                is Resource.Success -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(ROUTE_HOME) {
                            popUpTo(ROUTE_HOME) { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun LoginPreviewLight() {
    PetPalTheme {
        LoginScreen(null, rememberNavController())
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginPreviewDark() {
    PetPalTheme {
        LoginScreen(null, rememberNavController())
    }
}