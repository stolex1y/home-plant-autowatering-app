package ru.filimonov.hpa.common.utils.time

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import ru.filimonov.hpa.common.json.JsonDeserializer
import ru.filimonov.hpa.common.json.JsonSerializer
import ru.filimonov.hpa.common.utils.time.DateUtils.toCalendar
import java.lang.reflect.Type
import java.util.Calendar
import javax.inject.Inject
import kotlin.reflect.KClass

class CalendarSerializer @Inject constructor() : JsonSerializer<Calendar> {
    override val type: KClass<Calendar> = Calendar::class

    override fun serialize(
        src: Calendar?,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return src?.let {
            JsonPrimitive(it.timeInMillis)
        } ?: JsonNull.INSTANCE
    }
}

class CalendarDeserializer @Inject constructor() : JsonDeserializer<Calendar> {
    override val type: KClass<Calendar> = Calendar::class

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Calendar? {
        return json?.let {
            if (it.isJsonNull)
                null
            else
                it.asLong.toCalendar()
        }
    }
}
