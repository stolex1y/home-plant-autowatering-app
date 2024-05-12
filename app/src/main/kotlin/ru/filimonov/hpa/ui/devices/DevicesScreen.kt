package ru.filimonov.hpa.ui.devices

import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.filimonov.hpa.R
import ru.filimonov.hpa.ui.common.navigation.Destination
import ru.filimonov.hpa.ui.common.udf.SimpleLoadingUiState
import ru.filimonov.hpa.ui.common.utils.LaunchedEffectEveryResuming
import ru.filimonov.hpa.ui.common.utils.collectAsStateWhileResumed
import ru.filimonov.hpa.ui.devices.DevicesViewModel.Data
import ru.filimonov.hpa.ui.devices.DevicesViewModel.Event
import ru.filimonov.hpa.ui.devices.model.Device
import ru.filimonov.hpa.ui.devices.model.DeviceWithPlant
import ru.filimonov.hpa.ui.devices.model.DeviceWithoutPlant
import ru.filimonov.hpa.ui.devices.model.Plant
import ru.filimonov.hpa.ui.navigation.BottomBarDestination
import ru.filimonov.hpa.ui.navigation.HpaBottomBar
import ru.filimonov.hpa.ui.theme.HpaTheme
import ru.filimonov.hpa.widgets.HpaCardWithPhoto
import ru.filimonov.hpa.widgets.HpaCircularIconButton
import ru.filimonov.hpa.widgets.HpaLoadingScreen
import ru.filimonov.hpa.widgets.HpaScaffold
import ru.filimonov.hpa.widgets.HpaSnackbarHost
import ru.filimonov.hpa.widgets.LocalSnackbarState
import ru.filimonov.hpa.widgets.SnackbarState
import java.net.URI
import java.util.UUID

fun NavGraphBuilder.addDevicesScreen(
    onNavigateToDestination: (Destination) -> Unit,
    onAddDevice: () -> Unit,
    onNavigateToDevice: (UUID) -> Unit,
) {
    composable(
        route = DevicesScreenDestination.path.raw,
    ) {
        DevicesScreen(
            onNavigateToDestination = onNavigateToDestination,
            onAddDevice = onAddDevice,
            onNavigateToDevice = onNavigateToDevice,
        )
    }
}

object DevicesScreenDestination : Destination, BottomBarDestination {
    override val path = Destination.Path() / "devices"
    override val title: Int = R.string.devices
    override val icon: Int = R.drawable.watering_plant
}

@Composable
fun DevicesScreen(
    onNavigateToDestination: (Destination) -> Unit,
    onAddDevice: () -> Unit,
    viewModel: DevicesViewModel = hiltViewModel(),
    onNavigateToDevice: (UUID) -> Unit,
) {
    LaunchedEffectEveryResuming(key1 = viewModel) {
        viewModel.dispatchEvent(Event.Reload)
    }

    val data by viewModel.data.collectAsStateWhileResumed()
    val state by viewModel.state.collectAsStateWhileResumed()

    Content(
        prevState = remember(state) { viewModel.prevState },
        state = state,
        data = data,
        onAddDevice = onAddDevice,
        onLoadPhoto = viewModel::loadDevicePhoto,
        onNavigateToDestination = onNavigateToDestination,
        onNavigateToDevice = onNavigateToDevice,
    )
}

@Composable
private fun Content(
    prevState: SimpleLoadingUiState,
    state: SimpleLoadingUiState,
    data: Data,
    onNavigateToDestination: (Destination) -> Unit,
    onAddDevice: () -> Unit,
    onLoadPhoto: (URI?) -> Flow<Bitmap?>,
    onNavigateToDevice: (UUID) -> Unit,
) {
    val snackbarState = LocalSnackbarState.current
    HpaScaffold(
        snackbarHost = { HpaSnackbarHost(snackbarState = snackbarState) },
        floatingActionButton = {
            HpaCircularIconButton(
                onClick = onAddDevice,
                icon = ru.filimonov.hpa.ui.common.R.drawable.edit
            )
        },
        bottomBar = {
            HpaBottomBar(
                onNavigateToDestination = onNavigateToDestination,
                currentTab = DevicesScreenDestination
            )
        },
    ) {
        when (state) {
            is SimpleLoadingUiState.Error -> {
                if (prevState == SimpleLoadingUiState.Initial) {
                    HpaLoadingScreen()
                } else {
                    DevicesGrid(
                        devices = data.devices,
                        onLoadPhoto = onLoadPhoto,
                        onNavigateToDevice = onNavigateToDevice,
                    )
                }
                snackbarState.replaceSnackbar(messageRes = state.error)
            }

            SimpleLoadingUiState.Initial -> {
                HpaLoadingScreen()
            }

            SimpleLoadingUiState.Loaded -> {
                DevicesGrid(
                    devices = data.devices,
                    onLoadPhoto = onLoadPhoto,
                    onNavigateToDevice = onNavigateToDevice,
                )
            }

            SimpleLoadingUiState.Loading -> {
                HpaLoadingScreen()
            }
        }
    }
}

@Composable
private fun DevicesGrid(
    modifier: Modifier = Modifier,
    devices: List<Device>,
    onLoadPhoto: (URI?) -> Flow<Bitmap?>,
    onNavigateToDevice: (UUID) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        if (devices.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(id = R.string.no_devices_found),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyVerticalGrid(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(items = devices, key = Device::uuid) { device ->
                    val clickableDevice = Modifier.clickable { onNavigateToDevice(device.uuid) }
                    val devicePhoto by loadDevicePhoto(device = device, onLoadPhoto = onLoadPhoto)
                    when (device) {
                        is DeviceWithPlant -> {
                            DeviceWithPlantCard(
                                modifier = Modifier
                                    .height(250.dp)
                                    .fillMaxWidth()
                                    .then(clickableDevice),
                                device = device,
                                devicePhoto = devicePhoto
                            )
                        }

                        is DeviceWithoutPlant -> {
                            DeviceWithoutPlantCard(
                                modifier = Modifier
                                    .height(250.dp)
                                    .fillMaxWidth()
                                    .then(clickableDevice),
                                device = device,
                                devicePhoto = devicePhoto
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DeviceWithPlantCard(
    device: DeviceWithPlant,
    devicePhoto: Bitmap?,
    modifier: Modifier = Modifier,
) {
    val painter = if (devicePhoto == null)
        painterResource(id = R.drawable.watering_plant)
    else
        BitmapPainter(devicePhoto.asImageBitmap())

    HpaCardWithPhoto(
        modifier = modifier,
        title = device.name,
        painter = painter,
    )
}

@Composable
private fun DeviceWithoutPlantCard(
    device: DeviceWithoutPlant,
    devicePhoto: Bitmap?,
    modifier: Modifier = Modifier,
) {
    val painter = if (devicePhoto == null)
        painterResource(id = R.drawable.watering_plant)
    else
        BitmapPainter(devicePhoto.asImageBitmap())

    HpaCardWithPhoto(
        modifier = modifier,
        title = device.name,
        painter = painter,
    )
}

@Composable
private fun loadDevicePhoto(
    device: Device,
    onLoadPhoto: (URI?) -> Flow<Bitmap?>
): State<Bitmap?> {
    val photoFlow = remember(device) {
        onLoadPhoto(device.photoUri)
    }
    return photoFlow.collectAsStateWhileResumed(initialValue = null)
}

@Composable
@Preview
private fun ContentWithLoadedDataPreview() {
    val data = Data(
        devices = listOf(
            DeviceWithPlant(
                uuid = UUID.randomUUID(),
                plant = Plant(
                    uuid = UUID.randomUUID(),
                    name = "Хлорофитум 1",
                ),
                name = "Хлорофитум на кухне",
                photoUri = null,
            ),
            DeviceWithPlant(
                uuid = UUID.randomUUID(),
                plant = Plant(
                    uuid = UUID.randomUUID(),
                    name = "Хлорофитум необыкновенный 243",
                ),
                name = "Хлорофитум в комнате",
                photoUri = null,
            ),
            DeviceWithoutPlant(
                uuid = UUID.randomUUID(),
                name = "asdfjwqjid",
                photoUri = null,
            ),
            DeviceWithoutPlant(
                uuid = UUID.randomUUID(),
                name = "asdfjwqjid",
                photoUri = null,
            ),
            DeviceWithoutPlant(
                uuid = UUID.randomUUID(),
                name = "asdfjwqjid",
                photoUri = null,
            ),
            DeviceWithoutPlant(
                uuid = UUID.randomUUID(),
                name = "asdfjwqjid",
                photoUri = null,
            ),
            DeviceWithoutPlant(
                uuid = UUID.randomUUID(),
                name = "asdfjwqjid",
                photoUri = null,
            )
        )
    )
    HpaTheme(dynamicColor = false) {
        CompositionLocalProvider(LocalSnackbarState provides SnackbarState.rememberSnackbarState()) {
            Content(
                prevState = SimpleLoadingUiState.Initial,
                state = SimpleLoadingUiState.Loaded,
                data = data,
                onLoadPhoto = { emptyFlow() },
                onNavigateToDestination = {},
                onAddDevice = {},
                onNavigateToDevice = {},
            )
        }
    }
}

@Composable
@Preview
private fun ContentWithEmptyLoadedDataPreview() {
    HpaTheme(dynamicColor = false) {
        CompositionLocalProvider(LocalSnackbarState provides SnackbarState.rememberSnackbarState()) {
            Content(
                prevState = SimpleLoadingUiState.Initial,
                state = SimpleLoadingUiState.Loaded,
                data = Data(),
                onLoadPhoto = { emptyFlow() },
                onNavigateToDestination = {},
                onAddDevice = {},
                onNavigateToDevice = {},
            )
        }
    }
}

@Composable
@Preview
private fun ContentWithLoadingPreview() {
    HpaTheme(dynamicColor = false) {
        CompositionLocalProvider(LocalSnackbarState provides SnackbarState.rememberSnackbarState()) {
            Content(
                prevState = SimpleLoadingUiState.Initial,
                state = SimpleLoadingUiState.Loading,
                data = Data(),
                onLoadPhoto = { emptyFlow() },
                onNavigateToDestination = {},
                onAddDevice = {},
                onNavigateToDevice = {},
            )
        }
    }
}
