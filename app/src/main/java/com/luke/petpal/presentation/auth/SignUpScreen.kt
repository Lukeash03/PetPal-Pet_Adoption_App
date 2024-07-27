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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.luke.petpal.R
import com.luke.petpal.presentation.components.EmailInput
import com.luke.petpal.presentation.components.PasswordInput
import com.luke.petpal.presentation.components.UsernameInput
import com.luke.petpal.data.models.Resource
import com.luke.petpal.navigation.ROUTE_HOME
import com.luke.petpal.navigation.ROUTE_LOGIN
import com.luke.petpal.navigation.ROUTE_SIGNUP_DETAILED
import com.luke.petpal.presentation.theme.AppIcons
import com.luke.petpal.presentation.theme.PetPalTheme
import com.luke.petpal.presentation.theme.appColorPrimary
import com.luke.petpal.presentation.theme.appColorSecondary

@Composable
fun SignUpScreen(viewModel: AuthViewModel?, navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val emailFocusRequester = FocusRequester()
    val passwordFocusRequester = FocusRequester()
    val focusManager: FocusManager = LocalFocusManager.current

    val signupFlow = viewModel?.signUpFlow?.collectAsState()

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
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.signup_pic),
                contentDescription = "logo",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = 30.dp)
                    .graphicsLayer(
                        scaleX = 1.6f,
                        scaleY = 1.6f
                    )
            )
        }

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Sign ",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Up",
                    color = appColorPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row {
                Text(
                    text = "Create a new account to join\nour furry community",
                    color = appColorSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    style = TextStyle(
                        lineHeight = 16.sp
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(3f)
                .padding(20.dp)
        ) {
//            Spacer(modifier = Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.Center) {

                UsernameInput(
                    label = stringResource(id = R.string.label_username),
                    icon = AppIcons.Username,
                    currentValue = username,
                    keyboardActions = KeyboardActions(onNext = { emailFocusRequester.requestFocus() }),
                    onValueChange = { username = it }
                    )

                Spacer(modifier = Modifier.height(20.dp))

                EmailInput(
                    label = stringResource(id = R.string.label_email),
                    icon = AppIcons.Email,
                    currentValue = email,
                    keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() }),
                    onValueChange = { email = it },
                    focusRequester = emailFocusRequester
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

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                              viewModel?.signUp(username, email, password)
                    },
                    Modifier
                        .fillMaxWidth(fraction = 0.5f)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(appColorPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Box {
                        Text(
                            text = "Continue",
                            Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.background
                        )
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
                    text = "Already have an account?",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Login",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            navController.navigate(ROUTE_LOGIN) {
                                popUpTo(ROUTE_LOGIN)
                            }
                        }
                        .padding(5.dp)
                )
            }
        }

        signupFlow?.value?.let {
            when(it) {
                is Resource.Failure -> {
                    val context = LocalContext.current
                    Toast.makeText(context, it.exception.message, Toast.LENGTH_SHORT).show()
                }
                Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier)
                }
                is Resource.Success -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(ROUTE_SIGNUP_DETAILED) {
                            popUpTo(ROUTE_SIGNUP_DETAILED) { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SignUpPreviewLight() {
    PetPalTheme {
        SignUpScreen(null, rememberNavController())
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SignUpPreviewDark() {
    PetPalTheme {
        SignUpScreen(null, rememberNavController())
    }
}