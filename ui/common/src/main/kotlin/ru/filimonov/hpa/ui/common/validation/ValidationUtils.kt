package ru.filimonov.hpa.ui.common.validation

import androidx.compose.runtime.saveable.listSaver
import com.google.gson.Gson
import dagger.Lazy

object ValidationUtils {
    var jsonSerializer: Lazy<Gson> = Lazy<Gson> { Gson() }

    inline fun <reified T : ValidatedEntity> T.getSaver(crossinline constructor: () -> T) =
        listSaver<T, String>(
            save = {
                props.map {
                    jsonSerializer.get().toJson(it.value)
                }
            },
            restore = { restored ->
                constructor().apply {
                    restored.forEachIndexed { index, jsonValue ->
                        props[index].set(jsonSerializer.get().fromJson(jsonValue, T::class.java))
                    }
                }
            }
        )
}
