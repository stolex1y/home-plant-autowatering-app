package ru.filimonov.hpa.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HpaSnackbarHost(
    snackbarState: SnackbarState
) {
    SnackbarHost(hostState = snackbarState.hostState) {
        Snackbar(
            snackbarData = it,
            shape = MaterialTheme.shapes.large
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
        snackbarState.showSnackbar(message = "Hello")
        showSnackbar = false
    }
}
