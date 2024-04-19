package ru.filimonov.hpa.ui.devices

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.filimonov.hpa.R
import ru.filimonov.hpa.ui.common.navigation.Destination
import ru.filimonov.hpa.ui.common.udf.SimpleLoadingState
import ru.filimonov.hpa.ui.device.details.model.Plant
import ru.filimonov.hpa.ui.devices.model.Device
import ru.filimonov.hpa.ui.devices.model.DeviceWithPlant
import ru.filimonov.hpa.ui.devices.model.DeviceWithoutPlant
import ru.filimonov.hpa.ui.navigation.BottomBarDestination
import ru.filimonov.hpa.ui.navigation.HpaBottomBar
import ru.filimonov.hpa.ui.theme.HpaTheme
import ru.filimonov.hpa.widgets.HpaLoading
import ru.filimonov.hpa.widgets.HpaScaffold
import ru.filimonov.hpa.widgets.HpaSnackbarHost
import ru.filimonov.hpa.widgets.SnackbarState
import java.net.URI
import java.util.UUID

@Composable
fun DevicesScreen(
    onNavigateToDestination: (Destination) -> Unit,
    onAddDevice: () -> Unit,
    devicesViewModel: DevicesViewModel = hiltViewModel(),
) {
    LaunchedEffect(devicesViewModel) {
        devicesViewModel.dispatchEvent(DevicesViewModel.Event.Reload)
    }

    val data by devicesViewModel.data.collectAsStateWithLifecycle()
    val state by devicesViewModel.state.collectAsStateWithLifecycle()
    val snackbarState = SnackbarState.rememberSnackbarState()

    HpaScaffold(
        modifier = Modifier.consumeWindowInsets(WindowInsets.navigationBars),
        snackbarHost = { HpaSnackbarHost(snackbarState = snackbarState) },
        floatingActionButton = { AddDeviceButton(onClick = onAddDevice) },
        bottomBar = {
            HpaBottomBar(
                onNavigateToDestination = onNavigateToDestination,
                currentTab = DevicesScreenDestination
            )
        },
    ) {
        when (val stateValue = state) {
            is SimpleLoadingState.Error -> {
                if (devicesViewModel.prevState == SimpleLoadingState.Initial) {
                    Loading()
                } else {
                    Content(
                        devices = data.devices,
                        modifier = Modifier
                            .fillMaxSize(),
                        loadPhoto = devicesViewModel::loadDevicePhoto
                    )
                }
                snackbarState.replaceSnackbar(messageRes = stateValue.error)
            }

            SimpleLoadingState.Initial -> {
                Loading()
            }

            SimpleLoadingState.Loaded -> {
                Content(
                    devices = data.devices,
                    modifier = Modifier
                        .fillMaxSize(),
                    loadPhoto = devicesViewModel::loadDevicePhoto
                )
            }

            SimpleLoadingState.Loading -> {
                Loading()
            }
        }
    }

}

fun NavGraphBuilder.addDevicesScreen(
    onNavigateToDestination: (Destination) -> Unit,
    onAddDevice: () -> Unit,
) {
    composable(
        route = DevicesScreenDestination.path.raw,
    ) {
        DevicesScreen(
            onNavigateToDestination = onNavigateToDestination,
            onAddDevice = onAddDevice,
        )
    }
}

object DevicesScreenDestination : Destination, BottomBarDestination {
    override val path = Destination.Path() / "devices"
    override val title: Int = R.string.devices
    override val icon: Int = R.drawable.watering_plant
}

@Composable
private fun Content(
    devices: List<Device>,
    loadPhoto: (URI) -> Flow<Bitmap?>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
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
                    val devicePhoto =
                        if (device.photoUri == null) remember {
                            mutableStateOf<Bitmap?>(
                                null
                            )
                        } else {
                            loadPhoto(device.photoUri).collectAsStateWithLifecycle(null)
                        }
                    if (device is DeviceWithPlant) {
                        DeviceWithPlantCard(device = device, devicePhoto = devicePhoto.value)
                    } else if (device is DeviceWithoutPlant) {
                        DeviceWithoutPlantCard(device = device, devicePhoto = devicePhoto.value)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        HpaLoading()
    }
}

@Composable
private fun DeviceWithPlantCard(
    device: DeviceWithPlant,
    devicePhoto: Bitmap?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = Modifier
            .then(modifier)
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = device.name,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            DevicePhoto(bitmap = devicePhoto)
        }
    }
}

@Composable
private fun DeviceWithoutPlantCard(
    device: DeviceWithoutPlant,
    devicePhoto: Bitmap?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = Modifier
            .then(modifier)
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = device.name,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            DevicePhoto(bitmap = devicePhoto)
        }
    }
}

@Composable
private fun AddDeviceButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        shape = RoundedCornerShape(100),
        modifier = modifier,
        onClick = onClick
    ) {

    }
}

@Composable
private fun DevicePhoto(
    bitmap: Bitmap?,
) {
    val modifier = Modifier
        .height(150.dp)
        .clip(MaterialTheme.shapes.small)
    if (bitmap == null) {
        Image(
            painter = painterResource(id = R.drawable.watering_plant),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = modifier,
        )
    } else {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = modifier,
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun ContentPreview() {
    HpaTheme {
        Content(
            loadPhoto = { emptyFlow() },
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
    }
}

@Composable
@Preview(showBackground = true)
private fun EmptyContentPreview() {
    HpaTheme {
        Content(
            loadPhoto = { emptyFlow() },
            devices = emptyList(),
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun DeviceWithPlantCardPreview() {
    HpaTheme {
        DeviceWithPlantCard(
            devicePhoto = null,
            device = DeviceWithPlant(
                uuid = UUID.randomUUID(),
                plant = Plant(
                    name = "Хлорофитум",
                    uuid = UUID.randomUUID(),
                ),
                name = "Хлорофитум",
                photoUri = null,
            )
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun DeviceWithoutPlantCardPreview() {
    HpaTheme {
        DeviceWithoutPlantCard(
            devicePhoto = null,
            device = DeviceWithoutPlant(
                uuid = UUID.randomUUID(),
                name = "Хлорофитум",
                photoUri = null,
            )
        )
    }
}
