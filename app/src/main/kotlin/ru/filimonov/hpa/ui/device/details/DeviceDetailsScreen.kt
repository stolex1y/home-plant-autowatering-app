package ru.filimonov.hpa.ui.device.details

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.filimonov.hpa.R
import ru.filimonov.hpa.domain.model.readings.DomainSensorReading
import ru.filimonov.hpa.domain.model.readings.DomainSensorReadings
import ru.filimonov.hpa.ui.common.navigation.Destination
import ru.filimonov.hpa.ui.common.utils.LaunchedEffectEveryResuming
import ru.filimonov.hpa.ui.common.utils.collectAsStateWhileResumed
import ru.filimonov.hpa.ui.device.details.DeviceDetailsViewModel.Data
import ru.filimonov.hpa.ui.device.details.DeviceDetailsViewModel.Event
import ru.filimonov.hpa.ui.device.details.DeviceDetailsViewModel.UiState
import ru.filimonov.hpa.ui.device.details.model.Device
import ru.filimonov.hpa.ui.device.details.model.Plant
import ru.filimonov.hpa.ui.device.details.model.SensorReadings.Companion.toSensorReadings
import ru.filimonov.hpa.ui.devices.DevicesScreenDestination
import ru.filimonov.hpa.ui.navigation.HpaBottomBar
import ru.filimonov.hpa.ui.theme.HpaTheme
import ru.filimonov.hpa.widgets.HpaActionButton
import ru.filimonov.hpa.widgets.HpaCardWithText
import ru.filimonov.hpa.widgets.HpaLoadingScreen
import ru.filimonov.hpa.widgets.HpaScaffold
import ru.filimonov.hpa.widgets.HpaSnackbarHost
import ru.filimonov.hpa.widgets.HpaTextWithCaption
import ru.filimonov.hpa.widgets.LocalSnackbarState
import ru.filimonov.hpa.widgets.SnackbarState
import java.net.URI
import java.time.ZonedDateTime
import java.util.UUID

fun NavGraphBuilder.addDeviceDetailsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToDestination: (Destination) -> Unit,
    onEditDevice: (UUID) -> Unit,
    onDeleted: () -> Unit,
    onNavigateToPlant: (UUID) -> Unit,
) {
    val destinationSample = DeviceDetailsScreenDestination(UUID.randomUUID())
    composable(
        route = destinationSample.path.raw,
        arguments = listOf(destinationSample.deviceIdArg.toNavArgument())
    ) {
        DeviceDetailsScreen(
            onNavigateUp = onNavigateUp,
            onNavigateToDestination = onNavigateToDestination,
            onEditDevice = onEditDevice,
            onDeleteDevice = onDeleted,
            onNavigateToPlant = onNavigateToPlant,
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
fun DeviceDetailsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToDestination: (Destination) -> Unit,
    onEditDevice: (UUID) -> Unit,
    onDeleteDevice: () -> Unit,
    onNavigateToPlant: (UUID) -> Unit,
    viewModel: DeviceDetailsViewModel = hiltViewModel(),
) {
    LaunchedEffectEveryResuming(key1 = viewModel) {
        viewModel.dispatchEvent(Event.Reload)
    }

    val data by viewModel.data.collectAsStateWhileResumed()
    val state by viewModel.state.collectAsStateWhileResumed()

    Content(
        data = data,
        state = state,
        onDispatchEvent = viewModel::dispatchEvent,
        onLoadPhoto = viewModel::loadDevicePhoto,
        onNavigateUp = onNavigateUp,
        onNavigateToDestination = onNavigateToDestination,
        onDeleteDevice = onDeleteDevice,
        onEditDevice = onEditDevice,
        onNavigateToPlant = onNavigateToPlant,
    )
}

@Composable
private fun Content(
    data: Data,
    state: UiState,
    onDispatchEvent: (Event) -> Unit,
    onLoadPhoto: (URI?) -> Flow<Bitmap?>,
    onEditDevice: (UUID) -> Unit,
    onDeleteDevice: () -> Unit,
    onNavigateToPlant: (UUID) -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToDestination: (Destination) -> Unit,
) {
    val snackbarState = LocalSnackbarState.current
    HpaScaffold(
        snackbarHost = { HpaSnackbarHost(snackbarState = snackbarState) },
        bottomBar = {
            HpaBottomBar(
                onNavigateToDestination = onNavigateToDestination,
                currentTab = DevicesScreenDestination,
            )
        },
        onNavigateUp = onNavigateUp,
        actions = {
            if (data is Data.Loaded) {
                HpaActionButton(
                    contentDescription = R.string.edit_device,
                    onClick = { onEditDevice(data.device.uuid) },
                    icon = ru.filimonov.hpa.ui.common.R.drawable.edit
                )
                HpaActionButton(
                    contentDescription = R.string.delete_device,
                    onClick = { onDispatchEvent(Event.Delete) },
                    icon = ru.filimonov.hpa.ui.common.R.drawable.cancel
                )
            }
        }
    ) {
        when (state) {
            UiState.Loaded, UiState.Deleting, is UiState.Error -> {
                DataContent(
                    data = data,
                    onLoadPhoto = onLoadPhoto,
                    onNavigateToPlant = onNavigateToPlant,
                )
                if (state is UiState.Deleting) {
                    snackbarState.replaceSnackbar(messageRes = R.string.device_deleting)
                } else if (state is UiState.Error) {
                    snackbarState.replaceSnackbar(messageRes = state.error)
                }
            }

            UiState.Loading -> HpaLoadingScreen()

            UiState.Deleted -> {
                snackbarState.replaceSnackbar(messageRes = R.string.device_deleted)
                onDeleteDevice()
            }

            UiState.NotFound -> {
                snackbarState.replaceSnackbar(messageRes = R.string.device_not_found)
                onNavigateUp()
            }
        }
    }
}

@Composable
private fun DataContent(
    data: Data,
    onLoadPhoto: (URI?) -> Flow<Bitmap?>,
    onNavigateToPlant: (UUID) -> Unit,
) {
    when (data) {
        Data.Empty -> {}

        is Data.Loaded -> {
            val devicePhoto by loadDevicePhoto(device = data.device, onLoadPhoto = onLoadPhoto)
            DeviceDetails(
                device = data.device,
                devicePhoto = devicePhoto,
                onNavigateToPlant = onNavigateToPlant,
            )
        }
    }
}

@Composable
private fun DeviceDetails(
    device: Device,
    devicePhoto: Bitmap?,
    onNavigateToPlant: (UUID) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            DevicePhoto(
                bitmap = devicePhoto,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxHeight()
            ) {
                HpaTextWithCaption(
                    caption = stringResource(id = R.string.device),
                    text = device.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HpaTextWithCaption(
                        modifier = Modifier.weight(1f),
                        caption = stringResource(id = R.string.plant),
                        text = device.plant?.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                    )
                    if (device.plant != null) {
                        HpaActionButton(
                            modifier = Modifier
                                .size(16.dp)
                                .rotate(180f),
                            contentDescription = R.string.navigate_to_plant,
                            onClick = { onNavigateToPlant(device.plant.uuid) },
                            icon = ru.filimonov.hpa.ui.widgets.R.drawable.back
                        )
                    }
                }
            }
        }
        Text(
            text = stringResource(
                id = R.string.data_is_current_on,
                device.sensorReadings.timestamp
            )
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SensorReadingColumn(
                readings = listOf(
                    stringResource(id = R.string.soil_moisture) to device.sensorReadings.soilMoisture,
                    stringResource(id = R.string.light_level) to device.sensorReadings.lightLevel,
                    stringResource(id = R.string.battery_level) to device.sensorReadings.batteryCharge,
                )
            )
            SensorReadingColumn(
                readings = listOf(
                    stringResource(id = R.string.air_temp) to device.sensorReadings.airTemp,
                    stringResource(id = R.string.air_humidity) to device.sensorReadings.airHumidity,
                )
            )
        }
    }
}

@Composable
private fun RowScope.SensorReadingColumn(
    readings: List<Pair<String, String>>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        for (reading in readings) {
            SensorReadingCard(title = reading.first, value = reading.second)
        }
    }
}

@Composable
private fun SensorReadingCard(
    title: String,
    value: String,
) {
    HpaCardWithText(
        modifier = Modifier
            .fillMaxWidth(),
//            .heightIn(min = 100.dp),
        title = title,
        minTitleLines = 2,
        content = value,
        titleStyle = MaterialTheme.typography.titleMedium,
        contentStyle = MaterialTheme.typography.bodyLarge,
    )
}

@Composable
private fun DevicePhoto(
    modifier: Modifier = Modifier,
    bitmap: Bitmap?,
) {
    val photoPainter = bitmap?.asImageBitmap()?.run(::BitmapPainter)
        ?: painterResource(id = R.drawable.watering_plant)
    Image(
        painter = photoPainter,
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = modifier,
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
@Preview(showBackground = true, locale = "ru")
private fun ContentWithLoadedDataPreview() {
    val data = Data.Loaded(
        device = Device(
            uuid = UUID.randomUUID(),
            plant = Plant(UUID.randomUUID(), UUID.randomUUID().toString()),
            name = UUID.randomUUID().toString(),
            photoUri = null,
            sensorReadings = DomainSensorReadings(
                soilMoisture = DomainSensorReading(90f, ZonedDateTime.now()),
                airHumidity = DomainSensorReading(40f, ZonedDateTime.now()),
                airTemp = DomainSensorReading(22f, ZonedDateTime.now()),
                lightLevel = DomainSensorReading(670, ZonedDateTime.now()),
                batteryCharge = DomainSensorReading(90f, ZonedDateTime.now()),
            ).toSensorReadings(),
        )
    )
    HpaTheme(dynamicColor = false) {
        CompositionLocalProvider(LocalSnackbarState provides SnackbarState.rememberSnackbarState()) {
            Content(
                data = data,
                state = UiState.Loaded,
                onDispatchEvent = {},
                onNavigateUp = {},
                onNavigateToDestination = {},
                onLoadPhoto = { emptyFlow() },
                onEditDevice = {},
                onNavigateToPlant = {},
                onDeleteDevice = {},
            )
        }
    }
}
