package ru.filimonov.hpa.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.filimonov.hpa.data.auth.GoogleAuthTokenServiceImpl
import ru.filimonov.hpa.data.auth.UserAuthServiceImpl
import ru.filimonov.hpa.domain.auth.GoogleAuthTokenService
import ru.filimonov.hpa.domain.auth.UserAuthService

@Module
@InstallIn(SingletonComponent::class)
internal interface ServiceModule {
    @Binds
    fun userAuthService(i: UserAuthServiceImpl): UserAuthService

    @Binds
    fun googleAuthTokenService(i: GoogleAuthTokenServiceImpl): GoogleAuthTokenService
}
