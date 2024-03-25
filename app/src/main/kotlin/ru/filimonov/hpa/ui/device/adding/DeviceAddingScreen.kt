package ru.filimonov.hpa.ui.device.adding

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.filimonov.hpa.ui.common.navigation.Destination

@Composable
fun DeviceAddingScreen(

) {
}


fun NavGraphBuilder.addDeviceAddingScreen(
    onNavigateUp: () -> Unit,
) {
    composable(
        route = DeviceAddingScreenDestination.path.raw,
    ) {
        DeviceAddingScreen(

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
