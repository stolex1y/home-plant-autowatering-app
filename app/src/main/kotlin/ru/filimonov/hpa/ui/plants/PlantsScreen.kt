package ru.filimonov.hpa.ui.plants

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
import ru.filimonov.hpa.ui.navigation.BottomBarDestination
import ru.filimonov.hpa.ui.navigation.HpaBottomBar
import ru.filimonov.hpa.ui.plants.PlantsViewModel.Data
import ru.filimonov.hpa.ui.plants.PlantsViewModel.Event
import ru.filimonov.hpa.ui.plants.model.Plant
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

fun NavGraphBuilder.addPlantsScreen(
    onNavigateToDestination: (Destination) -> Unit,
    onAddPlant: () -> Unit,
    onNavigateToPlant: (UUID) -> Unit,
) {
    composable(
        route = PlantsScreenDestination.path.raw,
    ) {
        PlantsScreen(
            onNavigateToDestination = onNavigateToDestination,
            onAddPlant = onAddPlant,
            onNavigateToPlant = onNavigateToPlant,
        )
    }
}

object PlantsScreenDestination : Destination, BottomBarDestination {
    override val path = Destination.Path() / "plants"
    override val title: Int = R.string.plants
    override val icon: Int = R.drawable.watering_plant
}

@Composable
fun PlantsScreen(
    onNavigateToDestination: (Destination) -> Unit,
    onAddPlant: () -> Unit,
    viewModel: PlantsViewModel = hiltViewModel(),
    onNavigateToPlant: (UUID) -> Unit,
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
        onAddPlant = onAddPlant,
        onLoadPhoto = viewModel::loadPlantPhoto,
        onNavigateToDestination = onNavigateToDestination,
        onNavigateToPlant = onNavigateToPlant,
    )
}

@Composable
private fun Content(
    prevState: SimpleLoadingUiState,
    state: SimpleLoadingUiState,
    data: Data,
    onNavigateToDestination: (Destination) -> Unit,
    onAddPlant: () -> Unit,
    onLoadPhoto: (URI?) -> Flow<Bitmap?>,
    onNavigateToPlant: (UUID) -> Unit,
) {
    val snackbarState = LocalSnackbarState.current
    HpaScaffold(
        snackbarHost = { HpaSnackbarHost(snackbarState = snackbarState) },
        floatingActionButton = {
            HpaCircularIconButton(
                onClick = onAddPlant,
                icon = ru.filimonov.hpa.ui.common.R.drawable.edit
            )
        },
        bottomBar = {
            HpaBottomBar(
                onNavigateToDestination = onNavigateToDestination,
                currentTab = PlantsScreenDestination,
            )
        },
    ) {
        when (state) {
            is SimpleLoadingUiState.Error -> {
                if (prevState == SimpleLoadingUiState.Initial) {
                    HpaLoadingScreen()
                } else {
                    PlantsGrid(
                        plants = data.plants,
                        onLoadPhoto = onLoadPhoto,
                        onNavigateToPlant = onNavigateToPlant,
                    )
                }
                snackbarState.replaceSnackbar(messageRes = state.error)
            }

            SimpleLoadingUiState.Initial -> {
                HpaLoadingScreen()
            }

            SimpleLoadingUiState.Loaded -> {
                PlantsGrid(
                    plants = data.plants,
                    onLoadPhoto = onLoadPhoto,
                    onNavigateToPlant = onNavigateToPlant,
                )
            }

            SimpleLoadingUiState.Loading -> {
                HpaLoadingScreen()
            }
        }
    }
}

@Composable
private fun PlantsGrid(
    modifier: Modifier = Modifier,
    plants: List<Plant>,
    onLoadPhoto: (URI?) -> Flow<Bitmap?>,
    onNavigateToPlant: (UUID) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        if (plants.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(id = R.string.no_plants_found),
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
                items(items = plants, key = Plant::uuid) { plant ->
                    val plantPhoto by loadPlantPhoto(plant = plant, onLoadPhoto = onLoadPhoto)
                    PlantCard(
                        modifier = Modifier
                            .height(250.dp)
                            .fillMaxWidth()
                            .clickable { onNavigateToPlant(plant.uuid) },
                        plant = plant,
                        plantPhoto = plantPhoto,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PlantCard(
    plant: Plant,
    plantPhoto: Bitmap?,
    modifier: Modifier = Modifier,
) {
    val painter = if (plantPhoto == null)
        painterResource(id = R.drawable.watering_plant)
    else
        BitmapPainter(plantPhoto.asImageBitmap())

    HpaCardWithPhoto(
        modifier = modifier,
        title = plant.name,
        painter = painter,
    )
}

@Composable
private fun loadPlantPhoto(
    plant: Plant,
    onLoadPhoto: (URI?) -> Flow<Bitmap?>
): State<Bitmap?> {
    val photoFlow = remember(plant) {
        onLoadPhoto(plant.photoUri)
    }
    return photoFlow.collectAsStateWhileResumed(initialValue = null)
}

@Composable
@Preview
private fun ContentWithLoadedDataPreview() {
    val plants = (1..10).map {
        Plant(
            uuid = UUID.randomUUID(),
            name = "Хлорофитум $it",
            photoUri = null,
        )
    }.toList()
    val data = Data(plants = plants)
    HpaTheme(dynamicColor = false) {
        CompositionLocalProvider(LocalSnackbarState provides SnackbarState.rememberSnackbarState()) {
            Content(
                prevState = SimpleLoadingUiState.Initial,
                state = SimpleLoadingUiState.Loaded,
                data = data,
                onLoadPhoto = { emptyFlow() },
                onNavigateToDestination = {},
                onAddPlant = {},
                onNavigateToPlant = {},
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
                onAddPlant = {},
                onNavigateToPlant = {},
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
                onAddPlant = {},
                onNavigateToPlant = {},
            )
        }
    }
}
