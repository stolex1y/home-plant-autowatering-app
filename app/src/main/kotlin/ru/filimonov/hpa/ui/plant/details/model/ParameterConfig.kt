package ru.filimonov.hpa.ui.plant.details.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable

abstract class ParameterConfig(
    @StringRes val name: Int
) {
    @Composable
    abstract fun minStr(): String

    @Composable
    abstract fun maxStr(): String
}
