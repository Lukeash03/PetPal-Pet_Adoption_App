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
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import com.luke.petpal.presentation.HomeViewModel
import com.luke.petpal.presentation.components.AdoptionPetCard
import com.luke.petpal.presentation.theme.PetPalTheme
import com.luke.petpal.presentation.theme.appColorPrimary
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
        homeViewModel?.fetchPets()
        refreshing = false
    }

    val pullToRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = ::refresh
    )

    val petList = homeViewModel?.petList?.collectAsState(emptyList())?.value ?: emptyList()
    val selectedSpecies = homeViewModel?.selectedSpecies?.collectAsState()

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
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                FilterChip(
                    label = { Text(text = "Cat") },
                    shape = RoundedCornerShape(10.dp),
                    selected = selectedSpecies?.value == "Cat",
                    onClick = { homeViewModel?.setSpeciesFilter("Cat") },
                    trailingIcon = {
                        AnimatedVisibility(visible = selectedSpecies?.value == "Cat") {
                            IconButton(
                                modifier = Modifier.height(24.dp),
                                onClick = { homeViewModel?.setSpeciesFilter(null) }) {
                                Icon(
                                    imageVector = Icons.Outlined.Cancel,
                                    contentDescription = "Cancel selection"
                                )
                            }
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = appColorPrimary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))

                FilterChip(
                    label = { Text(text = "Dog") },
                    shape = RoundedCornerShape(10.dp),
                    selected = selectedSpecies?.value == "Dog",
                    onClick = { homeViewModel?.setSpeciesFilter("Dog") },
                    trailingIcon = {
                        AnimatedVisibility(visible = selectedSpecies?.value == "Dog") {
                            IconButton(
                                modifier = Modifier.height(24.dp),
                                onClick = { homeViewModel?.setSpeciesFilter(null) }) {
                                Icon(
                                    imageVector = Icons.Outlined.Cancel,
                                    contentDescription = "Cancel selection"
                                )
                            }
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = appColorPrimary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))

                FilterChip(
                    label = { Text(text = "Bird") },
                    shape = RoundedCornerShape(10.dp),
                    selected = selectedSpecies?.value == "Bird",
                    onClick = { homeViewModel?.setSpeciesFilter("Bird") },
                    trailingIcon = {
                        AnimatedVisibility(visible = selectedSpecies?.value == "Bird") {
                            IconButton(
                                modifier = Modifier.height(24.dp),
                                onClick = { homeViewModel?.setSpeciesFilter(null) }) {
                                Icon(
                                    imageVector = Icons.Outlined.Cancel,
                                    contentDescription = "Cancel selection"
                                )
                            }
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = appColorPrimary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))

                FilterChip(
                    label = { Text(text = "Other") },
                    shape = RoundedCornerShape(10.dp),
                    selected = selectedSpecies?.value == "Other",
                    onClick = { homeViewModel?.setSpeciesFilter("Other") },
                    trailingIcon = {
                        AnimatedVisibility(visible = selectedSpecies?.value == "Other") {
                            IconButton(
                                modifier = Modifier.height(24.dp),
                                onClick = { homeViewModel?.setSpeciesFilter(null) }) {
                                Icon(
                                    imageVector = Icons.Outlined.Cancel,
                                    contentDescription = "Cancel selection"
                                )
                            }
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = appColorPrimary
                    )
                )
            }

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