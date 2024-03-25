package ru.filimonov.hpa.ui.common.work.di

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import ru.filimonov.hpa.ui.common.R
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface WorkNotificationChannelModule {
    companion object {
        const val BACKGROUND_WORK = "BACKGROUND"

        @Provides
        @Singleton
        @IntoSet
        fun workNotificationChannel(@ApplicationContext appContext: Context): NotificationChannel {
            return NotificationChannel(
                BACKGROUND_WORK,
                appContext.getString(R.string.background_work),
                NotificationManager.IMPORTANCE_MIN
            )
        }
    }
}
