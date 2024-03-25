package ru.filimonov.hpa.log

import ru.filimonov.hpa.BuildConfig
import timber.log.Timber

class LogTree : Timber.DebugTree() {
    companion object {
        private const val GLOBAL_TAG: String = "HPA"
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority >= BuildConfig.LOG_LEVEL) {
            super.log(priority = priority, tag = "[${GLOBAL_TAG}] $tag", message = message, t = t)
        }
    }
}
