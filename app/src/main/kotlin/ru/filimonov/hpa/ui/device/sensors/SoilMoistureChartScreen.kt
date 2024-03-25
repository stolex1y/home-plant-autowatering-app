package ru.filimonov.hpa.ui.device.sensors

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.filimonov.hpa.ui.common.navigation.Destination
import ru.filimonov.hpa.ui.theme.HpaTheme

@Composable
fun SoilMoistureChartScreen(
    deviceId: String
) {
}

@Composable
fun SoilMoistureChartContent() {

}

fun NavGraphBuilder.addSoilMoistureChartScreen(
    onNavigateUp: () -> Unit,
) {
    val destination = SoilMoistureChartScreenDestination("")
    composable(
        route = destination.path.raw,
        arguments = listOf(destination.deviceIdArg.toNavArgument())
    ) { navBackStackEntry ->
        val deviceId =
            navBackStackEntry.arguments?.getString(SoilMoistureChartScreenDestination.ARG_DEVICE_ID)!!
        SoilMoistureChartScreen(
            deviceId = deviceId
        )
    }
}

class SoilMoistureChartScreenDestination(
    deviceId: String
) : Destination {
    internal val deviceIdArg = Destination.Arg(ARG_DEVICE_ID, deviceId)

    override val path: Destination.Path =
        Destination.Path() /
                "devices" /
                deviceIdArg /
                "sensors" /
                "soil" /
                "moisture"

    companion object {
        internal const val ARG_DEVICE_ID = "deviceId"
    }
}

@Composable
@Preview(showBackground = true)
private fun SoilMoistureChartScreenPreview() {
    HpaTheme {
        SoilMoistureChartContent(
        )
    }
}
