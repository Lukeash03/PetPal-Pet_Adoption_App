package com.luke.petpal.presentation.auth

import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.luke.petpal.R
import com.luke.petpal.data.models.Resource
import com.luke.petpal.navigation.Graph
import com.luke.petpal.presentation.theme.PetPalTheme
import com.luke.petpal.presentation.theme.appColorPrimary
import com.luke.petpal.presentation.theme.appColorSecondary
import kotlinx.coroutines.launch

@Composable
fun SignUpDetailScreen(viewModel: AuthViewModel?, navController: NavController) {

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? -> imageUri = uri }
    )

    LaunchedEffect(Unit) {
        viewModel?.fetchProfileImageUrl()
    }

    val context = LocalContext.current
    val uploadImageResult = viewModel?.uploadImageResult?.collectAsState()
    val updateProfileImageResult = viewModel?.updateProfileImageResult?.collectAsState()

    var location by remember { mutableStateOf(TextFieldValue()) }
    var selectedLocation by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
//                .weight(1f)
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row {
                Text(
                    text = "Complete your profile so we can know you better",
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
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp) // Size of the circular image holder
                .clip(CircleShape)
                .background(Color.White)
                .border(5.dp, appColorPrimary, CircleShape)
        ) {
            imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(data = uri)
                            .apply(block = fun ImageRequest.Builder.() {
                                transformations(CircleCropTransformation())
                            }).build()
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(200.dp - (4f * 2).dp)
                        .clickable {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                )
            } ?: Image(
                painter = painterResource(id = R.drawable.bx_camera),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp - (4f * 2).dp)
                    .clickable {
                        imagePickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            PlacesAutocomplete(
//                modifier = Modifier.padding(top = 16.dp),
//                onPlaceSelected = { place: Place ->
//                    viewModel?.updateLocation(place)
//                },
//                onError = { status: Status ->
//
//                })
            OutlinedTextField(
                value = location,
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.placeholder),
                        contentDescription = "Choose on map",
                        Modifier
                            .size(24.dp)
                    )
                },
                shape = RoundedCornerShape(10.dp),
                onValueChange = { location = it },
                label = { Text("Enter Location") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

        }

        Box(
//            modifier = Modifier.weight(0.5f),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Button(
                    onClick = {
                        imageUri?.let { uri ->
                            viewModel?.viewModelScope?.launch {
                                viewModel.uploadProfileImage(uri)
                                viewModel.uploadImageResult.collect { uploadResult ->
                                    if (uploadResult is Resource.Success) {
                                        val imageUrl = uploadResult.result
                                        viewModel.updateProfileImageUrl(imageUrl)
                                        viewModel.updateProfileImageResult.collect { updateResult ->
                                            if (updateResult is Resource.Success) {
                                                navController.navigate(Graph.HOME) {
                                                    popUpTo(Graph.HOME)
                                                }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Failed to update profile image URL",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Failed to upload image",
                                            Toast.LENGTH_LONG
                                        )
                                    }
                                }
                            }
                        }
//                    viewModel?.signUp(username, email, password)
                    },
                    Modifier
                        .fillMaxWidth(fraction = 0.5f),
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

                Spacer(modifier = Modifier.height(16.dp))

                selectedLocation?.let {
                    Text("Selected Location: $it", style = MaterialTheme.typography.bodyLarge)
                }

                Text(
                    text = "I'll do this later",
                    textDecoration = TextDecoration.Underline,
                    color = appColorPrimary,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .clickable {
                            navController.navigate(Graph.HOME) {
                                popUpTo(Graph.HOME)
                            }
                        }
                )

            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SignUpDetailPreviewLight() {
    PetPalTheme {
        SignUpDetailScreen(null, rememberNavController())
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SignUpDetailPreviewDark() {
    PetPalTheme {
        SignUpDetailScreen(null, rememberNavController())
    }
}