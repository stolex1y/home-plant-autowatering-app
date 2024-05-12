package ru.filimonov.hpa.ui.plant.details.model

import androidx.compose.runtime.Composable
import ru.filimonov.hpa.R
import ru.filimonov.hpa.ui.util.formatAsTemperature

data class AirTempConfig(
    val min: Float?,
    val max: Float?,
) : ParameterConfig(
    name = R.string.air_temp,
) {
    private val _minStr = min?.formatAsTemperature() ?: "＿"
    private val _maxStr = max?.formatAsTemperature() ?: "＿"

    @Composable
    override fun minStr(): String {
        return _minStr
    }

    @Composable
    override fun maxStr(): String {
        return _maxStr
    }
}
