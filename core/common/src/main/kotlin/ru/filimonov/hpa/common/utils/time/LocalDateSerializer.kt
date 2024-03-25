package ru.filimonov.hpa.common.utils.time

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import dagger.multibindings.IntoSet
import ru.filimonov.hpa.common.json.JsonDeserializer
import ru.filimonov.hpa.common.json.JsonSerializer
import ru.filimonov.hpa.common.utils.time.DateUtils.toEpochMillis
import ru.filimonov.hpa.common.utils.time.DateUtils.toLocalDate
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class LocalDateSerializer @Inject constructor() : JsonSerializer<LocalDate> {
    override val type: KClass<LocalDate> = LocalDate::class

    override fun serialize(
        src: LocalDate?,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return src?.let {
            JsonPrimitive(it.toEpochMillis(ZoneOffset.UTC))
        } ?: JsonNull.INSTANCE
    }
}

@Singleton
class LocalDateDeserializer @Inject constructor() : JsonDeserializer<LocalDate> {
    override val type: KClass<LocalDate> = LocalDate::class

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDate? {
        return json?.let {
            if (it.isJsonNull)
                null
            else
                it.asLong.toLocalDate(ZoneOffset.UTC)
        }
    }
}
