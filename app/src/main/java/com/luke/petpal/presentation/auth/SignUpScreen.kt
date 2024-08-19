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
import androidx.compose.ui.text.style.TextDecoration
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
import com.luke.petpal.navigation.AuthScreen
import com.luke.petpal.presentation.auth.validation.RegistrationFormEvent
import com.luke.petpal.presentation.theme.AppIcons
import com.luke.petpal.presentation.theme.PetPalTheme
import com.luke.petpal.presentation.theme.appColorPrimary

@Composable
fun SignUpScreen(viewModel: AuthViewModel?, navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val emailFocusRequester = FocusRequester()
    val passwordFocusRequester = FocusRequester()
    val focusManager: FocusManager = LocalFocusManager.current

    val signupFlow = viewModel?.signUpFlow?.collectAsState()

    val state = viewModel?.state
    val context = LocalContext.current
    LaunchedEffect(context) {
        viewModel?.validationEvents?.collect { event ->
            when (event) {
                is AuthViewModel.ValidationEvent.Success -> {
                    viewModel.signUp(username, email, password)
                }
            }
        }
    }

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
                .weight(2.5f),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.sign_up_),
                contentDescription = "logo",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = 10.dp)
                    .graphicsLayer(
                        scaleX = 1.1f,
                        scaleY = 1.1f
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
                    color = MaterialTheme.colorScheme.onBackground,
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
                    color = MaterialTheme.colorScheme.onBackground,
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

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {

                UsernameInput(
                    label = stringResource(id = R.string.label_username),
                    icon = AppIcons.Username,
                    currentValue = username,
                    isError = state?.usernameError != null,
                    keyboardActions = KeyboardActions(onNext = { emailFocusRequester.requestFocus() }),
                    onValueChange = {
                        username = it
                        viewModel?.onEvent(RegistrationFormEvent.UsernameChanged(it))
                    }
                )
                if (state?.usernameError != null) {
                    Text(
                        text = state.usernameError,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.End)
                    )
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                }

                EmailInput(
                    label = stringResource(id = R.string.label_email),
                    icon = AppIcons.Email,
                    currentValue = email,
                    isError = state?.emailError != null,
                    keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() }),
                    onValueChange = {
                        email = it
                        viewModel?.onEvent(RegistrationFormEvent.EmailChanged(it))
                    },
                    focusRequester = emailFocusRequester
                )
                if (state?.emailError != null) {
                    Text(
                        text = state.emailError,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.End)
                    )
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                }

                PasswordInput(
                    label = stringResource(id = R.string.label_password),
                    icon = AppIcons.Password,
                    currentValue = password,
                    isError = state?.passwordError != null,
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    onValueChange = {
                        password = it
                        viewModel?.onEvent(RegistrationFormEvent.PasswordChanged(it))
                    },
                    focusRequester = passwordFocusRequester
                )
                if (state?.passwordError != null) {
                    Text(
                        text = state.passwordError,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.End)
                    )
                }

                if (state?.passwordError == null) {
                    Spacer(modifier = Modifier.height(30.dp))
                }

                Button(
                    onClick = {
//                        viewModel?.signUp(username, email, password)
                        viewModel?.onEvent(RegistrationFormEvent.Submit)
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
                            color = MaterialTheme.colorScheme.onBackground
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
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Login",
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            navController.navigate(AuthScreen.Login.route) {
                                popUpTo(AuthScreen.Login.route)
                            }
                        }
                        .padding(5.dp)
                )
            }
        }

        signupFlow?.value?.let {
            when (it) {
                is Resource.Failure -> {
                    Toast.makeText(context, it.exception.message, Toast.LENGTH_SHORT).show()
                }

                Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier)
                }

                is Resource.Success -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(AuthScreen.SignUpDetailed.route) {
                            popUpTo(AuthScreen.SignUpDetailed.route) { inclusive = true }
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