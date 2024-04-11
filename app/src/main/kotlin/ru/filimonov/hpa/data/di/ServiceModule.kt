package ru.filimonov.hpa.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.filimonov.hpa.data.service.DeviceServiceImpl
import ru.filimonov.hpa.data.service.PlantServiceImpl
import ru.filimonov.hpa.data.service.auth.OAuthTokenServiceImpl
import ru.filimonov.hpa.data.service.auth.UserAuthServiceImpl
import ru.filimonov.hpa.domain.service.auth.OAuthTokenService
import ru.filimonov.hpa.domain.service.auth.UserAuthService
import ru.filimonov.hpa.domain.service.device.DeviceService
import ru.filimonov.hpa.domain.service.device.PlantService

@Module
@InstallIn(SingletonComponent::class)
internal interface ServiceModule {
    @Binds
    fun userAuthService(i: UserAuthServiceImpl): UserAuthService

    @Binds
    fun deviceService(i: DeviceServiceImpl): DeviceService

    @Binds
    fun plantService(i: PlantServiceImpl): PlantService

    @Binds
    fun googleAuthTokenService(i: OAuthTokenServiceImpl): OAuthTokenService
}
