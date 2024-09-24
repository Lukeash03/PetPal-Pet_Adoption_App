package com.luke.petpal.presentation.auth

import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.luke.petpal.R
import com.luke.petpal.data.models.Resource
import com.luke.petpal.navigation.Graph
import com.luke.petpal.presentation.UserProfileViewModel
import com.luke.petpal.presentation.theme.PetPalTheme
import com.luke.petpal.presentation.theme.appColorPrimary
import kotlinx.coroutines.launch

@Composable
fun SignUpDetailScreen(
    userProfileViewModel: UserProfileViewModel?,
    navController: NavController
) {

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            Log.i("SignUpDetailScreen", "Uri = $uri")
            imageUri = uri
        }
    )

    LaunchedEffect(Unit) {
        userProfileViewModel?.fetchProfileImageUrl()
    }

    val context = LocalContext.current
//    val uploadImageResult = authViewModel?.uploadImageResult?.collectAsState()
//    val updateProfileImageResult = authViewModel?.updateProfileImageResult?.collectAsState()

    val locationState = userProfileViewModel?.locationFlow?.collectAsState()
    val uploadState = userProfileViewModel?.uploadState?.collectAsState()

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
                    text = "Complete your profile so we can know you better",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    style = TextStyle(
                        lineHeight = 16.sp
                    )
                )
            }
        }

        Column {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp) // Size of the circular image holder
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, appColorPrimary, CircleShape)
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Add a profile photo",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        userProfileViewModel?.getLocation(context)

//                        { location ->
//                            if (location != null) {
//                                val latLng = "${location.latitude}, ${location.longitude}"
//                                selectedLocation = latLng
//                            }
//                        }
                    }) {
                    Text(
                        text = "Use current location",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        userProfileViewModel?.getLocation(context)
                    }) {
                    Text(
                        text = "Choose from Maps",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Text(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .border(border = BorderStroke(1.dp, Color.Black), RoundedCornerShape(10.dp)),
                text = if (locationState?.value == null) {
                    "Location coordinates"
                } else {
                    "${locationState.value!!.latitude}, ${locationState.value!!.longitude}"
                },
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

        }

        Box(
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Button(
                    onClick = {
                        val location = locationState?.value
                        userProfileViewModel?.uploadProfileImageAndLocation(imageUri, location)
                    },
                    Modifier
                        .fillMaxWidth(fraction = 0.5f),
                    colors = ButtonDefaults.buttonColors(appColorPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Box {
                        uploadState?.value?.let {
                            when (it) {
                                is Resource.Success -> {
                                    LaunchedEffect(Unit) {
                                        navController.navigate(Graph.HOME) {
                                            popUpTo(Graph.HOME)
                                        }
                                    }
                                }

                                is Resource.Failure -> {
                                    Toast.makeText(
                                        context,
                                        it.exception.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                Resource.Loading -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }

                        if (uploadState?.value !is Resource.Loading) {
                            Text(
                                text = "Continue",
                                Modifier.padding(8.dp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "I'll do this later",
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.onBackground,
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