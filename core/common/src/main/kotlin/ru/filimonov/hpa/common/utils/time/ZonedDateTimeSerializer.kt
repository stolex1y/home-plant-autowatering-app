package ru.filimonov.hpa.common.utils.time

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import ru.filimonov.hpa.common.json.JsonDeserializer
import ru.filimonov.hpa.common.json.JsonSerializer
import ru.filimonov.hpa.common.utils.time.DateUtils.toEpochMillis
import ru.filimonov.hpa.common.utils.time.DateUtils.toZonedDateTime
import java.lang.reflect.Type
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.reflect.KClass

class ZonedDateTimeSerializer @Inject constructor() : JsonSerializer<ZonedDateTime> {
    override val type: KClass<ZonedDateTime> = ZonedDateTime::class

    override fun serialize(
        src: ZonedDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonArray(2).apply {
            add(src?.zone?.id)
            add(src?.toEpochMillis())
        }
    }
}

class ZonedDateTimeDeserializer @Inject constructor() : JsonDeserializer<ZonedDateTime> {
    override val type: KClass<ZonedDateTime> = ZonedDateTime::class

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ZonedDateTime? {
        return json?.asJsonArray?.run {
            if (get(0).isJsonNull)
                return@run null

            val zone = ZoneId.of(get(0).asJsonPrimitive.asString)
            val millis = get(1).asJsonPrimitive.asLong
            (millis to zone).toZonedDateTime()
        }
    }
}
