package com.luke.petpal.presentation.screens

import android.content.res.Configuration
import android.net.Uri
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.luke.petpal.domain.data.Pet
import com.luke.petpal.presentation.HomeViewModel
import com.luke.petpal.presentation.components.PetDetailsDropdownTextField
import com.luke.petpal.presentation.components.PetDetailsTextField
import com.luke.petpal.presentation.theme.PetPalTheme

@Composable
fun AddPetScreen(
    homeViewModel: HomeViewModel?,
    paddingValues: PaddingValues
) {

    var petName by remember { mutableStateOf("") }
    var speciesExpanded by remember { mutableStateOf(false) }
    var selectedSpecies by remember { mutableStateOf("") }
    val speciesOptions = listOf("Cat", "Dog", "Bird", "Others")
    var petBreed by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    var genderExpanded by remember { mutableStateOf(false) }
    var petGender by remember { mutableStateOf("") }
    var petAge by remember { mutableStateOf("") }
    var petWeight by remember { mutableStateOf("") }
    var petColor by remember { mutableStateOf("") }
    var pet: Pet

    val context = LocalContext.current
    var imageUris by remember { mutableStateOf<List<Uri>?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                imageUris = uris
//                onImagesSelected(uris)
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding())
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        Column {
            Text(
                text = "Post for Adoption",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Column(
                modifier = Modifier
                    .fillMaxSize(),
//                    .padding(top = paddingValues.calculateTopPadding()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {


                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(15.dp),
                    elevation = CardDefaults.cardElevation(10.dp),
                    colors = CardDefaults.cardColors(
//                    containerColor = cardColorPrimaryLight,
//                    contentColor = cardColorPrimaryLight
                    )
                ) {
                    Column(
                        modifier = Modifier
//                        .fillMaxSize()
                            .fillMaxHeight(fraction = 0.9f)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .fillMaxWidth(),
                            text = "Enter Pet details",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Start
                        )

                        Column {

                            PetDetailsTextField(
                                value = petName,
                                onValueChange = { petName = it },
                                label = "Pet Name",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )

                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Species Dropdown
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    PetDetailsDropdownTextField(
                                        value = selectedSpecies,
                                        onValueChange = { selectedSpecies = it },
                                        label = "Species",
                                        options = speciesOptions,
                                        expanded = speciesExpanded,
                                        onExpandedChange = { speciesExpanded = it },
                                        focusRequester = focusRequester
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Breed TextField
                                PetDetailsTextField(
                                    value = petBreed,
                                    onValueChange = { petBreed = it },
                                    label = "Breed",
                                    modifier = Modifier
                                        .weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    PetDetailsDropdownTextField(
                                        value = petGender,
                                        onValueChange = { petGender = it },
                                        label = "Gender",
                                        options = listOf("Male", "Female"),
                                        expanded = genderExpanded,
                                        onExpandedChange = { genderExpanded = it },
                                        focusRequester = focusRequester
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                PetDetailsTextField(
                                    value = petAge,
                                    onValueChange = { petAge = it },
                                    label = "Age",
                                    modifier = Modifier
                                        .weight(1f),
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PetDetailsTextField(
                                    value = petWeight,
                                    onValueChange = { petWeight = it },
                                    label = "Weight",
                                    modifier = Modifier
                                        .weight(1f),
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number
                                    )
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                PetDetailsTextField(
                                    value = petColor,
                                    onValueChange = { petColor = it },
                                    label = "Color",
                                    modifier = Modifier
                                        .weight(1f)
                                )
                            }
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .requiredHeight(180.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(10.dp)
                                )
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    imagePickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                    )
                                }
                        ) {
                            if (imageUris.isNullOrEmpty()) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "",
                                        modifier = Modifier
                                            .size(100.dp),
                                        tint = MaterialTheme.colorScheme.onBackground.copy(0.6f)
                                    )
                                    Text(
                                        text = "Add photo",
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                }
                            } else {
                                LazyRow {
                                    items(imageUris!!) { uri ->
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                model = uri
                                            ),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
//                                            .padding(4.dp)
                                                .size(150.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                pet = Pet(
                                    name = petName,
                                    species = selectedSpecies,
                                    breed = petBreed,
                                    gender = petGender,
                                    age = petAge.toInt(),
                                    weight = petWeight.toInt(),
                                    color = petColor,
                                    photos = imageUris
                                )
                                homeViewModel?.uploadPet(pet)
                            },
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Box {
                                Text(
                                    text = "Add Pet",
                                    Modifier.padding(8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPetPreview() {
    PetPalTheme {
        AddPetScreen(null, paddingValues = PaddingValues(10.dp))
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AddPetPreviewDark() {
    PetPalTheme {
        AddPetScreen(null, paddingValues = PaddingValues(10.dp))
    }
}