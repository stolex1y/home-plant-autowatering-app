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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.filimonov.hpa.R
import ru.filimonov.hpa.domain.model.device.DomainDeviceInfo
import ru.filimonov.hpa.ui.common.navigation.Destination
import ru.filimonov.hpa.ui.common.utils.collectAsStateWhileResumed
import ru.filimonov.hpa.ui.device.adding.DeviceAddingViewModel.Data
import ru.filimonov.hpa.ui.device.adding.DeviceAddingViewModel.UiState
import ru.filimonov.hpa.ui.device.adding.model.AddingDeviceConfiguration
import ru.filimonov.hpa.ui.theme.HpaTheme
import ru.filimonov.hpa.widgets.HpaFilledTextButton
import ru.filimonov.hpa.widgets.HpaOutlinedTextButton
import ru.filimonov.hpa.widgets.HpaOutlinedTextField
import ru.filimonov.hpa.widgets.HpaScaffold
import ru.filimonov.hpa.widgets.HpaSnackbarHost
import ru.filimonov.hpa.widgets.SnackbarState
import java.util.UUID

fun NavGraphBuilder.addDeviceAddingScreen(
    onAdded: (UUID) -> Unit,
    onCancelAdding: () -> Unit,
) {
    composable(
        route = DeviceAddingScreenDestination.path.raw,
    ) {
        DeviceAddingScreen(
            onAdded = onAdded,
            onCancel = onCancelAdding,
        )
    }
}

object DeviceAddingScreenDestination : Destination {
    override val path: Destination.Path = Destination.Path() / "devices/add"
}

@Composable
fun DeviceAddingScreen(
    onAdded: (UUID) -> Unit,
    onCancel: () -> Unit,
    viewModel: DeviceAddingViewModel = hiltViewModel()
) {
    val data by viewModel.data.collectAsStateWhileResumed()
    val state by viewModel.state.collectAsStateWhileResumed()

    Content(
        data = data,
        state = state,
        onDeviceAdded = onAdded,
        onAddingCancel = onCancel,
        onAddDevice = viewModel::addDevice
    )
}

@Composable
private fun Content(
    data: Data,
    state: UiState,
    onDeviceAdded: (UUID) -> Unit,
    onAddingCancel: () -> Unit,
    onAddDevice: (DomainDeviceInfo, AddingDeviceConfiguration) -> Unit,
) {
    val snackbarState = SnackbarState.rememberSnackbarState()

    HpaScaffold(
        title = stringResource(id = R.string.device_adding_title),
        contentAlignment = Alignment.Center,
        snackbarHost = {
            HpaSnackbarHost(snackbarState)
        },
        onNavigateUp = onAddingCancel,
    ) {
        when (data) {
            Data.Empty -> DeviceSearchingMessage()
            is Data.Filled -> DeviceConfigurationForm(
                onConfirm = { deviceConfiguration ->
                    onAddDevice(
                        data.domainDeviceInfo,
                        deviceConfiguration,
                    )
                },
                onCancel = onAddingCancel
            )
        }
    }

    when (state) {
        is UiState.Error -> {
            snackbarState.replaceSnackbar(
                messageRes = state.error,
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite
            )
        }

        is UiState.Added -> {
            snackbarState.replaceSnackbar(messageRes = R.string.device_added)
            onDeviceAdded(state.addedDeviceId)
        }

        UiState.Adding -> {
            snackbarState.showSnackbar(
                messageRes = R.string.device_adding,
                duration = SnackbarDuration.Indefinite,
            )
        }

        UiState.Initial -> {}

        UiState.Loaded -> {
            snackbarState.cancel()
        }

        UiState.Loading -> {}
    }
}

@Composable
private fun DeviceConfigurationForm(
    modifier: Modifier = Modifier,
    onConfirm: (AddingDeviceConfiguration) -> Unit,
    onCancel: () -> Unit,
) {
    val validatedEntity =
        rememberSaveable(saver = AddingDeviceConfiguration.saver()) { AddingDeviceConfiguration() }
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
                onClick = { onConfirm(validatedEntity) },
                text = R.string.add
            )
        }
    }
}

@Composable
private fun DeviceSearchingMessage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(stringResource(id = R.string.device_searching))
    }
}

@Composable
@Preview
private fun ContentWithoutDeviceConfigurationPreview() {
    HpaTheme {
        Content(
            data = Data.Empty,
            state = UiState.Loading,
            onDeviceAdded = {},
            onAddingCancel = {},
            onAddDevice = { _, _ -> })
    }
}

@Composable
@Preview
private fun ContentWithDeviceConfigurationPreview() {
    val testData = Data.Filled(
        domainDeviceInfo = DomainDeviceInfo(mac = "")
    )
    HpaTheme {
        Content(
            data = testData,
            state = UiState.Loaded,
            onDeviceAdded = {},
            onAddingCancel = {},
            onAddDevice = { _, _ -> })
    }
}

@Composable
@Preview
private fun ContentWithErrorPreview() {
    HpaTheme {
        Content(
            data = Data.Empty,
            state = UiState.Error(R.string.invalid_device_configuration),
            onDeviceAdded = {},
            onAddingCancel = {},
            onAddDevice = { _, _ -> })
    }
}
