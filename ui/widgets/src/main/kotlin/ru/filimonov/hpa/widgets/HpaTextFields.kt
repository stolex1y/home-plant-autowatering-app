package ru.filimonov.hpa.widgets

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.filimonov.hpa.ui.common.validation.Conditions
import ru.filimonov.hpa.ui.common.validation.ValidatedProperty
import ru.filimonov.hpa.ui.common.validation.ValidationResult
import ru.filimonov.hpa.ui.widgets.R
import java.util.UUID

@Composable
fun HpaOutlinedTextFieldNullable(
    modifier: Modifier = Modifier,
    validatedProperty: ValidatedProperty<String?>,
    singleLine: Boolean = true,
    @StringRes label: Int,
    minLines: Int = 1,
    shape: Shape = MaterialTheme.shapes.large,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    trailingIcon: @Composable () -> Unit = {},
) {
    val value by validatedProperty.asState
    val validationResult by validatedProperty.validationResultAsState
    HpaOutlinedTextField(
        shape = shape,
        minLines = minLines,
        modifier = modifier,
        label = label,
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        onValueChange = { validatedProperty.set(it) },
        value = value ?: "",
        validationResult = validationResult,
        enabled = enabled,
        trailingIcon = trailingIcon,
        readOnly = readOnly,
    )
}

@Composable
fun HpaOutlinedTextField(
    modifier: Modifier = Modifier,
    validatedProperty: ValidatedProperty<String>,
    singleLine: Boolean = true,
    @StringRes label: Int,
    minLines: Int = 1,
    shape: Shape = MaterialTheme.shapes.large,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    trailingIcon: @Composable () -> Unit = {},
) {
    val value by validatedProperty.asState
    val validationResult by validatedProperty.validationResultAsState
    HpaOutlinedTextField(
        shape = shape,
        minLines = minLines,
        modifier = modifier,
        label = label,
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        onValueChange = { validatedProperty.set(it) },
        value = value,
        validationResult = validationResult,
        enabled = enabled,
        readOnly = readOnly,
        trailingIcon = trailingIcon,
    )
}

@Composable
fun <T> HpaOutlinedTextField(
    modifier: Modifier = Modifier,
    validatedProperty: ValidatedProperty<T?>,
    toObject: (String) -> T?,
    fromObject: (T?) -> String,
    singleLine: Boolean = true,
    @StringRes label: Int,
    minLines: Int = 1,
    shape: Shape = MaterialTheme.shapes.large,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    trailingIcon: @Composable () -> Unit = {},
) {
    val value by validatedProperty.asState
    val validationResult by validatedProperty.validationResultAsState
    HpaOutlinedTextField(
        keyboardOptions = keyboardOptions,
        shape = shape,
        minLines = minLines,
        modifier = modifier,
        label = label,
        singleLine = singleLine,
        onValueChange = { validatedProperty.set(toObject(it)) },
        value = fromObject(value),
        validationResult = validationResult,
        enabled = enabled,
        readOnly = readOnly,
        trailingIcon = trailingIcon,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> HpaDropdownTextField(
    modifier: Modifier = Modifier,
    items: List<T>,
    itemToText: (T) -> String,
    validatedProperty: ValidatedProperty<T>,
    singleLine: Boolean = true,
    @StringRes label: Int,
    minLines: Int = 1,
    shape: Shape = MaterialTheme.shapes.large,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
) {
    var expanded: Boolean by remember {
        mutableStateOf(false)
    }
    val selectedValue by validatedProperty.asState
    val validationResult by validatedProperty.validationResultAsState
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }) {
        HpaOutlinedTextField(
            modifier = Modifier.menuAnchor(),
            keyboardOptions = keyboardOptions,
            shape = shape,
            minLines = minLines,
            label = label,
            singleLine = singleLine,
            onValueChange = {},
            value = selectedValue?.run(itemToText) ?: "",
            validationResult = validationResult,
            enabled = enabled,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            readOnly = true,
        )
        ExposedDropdownMenu(
            modifier = Modifier,
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                val border = if (item == selectedValue)
                    Modifier.border(
                        1.dp,
                        MenuDefaults.itemColors().textColor,
                        MaterialTheme.shapes.medium
                    )
                else
                    Modifier
                DropdownMenuItem(
                    modifier = Modifier
                        .then(border)
                        .clip(MaterialTheme.shapes.large),
                    text = { Text(text = itemToText(item)) },
                    onClick = {
                        validatedProperty.set(item)
                        expanded = false
                    },
                )
            }
        }
    }
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
    shape: Shape = MaterialTheme.shapes.large,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    trailingIcon: @Composable () -> Unit = {},
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
            keyboardOptions = keyboardOptions.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }),
            enabled = enabled,
            trailingIcon = trailingIcon,
            readOnly = readOnly,
        )

        val helpText = if (validationResult.isNotValid && validationResult.errorMessageRes != null)
            stringResource(
                id = validationResult.errorMessageRes!!,
                *validationResult.errorMessageArgs
            )
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@Preview(showBackground = true)
private fun HpaDropdownTextFieldPreview() {
    MaterialTheme {
        Scaffold {
            val items = listOf(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
            )
            val property =
                ValidatedProperty<UUID>(
                    items.last(),
                    Conditions.RequiredField(R.string.required_field)
                )
            HpaDropdownTextField(
                items = items,
                itemToText = UUID::toString,
                validatedProperty = property,
                label = R.string.title
            )
        }
    }
}
