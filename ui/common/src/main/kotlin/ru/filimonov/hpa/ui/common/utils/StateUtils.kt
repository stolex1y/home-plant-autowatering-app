package ru.filimonov.hpa.ui.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun <T> StateFlow<T>.collectAsStateWhileResumed(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    context: CoroutineContext = EmptyCoroutineContext
): State<T> = collectAsStateWithLifecycle(
    lifecycleOwner = lifecycleOwner,
    context = context,
    minActiveState = Lifecycle.State.RESUMED
)

@Composable
fun <T> Flow<T>.collectAsStateWhileResumed(
    initialValue: T,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    context: CoroutineContext = EmptyCoroutineContext
): State<T> = collectAsStateWithLifecycle(
    initialValue = initialValue,
    lifecycleOwner = lifecycleOwner,
    context = context,
    minActiveState = Lifecycle.State.RESUMED
)

@Composable
fun LaunchedEffectEveryResuming(key1: Any?, block: suspend CoroutineScope.() -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(key1 = key1) {
        lifecycleOwner.repeatOnLifecycle(state = Lifecycle.State.RESUMED, block = block)
    }
}
