@file:OptIn(ExperimentalMaterialApi::class)

package com.luke.petpal.presentation.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luke.petpal.presentation.HomeViewModel
import com.luke.petpal.presentation.components.AdoptionPetCard
import com.luke.petpal.presentation.components.ShimmerListItem
import com.luke.petpal.presentation.theme.PetPalTheme
import com.luke.petpal.presentation.theme.appColorPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MyAdoptionPetScreen(
    homeViewModel: HomeViewModel?,
    paddingValues: PaddingValues,
    onSeeMoreClick: (String) -> Unit
) {
    Log.i("MYTAG", "Inside MyAdoptionPetScreen")
    homeViewModel?.fetchUserPets()
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(500)
        homeViewModel?.fetchUserPets()
        refreshing = false
    }

    val pullToRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = ::refresh
    )

    val userPetList = homeViewModel?.userPetList?.collectAsState(emptyList())?.value ?: emptyList()
    Log.i("MYTAG", "Pet List: $userPetList")

    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(key1 = true) {
        delay(2000)
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 24.dp,
                bottom = paddingValues.calculateBottomPadding(),
                start = 12.dp,
                end = 12.dp
            )
            .background(MaterialTheme.colorScheme.background)
            .pullRefresh(pullToRefreshState),
        contentAlignment = Alignment.TopCenter
    ) {

            LazyColumn(
                modifier = Modifier.padding()
            ) {
                item {
                    Text(
                        text = "My Posts",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                if (isLoading) {
                    items(10) {
                        ShimmerListItem(
                            isLoading = isLoading,
                            contentAfterLoading = { },
                            modifier = Modifier.padding()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else {
                    if (!refreshing) {
                        items(userPetList) { pet ->
                            Log.i("MyAdoptionPetScreen", "Inside MyPet lazy column: $pet")
                            AdoptionPetCard(
                                pet = pet,
                                onSeeMoreClick = { documentId ->
                                    documentId?.let {
                                        onSeeMoreClick(it)
                                    }
                                }
                            )
                        }
                    }
                }
            }

        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullToRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

    }

}


@Preview(showBackground = true)
@Composable
fun MyAdoptionPetScreenPreview() {
    PetPalTheme {
        MyAdoptionPetScreen(homeViewModel = null, paddingValues = PaddingValues(10.dp)) { }
    }
}