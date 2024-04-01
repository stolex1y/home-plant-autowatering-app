package ru.filimonov.hpa.ui.util.validation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

abstract class ValidatedEntity {
    private val _props: MutableList<ValidatedProperty<Any>> = mutableListOf()
    val props: List<ValidatedProperty<Any>> = _props

    protected fun <T> addValidatedProperty(
        initialValue: T,
        condition: Condition<T> = Conditions.None(),
    ): ValidatedProperty<T> =
        ValidatedProperty(initialValue, condition)

    val isValidAsFlow: Flow<Boolean>
        get() {
            return combine(_props.map { it.isValidAsFlow }) { propsValidity ->
                propsValidity.all { it }
            }.distinctUntilChanged()
        }

    val isValid: Boolean
        get() = _props.all { it.isValid }


    val isValidAsState: State<Boolean>
        @Composable get() = isValidAsFlow.collectAsState(initial = isValid)

    val isNotValid: Boolean
        get() = !isValid
}
