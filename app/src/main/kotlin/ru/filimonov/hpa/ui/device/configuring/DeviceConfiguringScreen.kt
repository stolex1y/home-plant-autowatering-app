package ru.filimonov.hpa.ui.device.configuring

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.filimonov.hpa.ui.common.navigation.Destination
import ru.filimonov.hpa.ui.theme.HpaTheme

@Composable
fun DeviceConfiguringScreen(
    deviceId: String,
) {
}

@Composable
fun DeviceConfiguringContent() {

}

fun NavGraphBuilder.addDeviceConfiguringScreen(
    onNavigateUp: () -> Unit,
) {
    val destination = DeviceConfiguringScreenDestination("")
    composable(
        route = destination.path.raw,
        arguments = listOf(destination.deviceIdArg.toNavArgument())
    ) { navBackStackEntry ->
        val deviceId =
            navBackStackEntry.arguments?.getString(DeviceConfiguringScreenDestination.ARG_DEVICE_ID)!!
        DeviceConfiguringScreen(
            deviceId = deviceId
        )
    }
}

class DeviceConfiguringScreenDestination(
    deviceId: String
) : Destination {
    internal val deviceIdArg = Destination.Arg(ARG_DEVICE_ID, deviceId)

    override val path: Destination.Path =
        Destination.Path() /
                "devices" /
                deviceIdArg /
                "configure"

    companion object {
        internal const val ARG_DEVICE_ID = "deviceId"
    }
}

@Composable
@Preview(showBackground = true)
private fun DeviceConfiguringScreenPreview() {
    HpaTheme {
        DeviceConfiguringContent(
        )
    }
}
