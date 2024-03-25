package ru.filimonov.hpa.common.json

import com.google.gson.JsonSerializer
import kotlin.reflect.KClass

interface JsonSerializer<T : Any> : JsonSerializer<T> {
    val type: KClass<T>
}

class JsonSerializers(
    vararg val values: ru.filimonov.hpa.common.json.JsonSerializer<*>
)
