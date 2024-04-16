package ru.filimonov.hpa.widgets

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.filimonov.hpa.ui.widgets.R

@Composable
fun HpaFilledTextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @StringRes text: Int,
    enabled: Boolean = true,
) {
    Button(
        enabled = enabled,
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        onClick = onClick
    ) {
        Text(
            text = stringResource(id = text),
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

@Composable
fun HpaOutlinedTextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @StringRes text: Int,
    enabled: Boolean = true
) {
    OutlinedButton(
        enabled = enabled,
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.5f
            )
        ),
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        onClick = onClick
    ) {
        Text(
            text = stringResource(id = text),
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun HpaFilledTextButtonPreview() {
    MaterialTheme {
        HpaFilledTextButton(enabled = true, onClick = {}, text = R.string.button)
    }
}

@Composable
@Preview(showBackground = true)
private fun HpaDisabledFilledTextButtonPreview() {
    MaterialTheme {
        HpaFilledTextButton(enabled = false, onClick = {}, text = R.string.button)
    }
}

@Composable
@Preview(showBackground = true)
private fun HpaOutlinedTextButtonPreview() {
    MaterialTheme {
        HpaOutlinedTextButton(enabled = true, onClick = {}, text = R.string.button)
    }
}

@Composable
@Preview(showBackground = true)
private fun HpaDisabledOutlinedTextButtonPreview() {
    MaterialTheme {
        HpaOutlinedTextButton(enabled = false, onClick = {}, text = R.string.button)
    }
}
