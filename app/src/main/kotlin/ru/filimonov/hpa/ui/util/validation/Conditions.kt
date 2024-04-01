package ru.filimonov.hpa.ui.util.validation

import androidx.annotation.StringRes
import ru.filimonov.hpa.R
import java.io.Serializable

object Conditions {
    class None<T> : Condition<T>, Serializable {
        override fun validate(value: T): ValidationResult {
            return ValidationResult.valid()
        }
    }

    class RequiredField<T : CharSequence>(@StringRes private val errorStringRes: Int) :
        Condition<T>, Serializable {
        override fun validate(value: T): ValidationResult {
            return if (value.isBlank()) {
                ValidationResult.invalid(errorStringRes)
            } else {
                ValidationResult.valid()
            }
        }
    }


    class TextMaxLength<T : CharSequence?>(
        val maxLength: Int,
        @StringRes private val errorStringRes: Int
    ) : Condition<T?>, Serializable {
        override fun validate(value: T?): ValidationResult {
            return if ((value?.length ?: 0) <= maxLength) {
                ValidationResult.valid()
            } else {
                ValidationResult.invalid(errorStringRes)
            }
        }
    }

    class TextMinLength<T : CharSequence>(
        val minLength: Int,
        @StringRes private val errorStringRes: Int
    ) : Condition<T?>, Serializable {

        override fun validate(value: T?): ValidationResult {
            return if ((value?.length ?: 0) >= minLength) {
                ValidationResult.valid()
            } else {
                ValidationResult.invalid(errorStringRes)
            }
        }
    }

    class TextLengthRange<T : CharSequence>(
        val textLenMin: Int,
        val textLenMax: Int,
        @StringRes private val errorStringRes: Int
    ) : Condition<T?>, Serializable {
        override fun validate(value: T?): ValidationResult {
            val textLength = (value?.length ?: 0)

            return if (textLength in textLenMin..textLenMax) {
                ValidationResult.valid()
            } else {
                ValidationResult.invalid(errorStringRes)
            }
        }
    }

    class TextLength<T : CharSequence>(
        val textLength: Int,
        @StringRes private val errorStringRes: Int
    ) : Condition<T?>, Serializable {

        override fun validate(value: T?): ValidationResult {
            return if ((value?.length ?: 0) == textLength) {
                ValidationResult.valid()
            } else {
                ValidationResult.invalid(errorStringRes)
            }
        }
    }

    class NotEmptyList<T : List<V>, V>(@StringRes private val errorStringRes: Int) :
        Condition<T>,
        Serializable {
        override fun validate(value: T): ValidationResult {
            return if (value.isEmpty())
                ValidationResult.invalid(errorStringRes)
            else
                ValidationResult.valid()
        }
    }

    open class RegEx<T : CharSequence>(
        private val regEx: Regex,
        @StringRes private val errorStringRes: Int
    ) : Condition<T>, Serializable {
        override fun validate(value: T): ValidationResult {
            return if (value.matches(regEx)) {
                ValidationResult.valid()
            } else {
                ValidationResult.invalid(errorStringRes)
            }
        }
    }

    val MacAddress = RegEx<String>(
        regEx = Regex("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$"),
        errorStringRes = R.string.not_valid_mac
    )
}
