package com.luke.petpal.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luke.petpal.domain.data.Pet
import com.luke.petpal.presentation.HomeViewModel
import com.luke.petpal.presentation.components.AdoptionPetCard
import com.luke.petpal.presentation.theme.PetPalTheme
import kotlinx.coroutines.flow.toList

@Composable
fun AdoptionScreen(
    homeViewModel: HomeViewModel?,
    paddingValues: PaddingValues,
    onClick: () -> Unit
) {

    val petList = homeViewModel?.petList?.collectAsState(emptyList())?.value ?: emptyList<Pet>()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {

        LazyColumn(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            items(petList) { pet ->
                AdoptionPetCard(
                    pet = pet,
                    onSeeMoreClick = {
                        onClick()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun AdoptionScreenPreview() {
    PetPalTheme {
        AdoptionScreen(homeViewModel = null, paddingValues = PaddingValues(10.dp)) {  }
    }
}