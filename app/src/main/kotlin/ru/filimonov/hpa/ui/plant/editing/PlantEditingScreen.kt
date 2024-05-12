package ru.filimonov.hpa.ui.plant.editing

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.filimonov.hpa.R
import ru.filimonov.hpa.domain.model.DomainPlant
import ru.filimonov.hpa.ui.common.navigation.Destination
import ru.filimonov.hpa.ui.common.utils.LaunchedEffectEveryResuming
import ru.filimonov.hpa.ui.common.utils.collectAsStateWhileResumed
import ru.filimonov.hpa.ui.common.validation.ValidatedProperty
import ru.filimonov.hpa.ui.navigation.HpaBottomBar
import ru.filimonov.hpa.ui.plant.editing.PlantEditingViewModel.Data
import ru.filimonov.hpa.ui.plant.editing.PlantEditingViewModel.Event
import ru.filimonov.hpa.ui.plant.editing.PlantEditingViewModel.UiState
import ru.filimonov.hpa.ui.plant.editing.model.EditingPlant
import ru.filimonov.hpa.ui.plant.editing.model.Plant
import ru.filimonov.hpa.ui.plant.editing.model.toPlant
import ru.filimonov.hpa.ui.plants.PlantsScreenDestination
import ru.filimonov.hpa.ui.theme.HpaTheme
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

fun NavGraphBuilder.addPlantEditingScreen(
    onNavigateUp: () -> Unit,
    onNavigateToDestination: (Destination) -> Unit,
    onEdited: () -> Unit,
    onCancelEditing: () -> Unit,
) {
    val destinationSample = PlantEditingScreenDestination(UUID.randomUUID())
    composable(
        route = destinationSample.path.raw,
        arguments = listOf(destinationSample.plantIdArg.toNavArgument())
    ) {
        PlantEditingScreen(
            onNavigateUp = onNavigateUp,
            onNavigateToDestination = onNavigateToDestination,
            onEdited = onEdited,
            onCancelEditing = onCancelEditing,
        )
    }
}

class PlantEditingScreenDestination(
    plantId: UUID
) : Destination {
    internal val plantIdArg = Destination.Arg(ARG_PLANT_ID, plantId.toString())

    override val path: Destination.Path =
        Destination.Path() /
                "plants" /
                plantIdArg /
                "edit"

    companion object {
        internal const val ARG_PLANT_ID = "plantId"
    }
}

@Composable
fun PlantEditingScreen(
    onNavigateUp: () -> Unit,
    onNavigateToDestination: (Destination) -> Unit,
    onEdited: () -> Unit,
    onCancelEditing: () -> Unit,
    viewModel: PlantEditingViewModel = hiltViewModel(),
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
        onLoadPhoto = viewModel::loadPlantPhoto,
        onNavigateUp = onNavigateUp,
        onNavigateToDestination = onNavigateToDestination,
        onCancelEditing = onCancelEditing,
        onEdited = onEdited,
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
    onNavigateUp: () -> Unit,
    onNavigateToDestination: (Destination) -> Unit,
) {
    val snackbarState = LocalSnackbarState.current
    HpaScaffold(
        snackbarHost = { HpaSnackbarHost(snackbarState = snackbarState) },
        bottomBar = {
            HpaBottomBar(
                onNavigateToDestination = onNavigateToDestination,
                currentTab = PlantsScreenDestination,
            )
        },
        title = stringResource(id = R.string.plant_editing_title),
        onNavigateUp = onNavigateUp,
    ) {
        when (state) {
            UiState.Loaded, UiState.Editing, is UiState.Error -> {
                DataContent(
                    data = data,
                    onLoadPhoto = onLoadPhoto,
                    onCancelEditing = onCancelEditing,
                    onConfirmEditing = { onDispatchEvent(Event.Edit(it)) },
                )
                if (state is UiState.Editing) {
                    snackbarState.replaceSnackbar(messageRes = R.string.plant_editing)
                } else if (state is UiState.Error) {
                    snackbarState.replaceSnackbar(messageRes = state.error)
                }
            }

            UiState.Loading -> HpaLoadingScreen()

            UiState.Edited -> {
                snackbarState.replaceSnackbar(messageRes = R.string.plant_edited)
                onEdited()
            }

            UiState.NotFound -> {
                snackbarState.replaceSnackbar(messageRes = R.string.plant_not_found)
                onNavigateUp()
            }
        }
    }
}

@Composable
private fun DataContent(
    data: Data,
    onLoadPhoto: (URI?) -> Flow<Bitmap?>,
    onConfirmEditing: (EditingPlant) -> Unit,
    onCancelEditing: () -> Unit,
) {
    when (data) {
        Data.Empty -> {}

        is Data.Loaded -> {
            PlantEditingForm(
                plant = data.plant,
                onLoadPhoto = onLoadPhoto,
                onConfirm = onConfirmEditing,
                onCancel = onCancelEditing,
            )
        }
    }
}

@Composable
private fun PlantEditingForm(
    plant: Plant,
    onLoadPhoto: (URI?) -> Flow<Bitmap?>,
    onConfirm: (EditingPlant) -> Unit,
    onCancel: () -> Unit,
) {
    val editingPlant = rememberSaveable(saver = EditingPlant.saver()) {
        plant.toEditingPlant()
    }
    val plantPhotoUri by editingPlant.photoUri.asState
    val plantPhoto by loadPlantPhoto(photoUri = plantPhotoUri, onLoadPhoto = onLoadPhoto)
    val isEntityValid by editingPlant.isValidAsState
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .verticalScroll(state = scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EditingPhoto(
            photo = plantPhoto,
            modifier = Modifier
                .size(200.dp),
            onUpdatePhoto = { uri ->
                editingPlant.photoUri.set(uri)
            }
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SensorReadingConfigForm(
                title = stringResource(id = R.string.air_temp_field_name, "°C"),
                minProperty = editingPlant.airTempMin,
                maxProperty = editingPlant.airTempMax
            )
            SensorReadingConfigForm(
                title = stringResource(id = R.string.air_humidity_field_name),
                minProperty = editingPlant.airHumidityMin,
                maxProperty = editingPlant.airHumidityMax
            )
            SensorReadingConfigForm(
                title = stringResource(id = R.string.light_level_field_name),
                minProperty = editingPlant.lightLuxMin,
                maxProperty = editingPlant.lightLuxMax
            )
            SensorReadingConfigForm(
                title = stringResource(id = R.string.soil_moisture_field_name),
                minProperty = editingPlant.soilMoistureMin,
            )
        }
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
                onClick = { onConfirm(editingPlant) },
                text = R.string.edit
            )
        }
    }
}

@Composable
private fun SensorReadingConfigForm(
    modifier: Modifier = Modifier,
    title: String,
    minProperty: ValidatedProperty<String>,
    maxProperty: ValidatedProperty<String>? = null,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HpaOutlinedTextField(
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                validatedProperty = minProperty,
                label = R.string.from,
            )
            if (maxProperty != null) {
                Text(text = "—")
                HpaOutlinedTextField(
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    validatedProperty = maxProperty,
                    label = R.string.to,
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
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
private fun loadPlantPhoto(
    photoUri: URI?,
    onLoadPhoto: (URI?) -> Flow<Bitmap?>,
): State<Bitmap?> {
    val photoFlow = remember(photoUri) {
        onLoadPhoto(photoUri)
    }
    return photoFlow.collectAsStateWhileResumed(initialValue = null)
}

@Composable
@Preview(showBackground = true, locale = "ru")
private fun ContentWithLoadedDataPreview() {
    val plant = DomainPlant(
        uuid = UUID.randomUUID(),
        name = "Хлорофитум 1",
        photoUri = null,
        airTempMin = 8f,
        airTempMax = 20f,
        airHumidityMin = 20f,
        airHumidityMax = 70f,
        lightLuxMin = 500,
        lightLuxMax = 2000,
        soilMoistureMin = 20f,
    )
    HpaTheme(dynamicColor = false) {
        CompositionLocalProvider(LocalSnackbarState provides SnackbarState.rememberSnackbarState()) {
            Content(
                data = Data.Loaded(plant.toPlant()),
                state = UiState.Loaded,
                onDispatchEvent = {},
                onNavigateUp = {},
                onNavigateToDestination = {},
                onLoadPhoto = { emptyFlow() },
                onCancelEditing = {},
                onEdited = {},
            )
        }
    }
}
