package ru.filimonov.hpa.data.remote.repository

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.filimonov.hpa.data.remote.model.device.AddDeviceRequest
import ru.filimonov.hpa.data.remote.model.device.DeviceResponse
import ru.filimonov.hpa.data.remote.model.device.UpdateDeviceRequest
import java.util.UUID

interface DeviceRemoteRepository {
    @GET("devices")
    suspend fun getAll(): List<DeviceResponse>

    @GET("devices/{deviceId}")
    suspend fun get(@Path("deviceId") deviceId: UUID): DeviceResponse

    @PUT("devices/{deviceId}")
    suspend fun update(
        @Path("deviceId") deviceId: UUID,
        @Body updateDeviceRequest: UpdateDeviceRequest
    ): DeviceResponse

    @POST("devices")
    suspend fun add(
        @Body addDeviceRequest: AddDeviceRequest
    ): DeviceResponse

    @DELETE("devices/{deviceId}")
    suspend fun delete(
        @Path("deviceId") deviceId: UUID,
    )
}
