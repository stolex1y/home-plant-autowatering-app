package ru.filimonov.hpa.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import dagger.hilt.android.AndroidEntryPoint
import ru.filimonov.hpa.domain.service.auth.UserAuthService
import ru.filimonov.hpa.ui.auth.signin.SignInScreen
import ru.filimonov.hpa.ui.auth.signout.addSignOutScreen
import ru.filimonov.hpa.ui.device.adding.DeviceAddingScreenDestination
import ru.filimonov.hpa.ui.device.adding.addDeviceAddingScreen
import ru.filimonov.hpa.ui.device.details.DeviceDetailsScreenDestination
import ru.filimonov.hpa.ui.device.details.addDeviceDetailsScreen
import ru.filimonov.hpa.ui.device.sensors.addSoilMoistureChartScreen
import ru.filimonov.hpa.ui.devices.DevicesScreenDestination
import ru.filimonov.hpa.ui.devices.addDevicesScreen
import ru.filimonov.hpa.ui.navigation.HpaNavController
import ru.filimonov.hpa.ui.navigation.rememberHpaNavController
import ru.filimonov.hpa.ui.theme.HpaTheme
import javax.inject.Inject

@AndroidEntryPoint
internal class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userAuthService: UserAuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LaunchedEffect(true) {
                userAuthService.reauthenticate()
            }
            val authState by userAuthService.getIdToken().collectAsState(initial = "")
            HpaTheme {
                val hpaNavController = rememberHpaNavController()
                if (authState == null) {
                    SignInScreen(
                        onSignInFailure = { finishAffinity() }
                    )
                } else {
                    NavHost(
                        navController = hpaNavController.navController,
                        startDestination = DevicesScreenDestination.path.raw
                    ) {
                        navGraph(navController = hpaNavController)
                    }
                }
            }
        }
    }
}

private fun NavGraphBuilder.navGraph(
    navController: HpaNavController
) {
    addSignOutScreen(onSignOut = { navController.navigateToRoute(navController.startDestination()!!) })
    addDevicesScreen(
        onNavigateToDestination = navController::navigateTo,
        onAddDevice = { navController.navigateTo(DeviceAddingScreenDestination) }
    )
    addDeviceAddingScreen(
        onAdding = { uuid -> navController.navigateTo(DeviceDetailsScreenDestination(uuid)) },
        onCancel = navController::navigateUp
    )
    addDeviceDetailsScreen()

    addSoilMoistureChartScreen(onNavigateUp = navController::navigateUp)
}
