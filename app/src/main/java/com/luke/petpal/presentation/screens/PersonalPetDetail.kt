package com.luke.petpal.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.luke.petpal.domain.data.Pet
import com.luke.petpal.presentation.theme.PetPalTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PersonalPetDetail(
    pet: Pet?
) {

    val imageStrings = pet?.photos
    val pagerState = rememberPagerState(
        pageCount = { imageStrings?.size ?: 0 }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        Box(modifier = Modifier.fillMaxWidth()) {
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
    }
}

@Preview(showBackground = true)
@Composable
fun PersonalPetDetailPreview() {
    PetPalTheme {
        PersonalPetDetail(pet = null)
    }
}