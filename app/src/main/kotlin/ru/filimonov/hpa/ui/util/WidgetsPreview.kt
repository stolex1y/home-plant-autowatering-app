package ru.filimonov.hpa.ui.util

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.filimonov.hpa.ui.theme.HpaTheme
import ru.filimonov.hpa.ui.widgets.R
import ru.filimonov.hpa.widgets.HpaActionButton
import ru.filimonov.hpa.widgets.HpaFilledTextButton
import ru.filimonov.hpa.widgets.HpaOutlinedTextButton
import ru.filimonov.hpa.widgets.HpaScaffold

@Composable
@Preview(showBackground = true)
private fun HpaActionButtonPreview() {
    HpaTheme(dynamicColor = false, darkTheme = false) {
        HpaScaffold(
            actions = {
                HpaActionButton(
                    enabled = true,
                    contentDescription = R.string.button,
                    onClick = { },
                    icon = ru.filimonov.hpa.ui.common.R.drawable.settings
                )
                HpaActionButton(
                    enabled = false,
                    contentDescription = R.string.button,
                    onClick = { },
                    icon = ru.filimonov.hpa.ui.common.R.drawable.settings
                )
            },
            title = "Название"
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun HpaButtonsPreview() {
    HpaTheme(dynamicColor = false, darkTheme = false) {
        HpaScaffold(
        ) {
            Column {
                HpaOutlinedTextButton(onClick = {}, text = R.string.button)
                HpaOutlinedTextButton(enabled = false, onClick = {}, text = R.string.button)
                HpaFilledTextButton(onClick = {}, text = R.string.button)
                HpaFilledTextButton(enabled = false, onClick = {}, text = R.string.button)
            }
        }
    }
}
