package com.luke.petpal.presentation.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luke.petpal.presentation.theme.PetPalTheme
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccinationEntryDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, Long, Boolean) -> Unit // Pass the vaccine name, doctor name, date, and reminder status
) {
    var vaccineName by remember { mutableStateOf("") }
    var doctorName by remember { mutableStateOf("") }
    var vaccinationDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var setReminder by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text(
                    text = "Add Vaccination Entry",
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                Column {
                    // Vaccine Name Input
                    OutlinedTextField(
                        value = vaccineName,
                        onValueChange = { vaccineName = it },
                        label = { Text("Vaccine Name") },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Doctor's Name Input (Optional)
                    OutlinedTextField(
                        value = doctorName,
                        onValueChange = { doctorName = it },
                        label = { Text("Doctor's Name (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Date of Vaccination
                    Text(
                        text = "Vaccination Date: ${vaccinationDate.toString()}",
                        modifier = Modifier.clickable { showDatePicker = true }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reminder Switch
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Set Reminder")
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = setReminder,
                            onCheckedChange = { setReminder = it }
                        )
                    }

                    // DatePicker Dialog
                    if (showDatePicker) {
                        com.luke.petpal.presentation.components.DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            onDateChange = { date ->
                                vaccinationDate = date
                                showDatePicker = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (vaccineName.isNotEmpty()) {
                            onSave(vaccineName, doctorName, vaccinationDate, setReminder)
                            onDismiss()
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateChange: (Long) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            val date = selectedDate
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
            onDateChange(date)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

@Preview(showBackground = true)
@Composable
fun VaccinationEntryDialogPreview() {
    PetPalTheme {
        VaccinationEntryDialog(
            showDialog = true,
            onDismiss = {  },
            onSave = { _, _, _, _ -> }
        )
    }
}