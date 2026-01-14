package com.godzuche.dend.features.rules.impl.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddManuallyDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (number: String, name: String?) -> Unit
) {
    var number by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val isNumberValid = number.isNotBlank() && number.length >= 3 // Simple validation

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(number, name.ifBlank { null }) // Pass null if name is blank
                    onDismissRequest()
                },
                enabled = isNumberValid // "Add" button is only enabled when number is valid
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        title = { Text("Add Number Manually") },
        text = {
            Column {
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("Phone Number*") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name (Optional)") },
                    singleLine = true
                )
            }
        }
    )
}
