package ru.filimonov.hpa

import android.app.Application
import android.app.NotificationManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.gson.Gson
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import ru.filimonov.hpa.log.LogTree
import ru.filimonov.hpa.ui.common.work.WorkUtils
import timber.log.Timber
import java.util.Optional
import javax.inject.Inject

@HiltAndroidApp
class HpaApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var notificationManager: Optional<NotificationManager>

    @Inject
    lateinit var jsonSerializer: Lazy<Gson>

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        WorkUtils.jsonSerializer = jsonSerializer

        Timber.plant(LogTree())

        Thread.currentThread().setUncaughtExceptionHandler { _, error ->
            Timber.e(error, "Uncaught exception:")
        }
    }
}
