package com.luke.petpal.presentation.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
fun PetDetailsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    imeAction: ImeAction = ImeAction.Next,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = keyboardOptions.copy(imeAction = imeAction),
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailsDropdownTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester
) {
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                onExpandedChange(it)
                if (!it) {
                    focusManager.clearFocus()
                } else {
                    focusRequester.requestFocus()
                }
            }
        ) {
            TextField(
                value = value,
                onValueChange = {},
                label = { Text(label) },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .clickable {
                        onExpandedChange(true)
//                        focusRequester.requestFocus()
                    }
                    .focusRequester(focusRequester),
                shape = RoundedCornerShape(10.dp),
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

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    onExpandedChange(false)
                    focusManager.clearFocus()
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            onValueChange(option)
                            onExpandedChange(false)
                            focusManager.clearFocus()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PetDetailsTextFieldWithDatePicker(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf(value) }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
            onValueChange(selectedDate)
        }, year, month, day
    )

    TextField(
        value = selectedDate,
        onValueChange = {},
        label = { Text(text = label) },
        modifier = modifier.clickable {
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
}
