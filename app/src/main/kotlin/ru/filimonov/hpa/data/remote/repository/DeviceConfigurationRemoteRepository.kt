package ru.filimonov.hpa.data.remote.repository

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.filimonov.hpa.data.remote.model.device.DeviceConnectionStatus
import ru.filimonov.hpa.domain.model.device.DomainDeviceConfiguration
import ru.filimonov.hpa.domain.model.device.DomainDeviceInfo

interface DeviceConfigurationRemoteRepository {
    @GET("/device")
    suspend fun getInfo(): DomainDeviceInfo

    @GET("/connection")
    suspend fun getConnectionStatus(): DeviceConnectionStatus

    @POST("/config")
    suspend fun updateConfiguration(@Body deviceConfiguration: DomainDeviceConfiguration): Response<Unit>

    @POST("/switch-mode")
    suspend fun switchMode(): Response<Unit>
}
