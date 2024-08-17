package com.luke.petpal.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luke.petpal.presentation.theme.PetPalTheme

@Composable
fun AdoptionScreen(
    name: String,
    paddingValues: PaddingValues,
    onClick: () -> Unit
) {

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
//            items(items = getAllData) { pet ->
//                AdoptionPetCard(pet = pet)
//                Spacer(modifier = Modifier.height(8.dp))
//            }
        }

//        FloatingActionButton(onClick = { showDialog = true },
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(16.dp)
//        ) {
//            Icon(imageVector = Icons.Default.Add, contentDescription = "Add pet")
//        }
//
//        if (showDialog) {
//            AddPetDialog(
//                onDismiss = { showDialog = false },
//                onAddPet = { name, breed, age ->
//                    addPet(name, breed, age)
//                    showDialog = false
//                }
//            )
//        }
    }

}

@Preview(showBackground = true)
@Composable
fun AdoptionScreenPreview() {
    PetPalTheme {
        AdoptionScreen(name = "Home", paddingValues = PaddingValues(10.dp)) {  }
    }
}