package com.luke.petpal.presentation.components

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.luke.petpal.presentation.theme.PetPalTheme

@Composable
fun PlacesAutocomplete(
    modifier: Modifier = Modifier,
    onPlaceSelected: ((Place) -> Unit),
    onError: ((Status) -> Unit)
) {
    val context = LocalContext.current

    val autocompleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val place = Autocomplete.getPlaceFromIntent(data)
                onPlaceSelected(place)
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            result.data?.let { data ->
                val status = Autocomplete.getStatusFromIntent(data)
                onError(status)
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Enter Location",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val intent = Autocomplete
                        .IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN,
                            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                        )
                        .build(context)
                    autocompleteLauncher.launch(intent)
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AutocompletePreview() {
    PetPalTheme {
        PlacesAutocomplete(
            onPlaceSelected = {  },
            onError = {  }
        )
    }
}