@file:OptIn(ExperimentalMaterialApi::class)

package com.luke.petpal.presentation.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luke.petpal.domain.data.Pet
import com.luke.petpal.presentation.HomeViewModel
import com.luke.petpal.presentation.components.AdoptionPetCard
import com.luke.petpal.presentation.theme.PetPalTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AdoptionScreen(
    homeViewModel: HomeViewModel?,
    paddingValues: PaddingValues,
    onSeeMoreClick: (String) -> Unit
) {

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(500)
        homeViewModel?.fetchAllPets()
        refreshing = false
    }

    val pullToRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = ::refresh
    )

    val petList = homeViewModel?.petList?.collectAsState(emptyList())?.value ?: emptyList()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = paddingValues.calculateTopPadding(),
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
            if (!refreshing) {
                items(petList) { pet ->
                    Log.i("MYTAG", "Inside lazy column: $pet")
                    AdoptionPetCard(
                        pet = pet,
                        onSeeMoreClick = { documentId ->
                            documentId?.let {
                                onSeeMoreClick(it)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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
fun AdoptionScreenPreview() {
    PetPalTheme {
        AdoptionScreen(homeViewModel = null, paddingValues = PaddingValues(10.dp)) { }
    }
}