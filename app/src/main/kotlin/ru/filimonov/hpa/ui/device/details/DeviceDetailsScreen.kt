package ru.filimonov.hpa.ui.device.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.filimonov.hpa.ui.common.navigation.Destination
import ru.filimonov.hpa.ui.theme.HpaTheme
import java.util.UUID

@Composable
fun DeviceDetailsScreen(
    deviceId: UUID
) {
}

@Composable
fun DeviceDetailsContent() {

}

fun NavGraphBuilder.addDeviceDetailsScreen() {
    val destinationSample = DeviceDetailsScreenDestination(UUID.randomUUID())
    composable(
        route = destinationSample.path.raw,
        arguments = listOf(destinationSample.deviceIdArg.toNavArgument())
    ) { navBackStackEntry ->
        val deviceId =
            navBackStackEntry.arguments?.getString(DeviceDetailsScreenDestination.ARG_DEVICE_ID)!!
                .run(UUID::fromString)
        DeviceDetailsScreen(
            deviceId = deviceId
        )
    }
}

class DeviceDetailsScreenDestination(
    deviceId: UUID
) : Destination {
    internal val deviceIdArg = Destination.Arg(ARG_DEVICE_ID, deviceId.toString())

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
