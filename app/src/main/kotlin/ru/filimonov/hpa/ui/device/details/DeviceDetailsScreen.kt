package ru.filimonov.hpa.ui.device.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.filimonov.hpa.ui.common.navigation.Destination
import ru.filimonov.hpa.ui.theme.HpaTheme

@Composable
fun DeviceDetailsScreen(
    deviceId: String
) {
}

@Composable
fun DeviceDetailsContent() {

}

fun NavGraphBuilder.addDeviceDetailsScreen(
    onNavigateUp: () -> Unit,
) {
    val destination = DeviceDetailsScreenDestination("")
    composable(
        route = destination.path.raw,
        arguments = listOf(destination.deviceIdArg.toNavArgument())
    ) { navBackStackEntry ->
        val deviceId =
            navBackStackEntry.arguments?.getString(DeviceDetailsScreenDestination.ARG_DEVICE_ID)!!
        DeviceDetailsScreen(
            deviceId = deviceId
        )
    }
}

class DeviceDetailsScreenDestination(
    deviceId: String
) : Destination {
    internal val deviceIdArg = Destination.Arg(ARG_DEVICE_ID, deviceId)

    override val path: Destination.Path =
        Destination.Path() /
                "devices" /
                deviceIdArg

    companion object {
        internal const val ARG_DEVICE_ID = "deviceId"
    }
}

@Composable
@Preview(showBackground = true)
private fun DeviceDetailsScreenPreview() {
    HpaTheme {
        DeviceDetailsContent(
        )
    }
}
