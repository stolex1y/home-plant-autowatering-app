package ru.filimonov.hpa.ui.plant.details.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.filimonov.hpa.R

data class LightLuxConfig(
    val min: Int?,
    val max: Int?,
) : ParameterConfig(
    name = R.string.light_level
) {
    private val _min = min?.toString() ?: "＿"
    private val _max = max?.toString() ?: "＿"

    @Composable
    override fun minStr(): String {
        return if (min == null)
            _min
        else
            _min + " " + stringResource(id = R.string.lx)
    }

    @Composable
    override fun maxStr(): String {
        return if (max == null)
            _max
        else
            _max + " " + stringResource(id = R.string.lx)
    }
}
