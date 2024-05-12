package ru.filimonov.hpa.widgets

import androidx.compose.runtime.compositionLocalOf

val LocalSnackbarState = compositionLocalOf<SnackbarState> { error("No SnackbarState provided") }
