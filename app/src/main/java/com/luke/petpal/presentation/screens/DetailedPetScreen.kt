@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalFoundationApi::class,
    ExperimentalFoundationApi::class, ExperimentalFoundationApi::class
)

package com.luke.petpal.presentation.screens

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.luke.petpal.data.models.Resource
import com.luke.petpal.domain.data.Pet
import com.luke.petpal.presentation.HomeViewModel
import com.luke.petpal.presentation.theme.PetPalTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DetailedPetScreen(
    homeViewModel: HomeViewModel?,
    paddingValues: PaddingValues,
    petId: String,
    onChatClick: (String) -> Unit,
) {

    homeViewModel?.fetchPetById(petId)
    val petResource by homeViewModel!!.petById.collectAsState()

    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding())
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Pet Information",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(top = 12.dp, start = 16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {


                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(15.dp),
                    elevation = CardDefaults.cardElevation(10.dp),
                    colors = CardDefaults.cardColors()
                ) {

                    when (petResource) {
                        is Resource.Failure -> {
                            LaunchedEffect(Unit) {
                                Toast.makeText(
                                    context,
                                    (petResource as Resource.Failure).exception.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        Resource.Loading -> {
                            CircularProgressIndicator(modifier = Modifier)
                        }

                        is Resource.Success -> {
                            val pet = (petResource as Resource.Success<Pet>).result
                            PetCard(homeViewModel, pet, onChatClick)
                        }

                        null -> TODO()
                    }

                }

                Spacer(modifier = Modifier.height(20.dp))

            }
        }
    }
}

@Composable
fun PetCard(homeViewModel: HomeViewModel?, pet: Pet?, onChatClick: (String) -> Unit) {

    val context = LocalContext.current
    val petId = pet?.petId.toString()

    val imageStrings = pet?.photos
    val pagerState = rememberPagerState(
        pageCount = { imageStrings?.size ?: 0 }
    )

    LaunchedEffect(pet?.userId) {
        pet?.userId?.let { userId ->
            homeViewModel?.fetchUserById(userId)
        }
    }

    val user = homeViewModel?.userById?.collectAsState()

    val isLiked = homeViewModel?.isLiked?.collectAsState()
    val likeStatus = homeViewModel?.likeStatus?.collectAsState()

    LaunchedEffect(petId) {
        // Check if the pet is already liked when the screen is loaded
        homeViewModel?.checkIfLiked(petId)
    }

    Column(
        modifier = Modifier
//                        .fillMaxSize()
            .fillMaxHeight(fraction = 0.9f)
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = pet?.name.toString() ?: "Pet Name",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = pet?.species.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Owner",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = user?.value?.username ?: "Owner name",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(state = pagerState,
                key = { imageStrings!![it] }
            ) { index ->

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Black.copy(0.5f), RoundedCornerShape(10.dp))
                        .size(180.dp)
                        .align(Alignment.Center)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = imageStrings!![index]
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(10.dp))
                            .size(180.dp)
                    )
                }

            }
            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = "Gender",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = pet?.gender.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Vaccination status",
                    textDecoration = TextDecoration.Underline,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (pet?.vaccinationStatus == true) "Completed" else "Not completed",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Description",
                textDecoration = TextDecoration.Underline,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(120.dp, 200.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = pet?.description ?: "No description to show.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
//                    minLines = 4,
                    textAlign = if (pet?.description == null) TextAlign.Center else TextAlign.Start,
                    modifier = if (pet?.description == null) {
                        Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                    } else {
                        Modifier
                            .align(Alignment.TopStart)
                            .fillMaxWidth()
                    }
//                    .align()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
//                if (pet?.publishDate != null)
                val localDate: LocalDate = LocalDate.ofEpochDay(pet?.publishDate ?: 0)
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                val formattedDate: String = localDate.format(formatter)
                Text(
                    text = "Published on",
                    textDecoration = TextDecoration.Underline,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = formattedDate,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Location",
                    textDecoration = TextDecoration.Underline,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground
                )
                val lat = user?.value?.location?.latitude
                val lng = user?.value?.location?.longitude
                Text(
                    text = "Open in maps",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.clickable {
                        Log.i("DetailedPetScreen", "Location: $lat, $lng")
                        val locationUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(Pet Location)")
                        val mapIntent = Intent(Intent.ACTION_VIEW, locationUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { },
                Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Box {
                    Text(
                        text = "Chat",
                        Modifier.padding(8.dp)
                    )
                }
            }
            Button(
                onClick = {
                    if (pet?.petId != null) {
                        homeViewModel?.toggleLike(pet.petId)
                    } else {
                        Log.i("MYTAG", "Add to like: $pet")
                    }
                },
                Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Box(
//                    modifier = Modifier.fillMaxWidth()
                ) {
                    when (likeStatus?.value) {
                        is Resource.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        is Resource.Failure -> {
                            Text(text = "An error occurred: ${(likeStatus.value as Resource.Failure).exception}")
                        }

                        else -> {
                            Text(
                                text = if (isLiked?.value == true) "Remove like" else "Add to likes",
                                Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
//            Button(
//                onClick = { },
//                Modifier.weight(1f),
//                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
//                shape = RoundedCornerShape(10.dp)
//            ) {
//                Box {
//                    Text(
//                        text = "Add to liked",
//                        Modifier.padding(8.dp)
//                    )
//                }
//            }
        }

        Spacer(modifier = Modifier.height(12.dp))

    }

}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun DetailedPetScreenPreviewLight() {
    PetPalTheme {
        PetPalTheme {
            PetCard(null, null) {  }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DetailedPetScreenPreviewDark() {
    PetPalTheme {
        PetCard(null, null) {  }
    }
}