package ru.filimonov.hpa.ui.device.editing

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.filimonov.hpa.ui.common.navigation.Destination
import ru.filimonov.hpa.ui.theme.HpaTheme

@Composable
fun DeviceEditingScreen(
    deviceId: String
) {
}

@Composable
fun DeviceEditingContent() {

}

fun NavGraphBuilder.addDeviceEditingScreen(
    onNavigateUp: () -> Unit,
) {
    val destination = DeviceEditingScreenDestination("")
    composable(
        route = destination.path.raw,
        arguments = listOf(destination.deviceIdArg.toNavArgument())
    ) { navBackStackEntry ->
        val deviceId =
            navBackStackEntry.arguments?.getString(DeviceEditingScreenDestination.ARG_DEVICE_ID)!!
        DeviceEditingScreen(
            deviceId = deviceId
        )
    }
}

class DeviceEditingScreenDestination(
    deviceId: String
) : Destination {
    internal val deviceIdArg = Destination.Arg(ARG_DEVICE_ID, deviceId)

    override val path: Destination.Path =
        Destination.Path() /
                "devices" /
                deviceIdArg /
                "edit"

    companion object {
        internal const val ARG_DEVICE_ID = "deviceId"
    }
}

@Composable
@Preview(showBackground = true)
private fun DeviceEditingScreenPreview() {
    HpaTheme {
        DeviceEditingContent(
        )
    }
}
