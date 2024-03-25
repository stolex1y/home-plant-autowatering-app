package ru.filimonov.hpa.common.json

import com.google.gson.JsonDeserializer
import kotlin.reflect.KClass

interface JsonDeserializer<T : Any> : JsonDeserializer<T> {
    val type: KClass<T>
}

class JsonDeserializers(
    vararg val values: ru.filimonov.hpa.common.json.JsonDeserializer<*>
)
