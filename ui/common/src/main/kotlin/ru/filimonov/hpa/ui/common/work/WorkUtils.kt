package ru.filimonov.hpa.ui.common.work

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.workDataOf
import com.google.gson.Gson
import dagger.Lazy
import ru.filimonov.hpa.ui.common.R
import ru.filimonov.hpa.ui.common.work.di.WorkNotificationChannelModule
import kotlin.reflect.KClass

object WorkUtils {

    var jsonSerializer: Lazy<Gson> = Lazy<Gson> { Gson() }

    private const val OBJECT_DATA = "OBJECT_DATA"
    private const val PRIMITIVE_DATA = "PRIMITIVE_DATA"

    fun createNotificationBackgroundWork(message: String, context: Context): Notification {
        return NotificationCompat.Builder(context, WorkNotificationChannelModule.BACKGROUND_WORK)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSmallIcon(R.drawable.settings)
            .setContentTitle(context.getString(R.string.background_work))
            .setContentText(message)
            .build()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> Data.deserialize(clazz: KClass<T>): T? {
        require(
            this.keyValueMap.containsKey(OBJECT_DATA) ||
                    this.keyValueMap.containsKey(PRIMITIVE_DATA)
        ) {
            "Invalid data: it doesn't contain known data of class ${clazz.simpleName}"
        }
        return if (this.keyValueMap.containsKey(PRIMITIVE_DATA)) {
            this.keyValueMap[PRIMITIVE_DATA] as T
        } else {
            val serialized = keyValueMap[OBJECT_DATA] as String
            jsonSerializer.get().fromJson(serialized, clazz.java)
        }
    }

    fun <T> serialize(data: T): Data {
        return if (data.needSerializing()) {
            val serialized = jsonSerializer.get().toJson(data)
            workDataOf(OBJECT_DATA to serialized)
        } else {
            workDataOf(PRIMITIVE_DATA to data)
        }
    }

    private fun <T> T.needSerializing(): Boolean {
        return when (this) {
            null -> true
            is String, is Int, is Long, is Double, is Float, is Char, is Boolean -> false
            else -> true
        }
    }
}
