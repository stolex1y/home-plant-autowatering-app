package ru.filimonov.hpa.ui.common.validation

import androidx.annotation.StringRes

interface ValidationResult {
    val isValid: Boolean
    val isNotValid: Boolean
        get() = !isValid

    val errorMessageRes: Int?

    val errorMessageArgs: Array<out Any>

    companion object {
        fun create(
            isValid: Boolean,
            errorMessage: Int,
            vararg errorMessageArgs: Any
        ): ValidationResult {
            return if (isValid) {
                valid()
            } else {
                invalid(errorMessage, errorMessageArgs)
            }
        }

        fun valid(): ValidationResult {
            return object : ValidationResult {
                override val isValid: Boolean
                    get() = true
                override val errorMessageRes: Int?
                    get() = null

                override val errorMessageArgs: Array<Any>
                    get() = emptyArray()
            }
        }

        fun invalid(
            @StringRes errorMessageRes: Int,
            vararg errorMessageArgs: Any,
        ): ValidationResult {
            return object : ValidationResult {
                override val isValid: Boolean
                    get() = false
                override val errorMessageRes: Int
                    get() = errorMessageRes

                override val errorMessageArgs: Array<out Any>
                    get() = errorMessageArgs
            }
        }
    }
}
