package ru.filimonov.hpa.widgets

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ru.filimonov.hpa.ui.common.validation.ValidatedProperty
import ru.filimonov.hpa.ui.common.validation.ValidationResult

@Composable
fun HpaOutlinedTextFieldNullable(
    modifier: Modifier = Modifier,
    validatedProperty: ValidatedProperty<String?>,
    singleLine: Boolean = true,
    @StringRes label: Int,
    minLines: Int = 1,
    shape: Shape = MaterialTheme.shapes.large
) {
    val value by validatedProperty.asState
    val validationResult by validatedProperty.validationResultAsState
    HpaOutlinedTextField(
        shape = shape,
        minLines = minLines,
        modifier = modifier,
        label = label,
        singleLine = singleLine,
        onValueChange = { validatedProperty.set(it) },
        value = value ?: "",
        validationResult = validationResult
    )
}

@Composable
fun HpaOutlinedTextField(
    modifier: Modifier = Modifier,
    validatedProperty: ValidatedProperty<String>,
    singleLine: Boolean = true,
    @StringRes label: Int,
    minLines: Int = 1,
    shape: Shape = MaterialTheme.shapes.large
) {
    val value by validatedProperty.asState
    val validationResult by validatedProperty.validationResultAsState
    HpaOutlinedTextField(
        shape = shape,
        minLines = minLines,
        modifier = modifier,
        label = label,
        singleLine = singleLine,
        onValueChange = { validatedProperty.set(it) },
        value = value,
        validationResult = validationResult
    )
}

@Composable
private fun HpaOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    validationResult: ValidationResult,
    singleLine: Boolean = true,
    @StringRes label: Int,
    minLines: Int = 1,
    shape: Shape = MaterialTheme.shapes.large
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
            value = value,
            onValueChange = { newValue: String ->
                onValueChange(newValue)
            },
            isError = validationResult.isNotValid,
            singleLine = singleLine,
            label = {
                Text(
                    text = stringResource(id = label),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            shape = shape,
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                })
        )

        val helpText = if (validationResult.isNotValid && validationResult.errorMessageRes != null)
            stringResource(id = validationResult.errorMessageRes!!)
        else
            ""

        val helpTextColor = if (validationResult.isNotValid)
            MaterialTheme.colorScheme.error
        else
            MaterialTheme.colorScheme.onSurface

        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            text = helpText,
            style = MaterialTheme.typography.bodySmall,
            color = helpTextColor
        )
    }
}
