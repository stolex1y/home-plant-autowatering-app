package ru.filimonov.hpa.ui.common.validation

import androidx.annotation.StringRes
import java.io.Serializable

fun interface Condition<T> : Serializable {
    fun validate(value: T): ValidationResult

    companion object {
        inline fun <V> create(
            @StringRes errorMessageRes: Int,
            crossinline isValueValid: (value: V?) -> Boolean
        ): Condition<V?> = Condition { value ->
            if (isValueValid(value)) {
                ValidationResult.valid()
            } else {
                ValidationResult.invalid(errorMessageRes)
            }
        }

        fun <T> Condition<T>.isValid(value: T): Boolean {
            return this.validate(value).isValid
        }
    }
}
