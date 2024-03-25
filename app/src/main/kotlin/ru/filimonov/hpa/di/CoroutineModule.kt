package ru.filimonov.hpa.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import ru.filimonov.hpa.common.coroutine.CoroutineNames.APPLICATION_SCOPE
import ru.filimonov.hpa.common.coroutine.CoroutineNames.DEFAULT_DISPATCHER
import ru.filimonov.hpa.common.coroutine.CoroutineNames.IO_DISPATCHER
import ru.filimonov.hpa.common.coroutine.CoroutineNames.MAIN_DISPATCHER
import timber.log.Timber
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface CoroutineModule {
    companion object {
        @Named(IO_DISPATCHER)
        @Provides
        @Singleton
        fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

        @Named(MAIN_DISPATCHER)
        @Provides
        @Singleton
        fun mainDispatcher(): CoroutineDispatcher = Dispatchers.Main

        @Named(DEFAULT_DISPATCHER)
        @Provides
        @Singleton
        fun defaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

        @Named(APPLICATION_SCOPE)
        @Provides
        @Singleton
        fun applicationScope(
            @Named(MAIN_DISPATCHER) dispatcher: CoroutineDispatcher,
            exceptionHandler: CoroutineExceptionHandler
        ): CoroutineScope {
            return CoroutineScope(SupervisorJob() + exceptionHandler + dispatcher)
        }

        @Provides
        @Singleton
        fun coroutineExceptionHandler() = CoroutineExceptionHandler { _, exception ->
            System.err.println(exception.stackTraceToString())
            Timber.e(exception, "uncaught exception")
        }
    }
}
