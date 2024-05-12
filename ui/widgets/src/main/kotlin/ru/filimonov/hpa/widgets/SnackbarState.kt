package ru.filimonov.hpa.widgets

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SnackbarState(
    val hostState: SnackbarHostState,
    val coroutineScope: CoroutineScope,
) {
    private var showingSnackbarJob: Job? = null

    @Composable
    fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration =
            if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
        onDismiss: () -> Unit = {},
        onAction: () -> Unit = {},
    ) {
        LaunchedEffect(key1 = coroutineScope) {
            showingSnackbarJob = coroutineScope.launch {
                val result = hostState.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                    withDismissAction = withDismissAction,
                    duration = duration
                )
                when (result) {
                    SnackbarResult.Dismissed -> onDismiss()
                    SnackbarResult.ActionPerformed -> onAction()
                }
            }
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun showSnackbar(
        @StringRes messageRes: Int,
        @StringRes actionLabelRes: Int? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration =
            if (actionLabelRes == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
        onDismiss: () -> Unit = {},
        onAction: () -> Unit = {},
    ) {
        val message = stringResource(id = messageRes)
        val actionLabel = actionLabelRes?.let { stringResource(id = it) }
        showSnackbar(
            message = message,
            actionLabel = actionLabel,
            withDismissAction = withDismissAction,
            duration = duration,
            onDismiss = onDismiss,
            onAction = onAction
        )
    }

    fun cancel() {
        showingSnackbarJob?.cancel()
        showingSnackbarJob = null
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun replaceSnackbar(
        @StringRes messageRes: Int,
        @StringRes actionLabelRes: Int? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration =
            if (actionLabelRes == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
        onDismiss: () -> Unit = {},
        onAction: () -> Unit = {},
    ) {
        cancel()
        showSnackbar(
            messageRes = messageRes,
            actionLabelRes = actionLabelRes,
            withDismissAction = withDismissAction,
            duration = duration,
            onDismiss = onDismiss,
            onAction = onAction,
        )
    }

    companion object {
        @Composable
        fun rememberSnackbarState(
            hostState: SnackbarHostState = remember { SnackbarHostState() },
            coroutineScope: CoroutineScope = rememberCoroutineScope(),
        ) = remember(hostState, coroutineScope) {
            SnackbarState(hostState, coroutineScope)
        }
    }
}
