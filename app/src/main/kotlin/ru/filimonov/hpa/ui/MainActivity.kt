package ru.filimonov.hpa.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
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
import ru.filimonov.hpa.ui.device.editing.DeviceEditingScreenDestination
import ru.filimonov.hpa.ui.device.editing.addDeviceEditingScreen
import ru.filimonov.hpa.ui.device.sensors.addSoilMoistureChartScreen
import ru.filimonov.hpa.ui.devices.DevicesScreenDestination
import ru.filimonov.hpa.ui.devices.addDevicesScreen
import ru.filimonov.hpa.ui.navigation.HpaNavController
import ru.filimonov.hpa.ui.navigation.rememberHpaNavController
import ru.filimonov.hpa.ui.plant.details.PlantDetailsScreenDestination
import ru.filimonov.hpa.ui.plant.details.addPlantDetailsScreen
import ru.filimonov.hpa.ui.plant.editing.PlantEditingScreenDestination
import ru.filimonov.hpa.ui.plant.editing.addPlantEditingScreen
import ru.filimonov.hpa.ui.plants.addPlantsScreen
import ru.filimonov.hpa.ui.theme.HpaTheme
import ru.filimonov.hpa.widgets.LocalSnackbarState
import ru.filimonov.hpa.widgets.SnackbarState
import javax.inject.Inject

@AndroidEntryPoint
internal class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userAuthService: UserAuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val authState by userAuthService.getIdToken().collectAsState(initial = "")
            HpaTheme {
                val hpaNavController = rememberHpaNavController()
                if (authState == null) {
                    SignInScreen(
                        onSignInFailure = { finishAffinity() }
                    )
                } else {
                    CompositionLocalProvider(LocalSnackbarState provides SnackbarState.rememberSnackbarState()) {
                        NavHost(
                            navController = hpaNavController.navController,
                            startDestination = DevicesScreenDestination.path.raw
                        ) {
                            navGraph(
                                navController = hpaNavController,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun NavGraphBuilder.navGraph(
    navController: HpaNavController,
) {
    addSignOutScreen(onSignOut = { navController.navigateToRoute(navController.startDestination()!!) })
    addDevicesScreen(
        onNavigateToDestination = navController::navigateTo,
        onAddDevice = { navController.navigateTo(DeviceAddingScreenDestination) },
        onNavigateToDevice = { uuid -> navController.navigateTo(DeviceDetailsScreenDestination(uuid)) },
    )
    addDeviceAddingScreen(
        onAdded = { uuid -> navController.navigateTo(DeviceDetailsScreenDestination(uuid)) },
        onCancelAdding = navController::navigateUp
    )
    addDeviceDetailsScreen(
        onNavigateUp = navController::navigateUp,
        onNavigateToDestination = navController::navigateTo,
        onEditDevice = { uuid -> navController.navigateTo(DeviceEditingScreenDestination(uuid)) },
        onDeleted = navController::navigateUp,
        onNavigateToPlant = { uuid -> navController.navigateTo(PlantDetailsScreenDestination(uuid)) },
    )
    addSoilMoistureChartScreen(onNavigateUp = navController::navigateUp)
    addDeviceEditingScreen(
        onNavigateUp = navController::navigateUp,
        onNavigateToDestination = navController::navigateTo,
        onDeleted = navController::navigateUp,
        onEdited = navController::navigateUp,
        onCancelEditing = navController::navigateUp,
    )
    addPlantsScreen(
        onNavigateToDestination = navController::navigateTo,
        onAddPlant = { /* TODO("переход на экран добавления растения") */ },
        onNavigateToPlant = { uuid -> navController.navigateTo(PlantDetailsScreenDestination(uuid)) },
    )
    addPlantDetailsScreen(
        onNavigateUp = navController::navigateUp,
        onNavigateToDestination = navController::navigateTo,
        onEditPlant = { uuid -> navController.navigateTo(PlantEditingScreenDestination(uuid)) },
        onDeleted = navController::navigateUp,
    )
    addPlantEditingScreen(
        onNavigateUp = navController::navigateUp,
        onNavigateToDestination = navController::navigateTo,
        onEdited = navController::navigateUp,
        onCancelEditing = navController::navigateUp,
    )
}
