package ru.filimonov.hpa.common.utils.time

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import ru.filimonov.hpa.common.json.JsonDeserializer
import ru.filimonov.hpa.common.json.JsonSerializer
import ru.filimonov.hpa.common.utils.time.DateUtils.toEpochMillis
import ru.filimonov.hpa.common.utils.time.DateUtils.toLocalTime
import java.lang.reflect.Type
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class LocalTimeSerializer @Inject constructor() : JsonSerializer<LocalTime> {
    override val type: KClass<LocalTime> = LocalTime::class

    override fun serialize(
        src: LocalTime?,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return src?.let {
            JsonPrimitive(it.toEpochMillis())
        } ?: JsonNull.INSTANCE
    }
}

@Singleton
class LocalTimeDeserializer @Inject constructor() : JsonDeserializer<LocalTime> {
    override val type: KClass<LocalTime> = LocalTime::class

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalTime? {
        return json?.let {
            if (it.isJsonNull)
                null
            else
                it.asLong.toLocalTime()
        }
    }
}
