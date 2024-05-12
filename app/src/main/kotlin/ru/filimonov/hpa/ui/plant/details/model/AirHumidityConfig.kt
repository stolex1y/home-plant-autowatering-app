package ru.filimonov.hpa.ui.plant.details.model

import androidx.compose.runtime.Composable
import ru.filimonov.hpa.R
import ru.filimonov.hpa.ui.util.formatAsPercents

data class AirHumidityConfig(
    val min: Float?,
    val max: Float?,
) : ParameterConfig(
    name = R.string.air_humidity,
) {
    private val _minStr: String = min?.formatAsPercents(0) ?: "＿"
    private val _maxStr: String = max?.formatAsPercents(0) ?: "＿"

    @Composable
    override fun minStr(): String {
        return _minStr
    }

    @Composable
    override fun maxStr(): String {
        return _maxStr
    }
}
