package com.luke.petpal.presentation.screens

import android.content.res.Configuration
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
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.luke.petpal.data.models.Resource
import com.luke.petpal.domain.data.Pet
import com.luke.petpal.presentation.HomeViewModel
import com.luke.petpal.presentation.components.PetDetailsDropdownTextField
import com.luke.petpal.presentation.components.PetDetailsTextField
import com.luke.petpal.presentation.components.PetDetailsTextFieldWithDatePicker
import com.luke.petpal.presentation.theme.PetPalTheme
import com.luke.petpal.presentation.validation.PetFormEvent
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddPersonalPetScreen(
    homeViewModel: HomeViewModel?,
    onAddPet: () -> Unit,
) {

    var petName by remember { mutableStateOf("") }

    var speciesExpanded by remember { mutableStateOf(false) }
    var selectedSpecies by remember { mutableStateOf("") }
    val speciesOptions = listOf("Cat", "Dog", "Bird", "Others")

    var petBreed by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    var genderExpanded by remember { mutableStateOf(false) }
    var petGender by remember { mutableStateOf("") }

    var pickedDOB by remember { mutableStateOf(LocalDate.now()) }
    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("dd-MM-yyyy")
                .format(pickedDOB)
        }
    }
    val dateDialogState = rememberMaterialDialogState()

    var petWeight by remember { mutableStateOf("") }
    var petColor by remember { mutableStateOf("") }
    var vaccineSwitch by remember {
        mutableStateOf(
            ToggleableInfo(
                isChecked = false,
                text = "Vaccine Status"
            )
        )
    }
    var petDescription by remember { mutableStateOf("") }
    var pet: Pet

    val context = LocalContext.current
    var imageStrings by remember { mutableStateOf<List<String>?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                val newUris = uris.map { it.toString() }
                imageStrings = (imageStrings ?: emptyList()) + newUris
            }
        }
    )

    val uploadStatus = homeViewModel?.uploadStatus?.collectAsState()

    val petValidationState = homeViewModel?.petValidationState

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = ""
                        )
                    }
                },
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
//                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Add personal pet",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = paddingValues.calculateTopPadding())
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(15.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors()
            ) {
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
                            onValueChange = {
                                petName = it
                                homeViewModel?.onEvent(PetFormEvent.PetNameChanged(it))
                            },
                            isError = petValidationState?.petNameError != null,
                            label = "Pet Name",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                        if (petValidationState?.petNameError != null) {
                            Text(
                                text = petValidationState.petNameError,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }

                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // Species Dropdown
                            Column(modifier = Modifier.weight(1f)) {
                                PetDetailsDropdownTextField(
                                    value = selectedSpecies,
                                    onValueChange = {
                                        selectedSpecies = it
                                        homeViewModel?.onEvent(PetFormEvent.PetSpeciesChanged(it))
                                    },
                                    label = "Species",
                                    options = speciesOptions,
                                    expanded = speciesExpanded,
                                    onExpandedChange = { speciesExpanded = it },
                                    focusRequester = focusRequester
                                )
                                if (petValidationState?.petSpeciesError != null) {
                                    Text(
                                        text = petValidationState.petSpeciesError,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                // Breed TextField
                                PetDetailsTextField(
                                    value = petBreed,
                                    onValueChange = {
                                        petBreed = it
                                        homeViewModel?.onEvent(PetFormEvent.PetBreedChanged(it))
                                    },
                                    isError = petValidationState?.petBreedError != null,
                                    label = "Breed",
                                    modifier = Modifier
                                )
                                if (petValidationState?.petBreedError != null) {
                                    Text(
                                        text = petValidationState.petBreedError,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }

                        Row(modifier = Modifier.padding(vertical = 4.dp)) {

                            Box(modifier = Modifier.weight(1f)) {
                                Column {
                                    PetDetailsDropdownTextField(
                                        value = petGender,
                                        onValueChange = {
                                            petGender = it
                                            homeViewModel?.onEvent(
                                                PetFormEvent.PetGenderChanged(
                                                    it
                                                )
                                            )
                                        },
                                        label = "Gender",
                                        options = listOf("Male", "Female"),
                                        expanded = genderExpanded,
                                        onExpandedChange = { genderExpanded = it },
                                        focusRequester = focusRequester
                                    )
                                    if (petValidationState?.petGenderError != null) {
                                        Text(
                                            text = petValidationState.petGenderError,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.align(Alignment.End)
                                        )
                                    }
                                }
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Vaccination History",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            IconButton(
                                onClick = {  },
                                modifier = Modifier.background(
                                    MaterialTheme.colorScheme.background,
                                    RoundedCornerShape(15.dp)
                                ),

                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "")
                            }
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
                        if (imageStrings.isNullOrEmpty()) {
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
                            val pagerState = rememberPagerState(
                                pageCount = { imageStrings?.size!! }
                            )

                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                HorizontalPager(state = pagerState,
                                    key = { imageStrings!![it] }
                                ) { index ->

                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(modifier = Modifier.size(180.dp)) {
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
                                            IconButton(
                                                onClick = {
                                                    imageStrings =
                                                        imageStrings?.filter { it != imageStrings!![index] }
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
                            homeViewModel?.onEvent(PetFormEvent.Submit)
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
                            onAddPet()
                        }
                    }
                }
            }

        }

    }


}

@Preview(showBackground = true)
@Composable
fun AddPersonalPetPreview() {
    PetPalTheme {
        AddPersonalPetScreen(homeViewModel = null) { }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AddPersonalPetPreviewDark() {
    PetPalTheme {
        AddPersonalPetScreen(homeViewModel = null) { }
    }
}
