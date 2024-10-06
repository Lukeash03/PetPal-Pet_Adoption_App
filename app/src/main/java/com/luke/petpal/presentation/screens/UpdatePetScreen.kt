@file:OptIn(ExperimentalFoundationApi::class)

package com.luke.petpal.presentation.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.luke.petpal.data.models.Resource
import com.luke.petpal.domain.data.Pet
import com.luke.petpal.presentation.HomeViewModel
import com.luke.petpal.presentation.components.PetDetailsDropdownTextField
import com.luke.petpal.presentation.components.PetDetailsTextField
import com.luke.petpal.presentation.components.PetDetailsTextFieldWithDatePicker
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun UpdatePetScreen(
    homeViewModel: HomeViewModel?,
    paddingValues: PaddingValues,
    petId: String,
    onUpdatePet: () -> Unit
) {

    Log.i("UpdatePetScreen", "Inside updatePetScreen: $petId")
    homeViewModel?.fetchPetById(petId)
    val petResource by homeViewModel!!.petById.collectAsState()

    val context = LocalContext.current
    val uploadStatus = homeViewModel?.updateStatus?.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding())
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Post for Adoption",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {


                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(15.dp),
                    elevation = CardDefaults.cardElevation(10.dp),
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
                            UpdatePetCard(homeViewModel = homeViewModel, pet = pet)
                        }

                        null -> TODO()

                    }

                }

                Spacer(modifier = Modifier.height(20.dp))

                uploadStatus?.value?.let {
                    when (it) {
                        is Resource.Failure -> {
                            LaunchedEffect(it) {
                                Toast.makeText(context, it.exception.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                        Resource.Loading -> {
                            CircularProgressIndicator(modifier = Modifier)
                        }

                        is Resource.Success -> {
                            LaunchedEffect(Unit) {
                                onUpdatePet()
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun UpdatePetCard(
    homeViewModel: HomeViewModel?,
    pet: Pet,
) {

    var petName by remember { mutableStateOf(pet.name.toString()) }

    var speciesExpanded by remember { mutableStateOf(false) }
    var selectedSpecies by remember { mutableStateOf(pet.species) }
    val speciesOptions = listOf("Cat", "Dog", "Bird", "Others")

    var petBreed by remember { mutableStateOf(pet.breed.toString()) }

    val focusRequester = remember { FocusRequester() }
    var genderExpanded by remember { mutableStateOf(false) }
    var petGender by remember { mutableStateOf(pet.gender) }

    var pickedDOB by remember {
        mutableStateOf(
            pet.dob?.let {
                Instant.ofEpochMilli(it)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            } ?: LocalDate.now()
        )
    }
    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("dd-MM-yyyy")
                .format(pickedDOB)
        }
    }

    val dateDialogState = rememberMaterialDialogState()

    var petWeight by remember { mutableStateOf(pet.weight) }
    var petColor by remember { mutableStateOf(pet.color ?: "") }
    var vaccineSwitch by remember {
        mutableStateOf(
            ToggleableInfo(
                isChecked = (pet.vaccinationStatus),
                text = "Vaccine Status"
            )
        )
    }

    var petDescription by remember { mutableStateOf(pet.description ?: "") }
    var updatedPet: Pet

    val context = LocalContext.current
    var imageStrings by remember { mutableStateOf(pet.photos) }
    var newImageStrings by remember { mutableStateOf<List<String>?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                val newUris = uris.map { it.toString() }
                imageStrings = (imageStrings ?: emptyList()) + newUris
            }
        }
    )


    val validateInput: () -> Boolean = {
        when {
            petName.isBlank() -> {
                Toast.makeText(context, "Please enter a pet name.", Toast.LENGTH_SHORT).show()
                false
            }

            selectedSpecies!!.isBlank() -> {
                Toast.makeText(context, "Please select a species.", Toast.LENGTH_SHORT).show()
                false
            }

            petBreed.isBlank() -> {
                Toast.makeText(context, "Please enter a breed.", Toast.LENGTH_SHORT).show()
                false
            }

            petGender!!.isBlank() -> {
                Toast.makeText(context, "Please select a gender.", Toast.LENGTH_SHORT).show()
                false
            }

            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight(fraction = 0.9f)
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 12.dp)
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
                isError = false,
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
                    modifier = Modifier.weight(1f)
                ) {
                    PetDetailsDropdownTextField(
                        value = selectedSpecies.toString(),
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
                    isError = false,
                    label = "Breed",
                    modifier = Modifier
                        .weight(1f)
                )
            }

            Row(modifier = Modifier.padding(vertical = 4.dp)) {

                Box(modifier = Modifier.weight(1f)) {
                    PetDetailsDropdownTextField(
                        value = petGender.toString(),
                        onValueChange = { petGender = it },
                        label = "Gender",
                        options = listOf("Male", "Female"),
                        expanded = genderExpanded,
                        onExpandedChange = { genderExpanded = it },
                        focusRequester = focusRequester
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(modifier = Modifier.weight(1f)) {
                    PetDetailsTextFieldWithDatePicker(
                        formattedDate = formattedDate,
                        onCalenderClick = { dateDialogState.show() },
                        label = "Date of Birth"
                    )
                }
            }

            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PetDetailsTextField(
                    value = petWeight.toString(),
                    onValueChange = { petWeight = it.toInt() },
                    isError = false,
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
                    isError = false,
                    label = "Color",
                    modifier = Modifier
                        .weight(1f)
                )
            }

            PetDetailsTextField(
                value = petDescription,
                onValueChange = { petDescription = it },
                isError = false,
                label = "Description",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                minLines = 4
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Vaccinated",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Switch(
                    checked = vaccineSwitch.isChecked,
                    onCheckedChange = { isChecked ->
                        vaccineSwitch = vaccineSwitch.copy(isChecked = isChecked)
                    },
                    thumbContent = {
                        Icon(
                            imageVector = if (vaccineSwitch.isChecked) {
                                Icons.Default.Check
                            } else {
                                Icons.Default.Close
                            },
                            contentDescription = null
                        )
                    }
                )
            }

        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 36.dp)
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
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
        ) {
            if (imageStrings.isNullOrEmpty() && newImageStrings.isNullOrEmpty()) {
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
                val combinedImages = imageStrings ?: (emptyList<String>() + newImageStrings) as List<String>
                val pagerState = rememberPagerState(
                    pageCount = { combinedImages.size }
                )

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    HorizontalPager(
                        state = pagerState,
                        key = { combinedImages[it] }
                    ) { index ->

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(modifier = Modifier.size(180.dp)) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = combinedImages[index]
                                    ),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .clip(RoundedCornerShape(10.dp))
                                        .size(180.dp)
                                )
                                IconButton(
                                    onClick = {
                                        if (index < (imageStrings?.size ?: 0)) {
                                            imageStrings =
                                                imageStrings?.filter { it != combinedImages[index] }
                                        } else {
                                            newImageStrings = newImageStrings?.filterIndexed { newIndex, _ ->
                                                newIndex != index - (imageStrings?.size ?: 0)
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(top = 6.dp, end = 6.dp)
                                        .align(Alignment.TopEnd)
                                        .size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Cancel,
                                        contentDescription = "Remove photo",
                                        tint = Color.White
                                    )
                                }
                            }
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

        Button(
            onClick = {
                if (validateInput()) {
                    val compressedNewPhotoStrings = newImageStrings?.let {
                        homeViewModel?.compressImages(it, context)
                    }

                    val finalPhotoStrings: List<String> =
                        (imageStrings ?: (emptyList<String>() + compressedNewPhotoStrings)) as List<String>

                    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    val localDate = LocalDate.parse(formattedDate, formatter)

                    // Convert LocalDate to Long timestamp
                    val dobTimestamp: Long =
                        localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

                    updatedPet = Pet(
                        petId = pet.petId,
                        userId = pet.userId,
                        name = petName,
                        species = selectedSpecies,
                        breed = petBreed,
                        gender = petGender,
                        dob = dobTimestamp,
                        weight = petWeight,
                        color = petColor,
                        photos = finalPhotoStrings,
                        vaccinationStatus = vaccineSwitch.isChecked,
                        description = petDescription,
                        publishDate = LocalDate.now().toEpochDay()
                    )

                    homeViewModel?.updatePet(updatedPet)
                }
            },
            Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(10.dp)
        ) {
            Box {
                Text(
                    text = "Update",
                    Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

    }

    MaterialDialog(
        dialogState = dateDialogState,
        shape = RoundedCornerShape(10.dp),
        buttons = {
            positiveButton(text = "Ok")
            negativeButton(text = "Cancel")
        }
    ) {
        datepicker(
            initialDate = LocalDate.now(),
            title = "Pick a date",
        ) {
            pickedDOB = it
        }
    }

}