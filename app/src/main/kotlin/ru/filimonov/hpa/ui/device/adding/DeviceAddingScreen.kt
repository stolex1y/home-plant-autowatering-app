package ru.filimonov.hpa.ui.device.adding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.filimonov.hpa.R
import ru.filimonov.hpa.domain.model.DeviceInfo
import ru.filimonov.hpa.ui.common.navigation.Destination
import ru.filimonov.hpa.ui.device.adding.model.AddingDeviceConfiguration
import ru.filimonov.hpa.ui.theme.HpaTheme
import ru.filimonov.hpa.widgets.HpaFilledTextButton
import ru.filimonov.hpa.widgets.HpaOutlinedTextButton
import ru.filimonov.hpa.widgets.HpaOutlinedTextField
import ru.filimonov.hpa.widgets.HpaScaffold
import ru.filimonov.hpa.widgets.HpaSnackbarHost
import ru.filimonov.hpa.widgets.SnackbarState
import java.util.UUID

@Composable
fun DeviceAddingScreen(
    onAdded: (UUID) -> Unit,
    onCancel: () -> Unit,
    viewModel: DeviceAddingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val data by viewModel.data.collectAsState()
    val snackbarState = SnackbarState.rememberSnackbarState()
    val addingDeviceConfiguration =
        rememberSaveable(saver = AddingDeviceConfiguration.saver()) { AddingDeviceConfiguration() }

    HpaScaffold(
        title = R.string.device_adding_title,
        contentAlignment = Alignment.Center,
        snackbarHost = {
            HpaSnackbarHost(snackbarState)
        }
    ) {
        when (val dataValue = data) {
            DeviceAddingViewModel.Data.Empty -> DeviceNotFoundMessage()
            is DeviceAddingViewModel.Data.Filled -> DeviceConfigurationForm(
                validatedEntity = addingDeviceConfiguration,
                onAdding = {
                    viewModel.addDevice(deviceInfo = dataValue.deviceInfo, deviceConfiguration = it)
                },
                onCancel = onCancel
            )
        }
    }

    when (val stateValue = state) {
        is DeviceAddingViewModel.State.Error -> {
            snackbarState.replaceSnackbar(messageRes = stateValue.error)
        }

        is DeviceAddingViewModel.State.Added -> {
            snackbarState.replaceSnackbar(messageRes = R.string.device_added)
            onAdded(stateValue.addedDeviceId)
        }

        DeviceAddingViewModel.State.Adding -> {
            snackbarState.showSnackbar(
                messageRes = R.string.device_adding,
                duration = SnackbarDuration.Indefinite,
            )
        }

        DeviceAddingViewModel.State.Initial -> {}

        DeviceAddingViewModel.State.Loaded -> {
            snackbarState.cancel()
        }

        DeviceAddingViewModel.State.Loading -> {}
    }
}

@Composable
fun DeviceConfigurationForm(
    modifier: Modifier = Modifier,
    validatedEntity: AddingDeviceConfiguration,
    onAdding: (AddingDeviceConfiguration) -> Unit,
    onCancel: () -> Unit,
) {
    val isEntityValid by validatedEntity.isValidAsState
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        HpaOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            validatedProperty = validatedEntity.ssid,
            label = R.string.ssid
        )
        HpaOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            validatedProperty = validatedEntity.pass,
            label = R.string.pass
        )
        Spacer(modifier = Modifier.height(36.dp))
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
                onClick = { onAdding(validatedEntity) },
                text = R.string.add
            )
        }
    }
}

@Composable
fun DeviceNotFoundMessage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Нет доступных устройств")
    }
}


fun NavGraphBuilder.addDeviceAddingScreen(
    onAdding: (UUID) -> Unit,
    onCancel: () -> Unit,
) {
    composable(
        route = DeviceAddingScreenDestination.path.raw,
    ) {
        DeviceAddingScreen(
            onAdded = onAdding,
            onCancel = onCancel,
        )
    }
}

object DeviceAddingScreenDestination : Destination {
    override val path: Destination.Path = Destination.Path() / "devices/add"
}

@Composable
@Preview(showBackground = true)
private fun DeviceConfigurationFormPreview() {
    HpaTheme {
        DeviceConfigurationForm(
            validatedEntity = AddingDeviceConfiguration(),
            onAdding = {},
            onCancel = {},
        )
    }
}
