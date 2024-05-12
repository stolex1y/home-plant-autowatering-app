package ru.filimonov.hpa.ui.device.editing

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.filimonov.hpa.R
import ru.filimonov.hpa.ui.common.navigation.Destination
import ru.filimonov.hpa.ui.common.utils.collectAsStateWhileResumed
import ru.filimonov.hpa.ui.device.editing.DeviceEditingViewModel.Data
import ru.filimonov.hpa.ui.device.editing.DeviceEditingViewModel.Event
import ru.filimonov.hpa.ui.device.editing.DeviceEditingViewModel.UiState
import ru.filimonov.hpa.ui.device.editing.model.Device
import ru.filimonov.hpa.ui.device.editing.model.EditingDevice
import ru.filimonov.hpa.ui.device.editing.model.EditingDevice.Companion.toEditingDevice
import ru.filimonov.hpa.ui.device.editing.model.Plant
import ru.filimonov.hpa.ui.devices.DevicesScreenDestination
import ru.filimonov.hpa.ui.navigation.HpaBottomBar
import ru.filimonov.hpa.ui.theme.HpaTheme
import ru.filimonov.hpa.widgets.HpaDropdownTextField
import ru.filimonov.hpa.widgets.HpaFilledTextButton
import ru.filimonov.hpa.widgets.HpaImagePicker
import ru.filimonov.hpa.widgets.HpaLoadingScreen
import ru.filimonov.hpa.widgets.HpaOutlinedTextButton
import ru.filimonov.hpa.widgets.HpaOutlinedTextField
import ru.filimonov.hpa.widgets.HpaScaffold
import ru.filimonov.hpa.widgets.HpaSnackbarHost
import ru.filimonov.hpa.widgets.LocalSnackbarState
import ru.filimonov.hpa.widgets.SnackbarState
import java.net.URI
import java.util.UUID

fun NavGraphBuilder.addDeviceEditingScreen(
    onNavigateUp: () -> Unit,
    onDeleted: () -> Unit,
    onNavigateToDestination: (Destination) -> Unit,
    onEdited: () -> Unit,
    onCancelEditing: () -> Unit,
) {
    val destination = DeviceEditingScreenDestination(UUID.randomUUID())
    composable(
        route = destination.path.raw,
        arguments = listOf(destination.deviceIdArg.toNavArgument())
    ) {
        DeviceEditingScreen(
            onNavigateUp = onNavigateUp,
            onDeleted = onDeleted,
            onNavigateToDestination = onNavigateToDestination,
            onEdited = onEdited,
            onCancelEditing = onCancelEditing,
        )
    }
}

class DeviceEditingScreenDestination(
    deviceId: UUID
) : Destination {
    internal val deviceIdArg = Destination.Arg(ARG_DEVICE_ID, deviceId.toString())

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
fun DeviceEditingScreen(
    onNavigateUp: () -> Unit,
    onNavigateToDestination: (Destination) -> Unit,
    onDeleted: () -> Unit,
    onEdited: () -> Unit,
    onCancelEditing: () -> Unit,
    viewModel: DeviceEditingViewModel = hiltViewModel(),
) {
    val data by viewModel.data.collectAsStateWhileResumed()
    val state by viewModel.state.collectAsStateWhileResumed()

    Content(
        data = data,
        state = state,
        onDispatchEvent = viewModel::dispatchEvent,
        onLoadPhoto = viewModel::loadDevicePhoto,
        onNavigateUp = onNavigateUp,
        onNavigateToDestination = onNavigateToDestination,
        onDeleted = onDeleted,
        onEdited = onEdited,
        onCancelEditing = onCancelEditing,
    )
}

@Composable
private fun Content(
    data: Data,
    state: UiState,
    onDispatchEvent: (Event) -> Unit,
    onLoadPhoto: (URI?) -> Flow<Bitmap?>,
    onEdited: () -> Unit,
    onCancelEditing: () -> Unit,
    onDeleted: () -> Unit,
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
        title = stringResource(id = R.string.device_editing_title),
        onNavigateUp = onNavigateUp,
    ) {
        when (state) {
            UiState.Loaded, UiState.Deleting, UiState.Editing, is UiState.Error -> {
                DataContent(
                    data = data,
                    onLoadPhoto = onLoadPhoto,
                    onCancelEditing = onCancelEditing,
                    onConfirmEditing = { onDispatchEvent(Event.Edit(it)) },
                )
                if (state is UiState.Deleting) {
                    snackbarState.replaceSnackbar(messageRes = R.string.device_deleting)
                } else if (state is UiState.Editing) {
                    snackbarState.replaceSnackbar(messageRes = R.string.device_editing)
                } else if (state is UiState.Error) {
                    snackbarState.replaceSnackbar(messageRes = state.error)
                }
            }

            UiState.Deleted -> {
                snackbarState.replaceSnackbar(messageRes = R.string.device_deleted)
                onDeleted()
            }

            UiState.Edited -> {
                snackbarState.replaceSnackbar(messageRes = R.string.device_edited)
                onEdited()
            }

            UiState.Loading -> HpaLoadingScreen()

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
    onConfirmEditing: (EditingDevice) -> Unit,
    onCancelEditing: () -> Unit,
) {
    when (data) {
        Data.Empty -> {}

        is Data.Loaded -> {
            DeviceEditForm(
                device = data.device,
                plants = data.plants,
                onConfirm = onConfirmEditing,
                onCancel = onCancelEditing,
                onLoadPhoto = onLoadPhoto,
            )
        }
    }
}

@Composable
private fun DeviceEditForm(
    device: Device,
    plants: List<Plant>,
    onLoadPhoto: (URI?) -> Flow<Bitmap?>,
    onConfirm: (EditingDevice) -> Unit,
    onCancel: () -> Unit,
) {
    val editingDevice = rememberSaveable(saver = EditingDevice.saver()) {
        device.toEditingDevice()
    }
    val devicePhotoUri by editingDevice.photoUri.asState
    val devicePhoto by loadDevicePhoto(photoUri = devicePhotoUri, onLoadPhoto = onLoadPhoto)
    val isEntityValid by editingDevice.isValidAsState
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EditingPhoto(
            photo = devicePhoto,
            modifier = Modifier
                .size(200.dp),
            onUpdatePhoto = { uri ->
                editingDevice.photoUri.set(uri)
            }
        )
        HpaOutlinedTextField(
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth(),
            validatedProperty = editingDevice.name,
            label = R.string.device_name_field
        )
        HpaDropdownTextField(
            items = plants,
            itemToText = { it?.name ?: "" },
            validatedProperty = editingDevice.plant,
            label = R.string.plant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HpaOutlinedTextButton(
                onClick = onCancel,
                text = R.string.cancel
            )
            HpaFilledTextButton(
                enabled = isEntityValid,
                onClick = { onConfirm(editingDevice) },
                text = R.string.edit
            )
        }
    }
}

@Composable
private fun EditingPhoto(
    modifier: Modifier = Modifier,
    photo: Bitmap?,
    onUpdatePhoto: (URI) -> Unit,
) {
    val photoPainter = photo?.asImageBitmap()?.run(::BitmapPainter)
        ?: painterResource(id = R.drawable.watering_plant)
    HpaImagePicker(
        modifier = modifier.clip(CircleShape),
        onPickImage = onUpdatePhoto,
        painter = photoPainter
    )
}

@Composable
private fun loadDevicePhoto(
    photoUri: URI?,
    onLoadPhoto: (URI?) -> Flow<Bitmap?>
): State<Bitmap?> {
    val photoFlow = remember(photoUri) {
        onLoadPhoto(photoUri)
    }
    return photoFlow.collectAsStateWhileResumed(initialValue = null)
}

@Composable
@Preview(showBackground = true)
private fun ContentWithLoadedDataPreview() {
    val devicePlant = Plant(
        UUID.randomUUID(), "Текущее растение устройства"
    )
    val device = Device(
        uuid = UUID.randomUUID(),
        plant = devicePlant,
        name = UUID.randomUUID().toString(),
        photoUri = null,
    )
    val data = Data.Loaded(
        device = device,
        plants = listOf(
            devicePlant,
            Plant(UUID.randomUUID(), "Растение 1"),
            Plant(UUID.randomUUID(), "Растение 2"),
            Plant(UUID.randomUUID(), "Растение 3"),
            Plant(UUID.randomUUID(), "Растение 4"),
        )
    )
    HpaTheme(dynamicColor = false) {
        CompositionLocalProvider(LocalSnackbarState provides SnackbarState.rememberSnackbarState()) {
            Content(
                state = UiState.Loaded,
                data = data,
                onEdited = {},
                onDeleted = {},
                onNavigateUp = {},
                onNavigateToDestination = {},
                onLoadPhoto = { emptyFlow() },
                onDispatchEvent = {},
                onCancelEditing = {},
            )
        }
    }
}
