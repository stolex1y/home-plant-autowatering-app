package ru.filimonov.hpa.ui.common.validation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

abstract class ValidatedEntity {
    private val props: MutableList<ValidatedProperty<*>> = mutableListOf()

    protected fun <T> addValidatedProperty(
        initialValue: T,
        condition: Condition<T> = Conditions.None(),
    ): ValidatedProperty<T> = ValidatedProperty(initialValue, condition).apply {
        props.add(this)
    }

    val isValidAsFlow: Flow<Boolean>
        get() {
            return combine(props.map { it.isValidAsFlow }) { propsValidity ->
                propsValidity.all { it }
            }.distinctUntilChanged()
        }

    val isValid: Boolean
        get() = props.all { it.isValid }


    val isValidAsState: State<Boolean>
        @Composable get() = isValidAsFlow.collectAsState(initial = isValid)

    val isNotValid: Boolean
        get() = !isValid
}
