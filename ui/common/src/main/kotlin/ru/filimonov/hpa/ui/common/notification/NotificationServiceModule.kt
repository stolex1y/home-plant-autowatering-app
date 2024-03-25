package ru.filimonov.hpa.ui.common.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.filimonov.hpa.common.utils.OptionalExtensions.toOptional
import java.util.Optional
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NotificationServiceModule {
    companion object {
        @Provides
        @Singleton
        internal fun notificationChannels(
            notificationChannels: Set<NotificationChannel>,
            @ApplicationContext context: Context
        ): List<NotificationChannel> {
            val notificationManager =
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)
            notificationManager?.createNotificationChannels(notificationChannels.toList())
            return notificationManager?.notificationChannels ?: emptyList()
        }

        @Provides
        fun notificationManager(
            @ApplicationContext context: Context,
            notificationChannels: List<NotificationChannel>
        ): Optional<NotificationManager> {
            val notificationManager =
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)
            return notificationManager.toOptional()
        }
    }
}
