package ru.filimonov.hpa.data.remote.repository

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.filimonov.hpa.domain.model.DeviceConfiguration
import ru.filimonov.hpa.domain.model.DeviceInfo

interface DeviceConfigurationRemoteRepository {
    @GET("/")
    suspend fun getInfo(): DeviceInfo

    @POST("/")
    suspend fun updateConfiguration(@Body deviceConfiguration: DeviceConfiguration)
}
