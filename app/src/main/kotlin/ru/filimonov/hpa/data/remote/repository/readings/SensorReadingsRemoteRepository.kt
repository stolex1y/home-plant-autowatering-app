package ru.filimonov.hpa.data.remote.repository.readings

import retrofit2.http.GET
import retrofit2.http.Path
import ru.filimonov.hpa.data.remote.model.readings.LastSensorReadingsResponse
import java.util.UUID

interface SensorReadingsRemoteRepository {
    @GET("devices/{deviceId}/readings/last")
    suspend fun getAllLast(@Path("deviceId") deviceId: UUID): LastSensorReadingsResponse
}
