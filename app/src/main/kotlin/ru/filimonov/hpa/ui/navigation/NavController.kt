package ru.filimonov.hpa.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ru.filimonov.hpa.ui.auth.signout.SignOutScreenDestination
import ru.filimonov.hpa.ui.common.navigation.Destination

@Composable
fun rememberHpaNavController(
    navController: NavHostController = rememberNavController()
): HpaNavController = remember(navController) {
    HpaNavController(navController)
}

@Stable
class HpaNavController(
    val navController: NavHostController
) {
    private val currentRoute: String?
        get() = navController.currentDestination?.route

    fun navigateToRoute(route: String) {
        if (route != currentRoute) {
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
            }
        }
    }

    fun startDestination(): String? {
        return navController.graph.startDestinationRoute
    }

    fun navigateTo(destination: Destination) {
        navigateToRoute(destination.path.filled)
    }

    fun navigateUp() {
        navController.navigateUp()
    }

    fun signOut() {
        navigateTo(SignOutScreenDestination)
    }
}
