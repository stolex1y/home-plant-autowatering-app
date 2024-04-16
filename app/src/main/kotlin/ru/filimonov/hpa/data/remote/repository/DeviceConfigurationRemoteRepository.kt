package ru.filimonov.hpa.data.remote.repository

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.filimonov.hpa.domain.model.device.DomainDeviceConfiguration
import ru.filimonov.hpa.domain.model.device.DomainDeviceInfo

interface DeviceConfigurationRemoteRepository {
    @GET("/")
    suspend fun getInfo(): DomainDeviceInfo

    @POST("/")
    suspend fun updateConfiguration(@Body deviceConfiguration: DomainDeviceConfiguration)
}
