package ru.filimonov.hpa.ui.common.validation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import ru.filimonov.hpa.ui.common.validation.Condition.Companion.isValid

class ValidatedProperty<T>(
    initialValue: T,
    private val condition: Condition<T> = Conditions.None(),
) {

    private val _state: MutableStateFlow<T> = MutableStateFlow(initialValue)

    val value: T
        get() = _state.value

    val asFlow: StateFlow<T> = _state.asStateFlow()

    val asState: State<T>
        @Composable get() = _state.collectAsState()

    val isValid: Boolean
        get() = condition.isValid(value)

    val isValidAsFlow: Flow<Boolean>
        get() = _validationResult.map { it.isValid }.distinctUntilChanged()

    private val _validationResult: MutableStateFlow<ValidationResult> =
        MutableStateFlow(condition.validate(initialValue))

    val validationResultAsState: State<ValidationResult>
        @Composable get() = _validationResult.collectAsState()

    val validationResultAsFlow: StateFlow<ValidationResult> = _validationResult.asStateFlow()
    val validationResult: ValidationResult
        get() = _validationResult.value

    fun set(value: T) {
        if (value != _state.value) {
            _state.value = value
            _validationResult.value = condition.validate(value)
        }
    }

    fun get() = value

    override fun toString(): String {
        return value.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null)
            return false

        if (other is ValidatedProperty<*>) {
            return if ((other.value == null) xor (this.value == null))
                false
            else if ((other.value == null) and (this.value == null))
                true
            else if (other.value!!::class == this.value!!::class)
                this.value!! == other.value!!
            else
                false
        } else {
            if (this.value == null)
                return false
            else if (other::class == this.value!!::class)
                return this.value == other
        }
        return false
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
