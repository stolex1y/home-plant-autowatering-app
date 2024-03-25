package ru.filimonov.hpa.ui.common.notification

object NotificationUtils {
    private var nextId: Int = 0

    @Synchronized
    fun getUniqueNotificationId(): Int = nextId++
}
