package ru.filimonov.hpa.ui.plant.details

import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
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
import ru.filimonov.hpa.domain.model.DomainPlant
import ru.filimonov.hpa.ui.common.navigation.Destination
import ru.filimonov.hpa.ui.common.utils.LaunchedEffectEveryResuming
import ru.filimonov.hpa.ui.common.utils.collectAsStateWhileResumed
import ru.filimonov.hpa.ui.navigation.HpaBottomBar
import ru.filimonov.hpa.ui.plant.details.PlantDetailsViewModel.Data
import ru.filimonov.hpa.ui.plant.details.PlantDetailsViewModel.Event
import ru.filimonov.hpa.ui.plant.details.PlantDetailsViewModel.UiState
import ru.filimonov.hpa.ui.plant.details.model.ParameterConfig
import ru.filimonov.hpa.ui.plant.details.model.Plant
import ru.filimonov.hpa.ui.plant.details.model.Plant.Companion.toPlant
import ru.filimonov.hpa.ui.plants.PlantsScreenDestination
import ru.filimonov.hpa.ui.theme.HpaTheme
import ru.filimonov.hpa.widgets.HpaActionButton
import ru.filimonov.hpa.widgets.HpaCardWithText
import ru.filimonov.hpa.widgets.HpaLoadingScreen
import ru.filimonov.hpa.widgets.HpaScaffold
import ru.filimonov.hpa.widgets.HpaSnackbarHost
import ru.filimonov.hpa.widgets.LocalSnackbarState
import ru.filimonov.hpa.widgets.SnackbarState
import java.net.URI
import java.util.UUID

fun NavGraphBuilder.addPlantDetailsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToDestination: (Destination) -> Unit,
    onEditPlant: (UUID) -> Unit,
    onDeleted: () -> Unit,
) {
    val destinationSample = PlantDetailsScreenDestination(UUID.randomUUID())
    composable(
        route = destinationSample.path.raw,
        arguments = listOf(destinationSample.plantIdArg.toNavArgument())
    ) {
        PlantDetailsScreen(
            onNavigateUp = onNavigateUp,
            onNavigateToDestination = onNavigateToDestination,
            onEditPlant = onEditPlant,
            onPlantDeleted = onDeleted,
        )
    }
}

class PlantDetailsScreenDestination(
    plantId: UUID
) : Destination {
    internal val plantIdArg = Destination.Arg(ARG_PLANT_ID, plantId.toString())

    override val path: Destination.Path =
        Destination.Path() /
                "plants" /
                plantIdArg

    companion object {
        internal const val ARG_PLANT_ID = "plantId"
    }
}

@Composable
fun PlantDetailsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToDestination: (Destination) -> Unit,
    onEditPlant: (UUID) -> Unit,
    onPlantDeleted: () -> Unit,
    viewModel: PlantDetailsViewModel = hiltViewModel(),
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
        onPlantDeleted = onPlantDeleted,
        onEditPlant = onEditPlant,
    )
}

@Composable
private fun Content(
    data: Data,
    state: UiState,
    onDispatchEvent: (Event) -> Unit,
    onLoadPhoto: (URI?) -> Flow<Bitmap?>,
    onEditPlant: (UUID) -> Unit,
    onPlantDeleted: () -> Unit,
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
        onNavigateUp = onNavigateUp,
        actions = {
            if (data is Data.Loaded) {
                HpaActionButton(
                    contentDescription = R.string.edit_plant,
                    onClick = { onEditPlant(data.plant.uuid) },
                    icon = ru.filimonov.hpa.ui.common.R.drawable.edit
                )
                HpaActionButton(
                    contentDescription = R.string.delete_plant,
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
                )
                if (state is UiState.Deleting) {
                    snackbarState.replaceSnackbar(messageRes = R.string.plant_deleting)
                } else if (state is UiState.Error) {
                    snackbarState.replaceSnackbar(messageRes = state.error)
                }
            }

            UiState.Loading -> HpaLoadingScreen()

            UiState.Deleted -> {
                snackbarState.replaceSnackbar(messageRes = R.string.plant_deleted)
                onPlantDeleted()
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
) {
    when (data) {
        Data.Empty -> {}

        is Data.Loaded -> {
            val plantPhoto by loadPlantPhoto(plant = data.plant, onLoadPhoto = onLoadPhoto)
            PlantDetails(
                plant = data.plant,
                plantPhoto = plantPhoto,
            )
        }
    }
}

@Composable
private fun PlantDetails(
    plant: Plant,
    plantPhoto: Bitmap?,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        PlantPhoto(
            bitmap = plantPhoto,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .aspectRatio(1f)
                .clip(CircleShape)
        )
        Text(
            text = plant.name,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val verticalArrangement = Arrangement.spacedBy(16.dp)
            val columnModifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            Column(modifier = columnModifier, verticalArrangement = verticalArrangement) {
                SoilMoistureCard(soilMoistureMin = plant.soilMoistureMin)
                ParameterConfigsColumn(
                    parameters = listOf(
                        plant.lightLuxConfig,
                    ),
                    verticalArrangement = verticalArrangement,
                )
            }
            ParameterConfigsColumn(
                modifier = columnModifier,
                parameters = listOf(
                    plant.airTempConfig,
                    plant.airHumidityConfig,
                ),
                verticalArrangement = verticalArrangement,
            )
        }
    }
}

@Composable
private fun ParameterConfigsColumn(
    modifier: Modifier = Modifier,
    parameters: List<ParameterConfig>,
    verticalArrangement: Arrangement.Vertical,
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
    ) {
        for (parameter in parameters) {
            ParameterCard(
                title = parameter.name,
                content = "${parameter.minStr()} — ${parameter.maxStr()}"
            )
        }
    }
}

@Composable
private fun SoilMoistureCard(
    soilMoistureMin: String
) {
    ParameterCard(title = R.string.soil_moisture, content = soilMoistureMin)
}

@Composable
private fun ParameterCard(
    @StringRes title: Int,
    content: String,
) {
    HpaCardWithText(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp),
        title = stringResource(id = title),
        minTitleLines = 2,
        content = content,
        titleStyle = MaterialTheme.typography.titleMedium,
        contentStyle = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun PlantPhoto(
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
private fun loadPlantPhoto(
    plant: Plant,
    onLoadPhoto: (URI?) -> Flow<Bitmap?>,
): State<Bitmap?> {
    val photoFlow = remember(plant) {
        onLoadPhoto(plant.photoUri)
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
                onEditPlant = {},
                onPlantDeleted = {},
            )
        }
    }
}

@Composable
@Preview(showBackground = true, locale = "ru")
private fun ContentWithLoadedDataWithEmptyParamsPreview() {
    val plant = DomainPlant(
        uuid = UUID.randomUUID(),
        name = "Хлорофитум 1",
        photoUri = null,
        airTempMin = null,
        airTempMax = null,
        airHumidityMin = null,
        airHumidityMax = 70f,
        lightLuxMin = 500,
        lightLuxMax = null,
        soilMoistureMin = null,
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
                onEditPlant = {},
                onPlantDeleted = {},
            )
        }
    }
}
