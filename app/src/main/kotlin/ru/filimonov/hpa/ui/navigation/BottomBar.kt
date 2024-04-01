package ru.filimonov.hpa.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.filimonov.hpa.R
import ru.filimonov.hpa.ui.common.navigation.Destination
import ru.filimonov.hpa.ui.devices.DevicesScreenDestination
import ru.filimonov.hpa.ui.theme.HpaTheme

@Composable
fun HpaBottomBar(
    tabs: Array<out BottomBarDestination> = MAIN_DESTINATIONS,
    currentTab: BottomBarDestination,
    onNavigateToDestination: (Destination) -> Unit
) {
    NavigationBar(
        modifier = Modifier.navigationBarsPadding(),
    ) {
        tabs.forEach { tab ->
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unselectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                selected = tab == currentTab,
                onClick = { onNavigateToDestination(tab) },
                icon = {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(id = tab.icon),
                        contentDescription = null,
                    )
                },
                label = { Text(text = stringResource(id = tab.title)) }
            )
        }
    }
}

val MAIN_DESTINATIONS: Array<out BottomBarDestination> = arrayOf(
    DevicesScreenDestination,
)

interface BottomBarDestination : Destination {
    @get:StringRes
    val title: Int

    @get:DrawableRes
    val icon: Int
}

@Preview(showBackground = true)
@Composable
private fun BottomBarPreview() {
    val TestDestination = object : BottomBarDestination {
        override val title: Int = R.string.profile
        override val icon: Int = ru.filimonov.hpa.ui.common.R.drawable.settings
        override val path: Destination.Path
            get() = TODO("Not yet implemented")
    }
    val destinations: Array<out BottomBarDestination> = arrayOf(
        DevicesScreenDestination,
        TestDestination
    )


    HpaTheme {
        Scaffold(
            bottomBar = {
                HpaBottomBar(
                    tabs = destinations,
                    currentTab = DevicesScreenDestination,
                    onNavigateToDestination = {})
            }
        ) { innerPadding ->
            Text(
                modifier = Modifier.padding(innerPadding),
                text = "Example of a scaffold with a bottom app bar."
            )
        }
    }
}
