package ru.filimonov.hpa.ui.common.navigation

import android.annotation.SuppressLint
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

interface Destination {
    val path: Path

    class Path {
        val raw: String
            get() {
                var hasManyOptArgs = false
                return parts.joinToString("") {
                    when (it) {
                        is String -> "/$it"
                        is Arg<*> -> "/${it.raw}"
                        is OptArg<*> -> {
                            if (hasManyOptArgs) {
                                "&${it.raw}"
                            } else {
                                hasManyOptArgs = true
                                "?${it.raw}"
                            }
                        }

                        else -> throw IllegalStateException("Unknown type!")
                    }
                }
            }

        val filled: String
            get() {
                var hasManyOptArgs = false
                return parts.joinToString("") {
                    when (it) {
                        is String -> "/$it"
                        is Arg<*> -> "/${it.filled}"
                        is OptArg<*> -> {
                            if (hasManyOptArgs) {
                                "&${it.filled}"
                            } else {
                                hasManyOptArgs = true
                                "?${it.filled}"
                            }
                        }

                        else -> throw IllegalStateException("Unknown type!")
                    }
                }
            }

        private val parts: MutableList<Any> = mutableListOf()

        operator fun div(path: String): Path {
            if (parts.isNotEmpty() && parts.last() is OptArg<*>) {
                throw IllegalStateException("OptArg must be last!")
            }
            parts.add(path)
            return this
        }

        operator fun <Value> div(arg: Arg<Value>): Path {
            if (parts.isNotEmpty() && parts.last() is OptArg<*>) {
                throw IllegalStateException("OptArg must be last!")
            }
            parts.add(arg)
            return this
        }

        operator fun <Value> div(arg: OptArg<Value>): Path {
            parts.add(arg)
            return this
        }
    }

    class Arg<Value>(private val name: String, private val value: Value) {
        val filled: String = "$value"
        val raw: String = "{$name}"

        fun toNavArgument(): NamedNavArgument {
            return navArgument(name = name) {
                type = toNavType(value)
                nullable = false
            }
        }
    }

    class OptArg<Value>(
        private val name: String,
        private val value: Value,
        private val isNullable: Boolean = false
    ) {
        val filled: String = "$name=$value"
        val raw: String = "$name={$name}"

        fun toNavArgument(): NamedNavArgument {
            return navArgument(name = name) {
                type = toNavType(value)
                nullable = isNullable
            }
        }
    }

    companion object {
        @SuppressLint("RestrictedApi")
        fun <T> toNavType(value: T): NavType<*> {
            return NavType.inferFromValueType(value)
        }
    }
}
