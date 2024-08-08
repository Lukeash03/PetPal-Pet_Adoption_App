package com.luke.petpal.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luke.petpal.domain.repository.AdoptionPetRepository
import com.luke.petpal.presentation.components.AdoptionPetCard
import com.luke.petpal.presentation.theme.PetPalTheme

@Composable
fun AdoptionScreen(
    name: String,
    paddingValues: PaddingValues
) {

    val petRepository = AdoptionPetRepository()
    val getAllData = petRepository.getAllData()
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
            items(items = getAllData) { pet ->
                AdoptionPetCard(pet = pet)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

//        Text(
//            modifier = Modifier.clickable {  },
//            text = name,
//            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
//            fontWeight = FontWeight.Bold
//        )
    }

}

@Preview(showBackground = true)
@Composable
fun AdoptionScreenPreview() {
    PetPalTheme {
        AdoptionScreen(name = "Home", paddingValues = PaddingValues(10.dp))
    }
}