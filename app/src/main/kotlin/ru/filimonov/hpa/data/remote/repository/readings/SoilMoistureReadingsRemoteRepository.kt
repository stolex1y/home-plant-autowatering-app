package ru.filimonov.hpa.data.remote.repository.readings

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.filimonov.hpa.data.remote.model.readings.SensorReadingResponse
import java.util.UUID

interface SoilMoistureReadingsRemoteRepository {
    @GET("devices/{deviceId}/readings/soil-moisture/last")
    suspend fun getLast(@Path("deviceId") deviceId: UUID): SensorReadingResponse<Float>

    @GET("devices/{deviceId}/readings/soil-moisture/period?unit=hour")
    suspend fun getForPeriodByHour(
        @Path("deviceId") deviceId: UUID,
        @Query("from") fromTimestamp: Long,
        @Query("to") toTimestamp: Long
    ): List<SensorReadingResponse<Float>>
}
