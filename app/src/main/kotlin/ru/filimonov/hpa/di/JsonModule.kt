package ru.filimonov.hpa.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.filimonov.hpa.common.json.JsonDeserializers
import ru.filimonov.hpa.common.json.JsonSerializers
import ru.filimonov.hpa.common.utils.time.LocalDateDeserializer
import ru.filimonov.hpa.common.utils.time.LocalDateSerializer
import ru.filimonov.hpa.common.utils.time.LocalTimeDeserializer
import ru.filimonov.hpa.common.utils.time.LocalTimeSerializer
import ru.filimonov.hpa.common.utils.time.ZonedDateTimeDeserializer
import ru.filimonov.hpa.common.utils.time.ZonedDateTimeSerializer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface JsonModule {
    companion object {
        @Provides
        @Singleton
        fun gsonSerializer(
            serializers: JsonSerializers,
            deserializers: JsonDeserializers
        ): Gson {
            val gsonBuilder = GsonBuilder()
            serializers.values.forEach {
                gsonBuilder.registerTypeAdapter(it.type.java, it)
            }
            deserializers.values.forEach {
                gsonBuilder.registerTypeAdapter(it.type.java, it)
            }
            return gsonBuilder.create()
        }

        @Provides
        @Singleton
        fun jsonSerializers(
            localDateSerializer: LocalDateSerializer,
            localTimeSerializer: LocalTimeSerializer,
            zonedDateTimeSerializer: ZonedDateTimeSerializer,
        ): JsonSerializers {
            return JsonSerializers(
                localDateSerializer,
                localTimeSerializer,
                zonedDateTimeSerializer,
            )
        }

        @Provides
        @Singleton
        fun jsonDeserializers(
            localDateDeserializer: LocalDateDeserializer,
            localTimeDeserializer: LocalTimeDeserializer,
            zonedDateTimeDeserializer: ZonedDateTimeDeserializer
        ): JsonDeserializers {
            return JsonDeserializers(
                localDateDeserializer,
                localTimeDeserializer,
                zonedDateTimeDeserializer,
            )
        }
    }
}
