package ru.filimonov.hpa.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HpaSnackbarHost(
    snackbarState: SnackbarState
) {
    SnackbarHost(hostState = snackbarState.hostState) { snackbarData ->
        val actionLabel = snackbarData.visuals.actionLabel
        val actionComposable: (@Composable () -> Unit)? = if (actionLabel != null) {
            @Composable {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(contentColor = SnackbarDefaults.actionColor),
                    onClick = { snackbarData.performAction() },
                    content = { Text(actionLabel) }
                )
            }
        } else {
            null
        }
        val dismissActionComposable: (@Composable () -> Unit)? =
            if (snackbarData.visuals.withDismissAction) {
                @Composable {
                    IconButton(
                        onClick = { snackbarData.dismiss() },
                        content = {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = null,
                            )
                        }
                    )
                }
            } else {
                null
            }
        Snackbar(
            modifier = Modifier.padding(12.dp),
            action = actionComposable,
            dismissAction = dismissActionComposable,
            actionOnNewLine = false,
            shape = MaterialTheme.shapes.medium,
            containerColor = SnackbarDefaults.color,
            contentColor = SnackbarDefaults.contentColor,
            actionContentColor = SnackbarDefaults.actionContentColor,
            dismissActionContentColor = SnackbarDefaults.dismissActionContentColor,
            content = {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = snackbarData.visuals.message
                )
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun HpaSnackbarPreview() {
    var showSnackbar: Boolean by remember {
        mutableStateOf(false)
    }
    val snackbarState = SnackbarState.rememberSnackbarState()
    MaterialTheme {
        Column {
            Button(onClick = {
                showSnackbar = true
            }) {
                Text("Show")
            }
            HpaSnackbarHost(snackbarState)
        }
    }
    if (showSnackbar) {
        snackbarState.showSnackbar(
            withDismissAction = true,
            onAction = {},
            actionLabel = "Action",
            message = "Very very very very very very very very very very very very very long text"
        )
        showSnackbar = false
    }
}
