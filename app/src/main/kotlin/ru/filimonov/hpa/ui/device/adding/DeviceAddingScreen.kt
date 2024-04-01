package ru.filimonov.hpa.ui.device.adding

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.filimonov.hpa.ui.common.navigation.Destination
import java.util.UUID

@Composable
fun DeviceAddingScreen(
    onAdding: (UUID) -> Unit,
    onCancel: () -> Unit,
) {
}


fun NavGraphBuilder.addDeviceAddingScreen(
    onAdding: (UUID) -> Unit,
    onCancel: () -> Unit,
) {
    composable(
        route = DeviceAddingScreenDestination.path.raw,
    ) {
        DeviceAddingScreen(
            onAdding = onAdding,
            onCancel = onCancel,
        )
    }
}

object DeviceAddingScreenDestination : Destination {
    override val path: Destination.Path = Destination.Path() / "devices/add"
}

@Composable
@Preview(showBackground = true)
private fun DeviceAddingScreenPreview() {

}
