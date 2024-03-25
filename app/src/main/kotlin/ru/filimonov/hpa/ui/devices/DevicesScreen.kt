package ru.filimonov.hpa.ui.devices

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.filimonov.hpa.ui.auth.AuthViewModel
import ru.filimonov.hpa.ui.common.navigation.Destination
import ru.filimonov.hpa.ui.theme.HpaTheme

@Composable
fun DevicesScreen(
    onSignOut: () -> Unit,
) {
    DevicesContent(
        onSignOut = onSignOut,
    )
}

@Composable
fun DevicesContent(
    onSignOut: () -> Unit,
) {
    Column {
        Text("Мои устройства")
        Button(onClick = onSignOut) {
            Text("Выйти")
        }
    }
}

fun NavGraphBuilder.addDevicesScreen(
    onNavigateUp: () -> Unit,
    onSignOut: () -> Unit,
) {
    composable(
        route = DevicesScreenDestination.path.raw,
    ) {
        DevicesScreen(
            onSignOut = onSignOut
        )
    }
}

object DevicesScreenDestination : Destination {
    override val path = Destination.Path() / "devices"
}

@Composable
@Preview(showBackground = true)
private fun DevicesScreenPreview() {
    HpaTheme {
        DevicesContent(
            onSignOut = {}
        )
    }
}
