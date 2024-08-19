package com.luke.petpal.presentation.screens

import android.app.DatePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.util.Calendar

@Composable
fun AddPetScreen(
    homeViewModel: HomeViewModel?,
    paddingValues: PaddingValues,
    onAddPet: () -> Unit
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

    var dob by remember { mutableStateOf(LocalDate.now()) }

    val context = LocalContext.current
    var imageStrings by remember { mutableStateOf<List<String>?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            Log.i("MYTAG", "ImagePickerLauncher: $uris")
            if (uris.isNotEmpty()) {
                imageStrings = uris.map { it.toString() }
                Log.i("MYTAG", "ImagePickerLauncher: $imageStrings")
            }
        }
    )

    val uploadStatus = homeViewModel?.uploadStatus?.collectAsState()

    val validateInput: () -> Boolean = {
        when {
            petName.isBlank() -> {
                Toast.makeText(context, "Please enter a pet name.", Toast.LENGTH_SHORT).show()
//                showToast("Please enter a pet name.")
                false
            }

            selectedSpecies.isBlank() -> {
                Toast.makeText(context, "Please select a species.", Toast.LENGTH_SHORT).show()
//                showToast("Please select a species.")
                false
            }

            petBreed.isBlank() -> {
                Toast.makeText(context, "Please enter a breed.", Toast.LENGTH_SHORT).show()
//                showToast("Please enter a breed.")
                false
            }

            petGender.isBlank() -> {
                Toast.makeText(context, "Please select a gender.", Toast.LENGTH_SHORT).show()
//                showToast("Please select a gender.")
                false
            }

            petAge.isBlank() -> {
                petAge = 0.toString()
                true
            }

            else -> true
        }
    }

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

                                Box(modifier = Modifier.weight(1f)) {
                                    var selectedDate by remember { mutableStateOf("") }

                                    val calendar = Calendar.getInstance()
                                    val year = calendar.get(Calendar.YEAR)
                                    val month = calendar.get(Calendar.MONTH)
                                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                                    val datePickerDialog = DatePickerDialog(
                                        LocalContext.current,
                                        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                                            selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
//                                            onValueChange(selectedDate)
                                        }, year, month, day
                                    )

                                    TextField(
                                        value = selectedDate,
                                        onValueChange = {},
                                        label = { Text(text = "Date of Birth") },
                                        modifier = Modifier.clickable {
                                            datePickerDialog.show()
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        readOnly = true,
                                        colors = TextFieldDefaults.colors(
                                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                                            focusedTextColor = MaterialTheme.colorScheme.onBackground.copy(0.6f),
                                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                            focusedContainerColor = MaterialTheme.colorScheme.background,
                                            focusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                        )

                                    )

//                                    PetDetailsTextFieldWithDatePicker(
//                                        value = petAge,
//                                        onValueChange = { petAge = it },
//                                        label = "Date of Birth"
//                                    )
                                }

//                                PetDetailsTextField(
//                                    value = petAge,
//                                    onValueChange = { petAge = it },
//                                    label = "Age",
//                                    modifier = Modifier
//                                        .weight(1f),
//                                    keyboardOptions = KeyboardOptions.Default.copy(
//                                        keyboardType = KeyboardType.Number
//                                    )
//                                )
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
                                LazyRow {
                                    items(imageStrings!!) { uri ->
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
                                if (validateInput()) {
//                                    val photoString = pet.photos ?: emptyList()

                                    val photoUrls: List<Uri>? = imageStrings?.map { Uri.parse(it) }
                                    val compressedPhotoUri =
                                        photoUrls?.let { compressImages(it, context = context) }
                                    val compressedPhotoStrings = compressedPhotoUri?.map { it.toString() }

                                    pet = Pet(
                                        name = petName,
                                        species = selectedSpecies,
                                        breed = petBreed,
                                        gender = petGender,
                                        age = petAge.toInt(),
                                        weight = petWeight.toInt(),
                                        color = petColor,
                                        photos = compressedPhotoStrings
                                    )
//                                    pet.photos = compressedPhotoStrings

                                    homeViewModel?.uploadPet(pet)
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
}

fun compressImages(uris: List<Uri>, context: Context): List<Uri> {
    return uris.mapNotNull { uri ->
        compressImage(uri, context)
    }
}

private fun compressImage(uri: Uri, context: Context): Uri? {
    val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
    val compressedFile = File(context.cacheDir, "compressed_image.jpg")
    val outputStream = FileOutputStream(compressedFile)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream) // Compress to 70% quality
    outputStream.flush()
    outputStream.close()
    return Uri.fromFile(compressedFile)
}

@Preview(showBackground = true)
@Composable
fun AddPetPreview() {
    PetPalTheme {
        AddPetScreen(null, paddingValues = PaddingValues(10.dp), onAddPet = {})
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AddPetPreviewDark() {
    PetPalTheme {
        AddPetScreen(null, paddingValues = PaddingValues(10.dp), onAddPet = {})
    }
}