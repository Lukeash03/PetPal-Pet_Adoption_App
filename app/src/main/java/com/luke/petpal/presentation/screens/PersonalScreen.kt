package com.luke.petpal.presentation.screens

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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.luke.petpal.R
import com.luke.petpal.data.models.Resource
import com.luke.petpal.navigation.Graph
import com.luke.petpal.presentation.auth.AuthViewModel
import com.luke.petpal.presentation.auth.SignUpDetailScreen
import com.luke.petpal.presentation.theme.PetPalTheme
import com.luke.petpal.presentation.theme.appColorPrimary
import com.luke.petpal.presentation.theme.appColorSecondary
import kotlinx.coroutines.launch

@Composable
fun PersonalScreen(
    viewModel: AuthViewModel?
) {

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? -> imageUri = uri }
    )

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel?.fetchProfileImageUrl()
    }

    val profileImageUrl by viewModel?.profileImageUrl?.collectAsState() ?: remember { mutableStateOf(Resource.Loading) }
//    val profileImageUrl = viewModel?.profileImageUrl?.collectAsState()

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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Profile",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = TextStyle(
                        lineHeight = 16.sp
                    )
                )
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(5.dp, appColorPrimary, CircleShape)
        ) {
            when (profileImageUrl) {
                is Resource.Loading -> {
                    // Show a loading indicator if needed
                }
                is Resource.Success -> {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current).data(data = (profileImageUrl as Resource.Success<String>).result)
                                .apply {
                                    transformations(CircleCropTransformation())
                                }
                                .build()
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
                }
                is Resource.Failure -> {
                    // Handle the error state
                    Image(
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
            }
        }

//        Box(
//            contentAlignment = Alignment.Center,
//            modifier = Modifier
//                .size(200.dp) // Size of the circular image holder
//                .clip(CircleShape)
//                .background(Color.White)
//                .border(5.dp, appColorPrimary, CircleShape)
//        ) {
//            imageUri?.let { uri ->
//                Image(
//                    painter = rememberAsyncImagePainter(
//                        ImageRequest.Builder(LocalContext.current).data(data = uri)
//                            .apply(block = fun ImageRequest.Builder.() {
//                                transformations(CircleCropTransformation())
//                            }).build()
//                    ),
//                    contentDescription = null,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .size(200.dp - (4f * 2).dp)
//                        .clickable {
//                            imagePickerLauncher.launch(
//                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
//                            )
//                        }
//                )
//            } ?: Image(
//                painter = painterResource(id = R.drawable.bx_camera),
//                contentDescription = null,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .size(150.dp - (4f * 2).dp)
//                    .clickable {
//                        imagePickerLauncher.launch(
//                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
//                        )
//                    }
//            )
//        }


        Box(
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
                                                Toast.makeText(
                                                    context,
                                                    "Updated profile image URL",
                                                    Toast.LENGTH_LONG
                                                ).show()
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

            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PersonalScreenPreviewLight() {
    PetPalTheme {
        PersonalScreen(null)
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PersonalScreenPreviewDark() {
    PetPalTheme {
        PersonalScreen(null)
    }
}