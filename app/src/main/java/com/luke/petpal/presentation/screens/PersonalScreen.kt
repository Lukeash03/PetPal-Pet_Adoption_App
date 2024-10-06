package com.luke.petpal.presentation.screens

import android.content.res.Configuration
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.luke.petpal.R
import com.luke.petpal.data.models.Resource
import com.luke.petpal.presentation.HomeViewModel
import com.luke.petpal.presentation.UserProfileViewModel
import com.luke.petpal.presentation.components.AdoptionPetCard
import com.luke.petpal.presentation.components.ShimmerListItem
import com.luke.petpal.presentation.theme.PetPalTheme
import com.luke.petpal.presentation.theme.appColorPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PersonalScreen(
    userProfileViewModel: UserProfileViewModel?,
    paddingValues: PaddingValues,
    onAddPetClick: () -> Unit
) {

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            Log.i("PersonalScreen", "Uri = $uri")
            imageUri = uri
        }
    )

//    val context = LocalContext.current

    LaunchedEffect(Unit) {
        userProfileViewModel?.fetchProfileImageUrl()
    }

    val userId = userProfileViewModel?.currentUser?.uid.toString()
    userProfileViewModel?.fetchUserById(userId)
    val user = userProfileViewModel?.userById?.collectAsState()
    val profileImageUrl by userProfileViewModel?.profileImageUrl?.collectAsState()
        ?: remember { mutableStateOf(Resource.Loading) }

    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(key1 = true) {
        delay(2000)
        isLoading = false
    }

    Column(
        modifier = Modifier
            .padding(top = paddingValues.calculateTopPadding())
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
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

        Column(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                shape = RoundedCornerShape(15.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors()
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier,
//                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(top = 12.dp, bottom = 12.dp, start = 12.dp)
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, appColorPrimary, CircleShape)
                        ) {
                            when (profileImageUrl) {
                                is Resource.Loading -> {
                                    // Show a loading indicator if needed
                                }

                                is Resource.Success -> {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            ImageRequest.Builder(LocalContext.current)
                                                .data(data = (profileImageUrl as Resource.Success<String>).result)
                                                .apply {
                                                    transformations(CircleCropTransformation())
                                                }
                                                .build()
                                        ),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(100.dp - (1f * 2).dp)
                                            .clickable {
                                                imagePickerLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            }
                                    )
                                }

                                is Resource.Failure -> {
                                    imageUri?.let { uri ->
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                ImageRequest.Builder(LocalContext.current)
                                                    .data(data = uri)
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
                                                        PickVisualMediaRequest(
                                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                                        )
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
                            }
                        }

                        Column {

                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp),
                                value = user?.value?.username.toString(),
                                enabled = false,
                                singleLine = true,
                                onValueChange = { },
                                shape = RoundedCornerShape(15.dp),
                                colors = TextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onBackground,
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                                    disabledIndicatorColor = Color.Transparent
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp),
                                value = user?.value?.email.toString(),
                                enabled = false,
                                singleLine = true,
                                onValueChange = { },
                                shape = RoundedCornerShape(15.dp),
                                colors = TextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onBackground,
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                                    disabledIndicatorColor = Color.Transparent
                                )
                            )
                        }
                    }

                }

            }

        }

        Box(
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Button(
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.5f),
                    onClick = {
                        userProfileViewModel?.uploadProfileImageAndLocation(imageUri, null)
                    },
                    colors = ButtonDefaults.buttonColors(appColorPrimary),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Box {
                        Text(
                            text = "Update",
                            Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.background
                        )
                    }
                }

            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = paddingValues.calculateBottomPadding()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(15.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors()
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 12.dp)
                        .clickable {

                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "My pets",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            style = TextStyle(
                                lineHeight = 16.sp
                            )
                        )

                        IconButton(
                            onClick = {
                                onAddPetClick()
                            },
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(15.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add pet",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (isLoading) {
                            items(5) {
                                ShimmerListItem(
                                    isLoading = isLoading,
                                    contentAfterLoading = { },
                                    modifier = Modifier.padding()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        } else {
//                            if (!refreshing) {
//                                items(petList) { pet ->
//                                    AdoptionPetCard(
//                                        pet = pet,
//                                        onSeeMoreClick = { documentId ->
//                                            documentId?.let {
//                                                onSeeMoreClick(it)
//                                            }
//                                        }
//                                    )
//                                }
//                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

//                    TextField(
//                        modifier = Modifier
//                            .fillMaxWidth(),
////                            .padding(horizontal = 12.dp),
//                        value = "My Pets",
//                        enabled = false,
//                        onValueChange = { },
//                        leadingIcon = {
//                            androidx.compose.material3.Icon(
//                                imageVector = Icons.Outlined.Pets,
//                                contentDescription = "Email"
//                            )
//                        },
//                        trailingIcon = {
//                            Icon(
//                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
//                                contentDescription = ""
//                            )
//                        },
//                        shape = RoundedCornerShape(10.dp),
//                        colors = TextFieldDefaults.colors(
//                            disabledTextColor = MaterialTheme.colorScheme.onBackground,
//                            disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground,
//                            disabledContainerColor = MaterialTheme.colorScheme.surface,
//                            disabledIndicatorColor = Color.Transparent,
//                            disabledTrailingIconColor = MaterialTheme.colorScheme.onBackground
//                        )
//                    )
                }

            }

        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PersonalScreenPreviewLight() {
    PetPalTheme {
        PersonalScreen(null, PaddingValues()) { }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PersonalScreenPreviewDark() {
    PetPalTheme {
        PersonalScreen(null, PaddingValues()) { }
    }
}