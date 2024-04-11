package ru.filimonov.hpa.data.remote.repository

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import ru.filimonov.hpa.data.remote.model.plant.AddPlantRequest
import ru.filimonov.hpa.data.remote.model.plant.PlantResponse
import ru.filimonov.hpa.data.remote.model.plant.UpdatePlantRequest
import java.util.UUID

interface PlantRemoteRepository {
    @GET("plants")
    suspend fun getAllInList(@Query("ids") ids: List<UUID>): List<PlantResponse>

    @GET("plants/{plantId}")
    suspend fun get(@Path("plantId") plantId: UUID): PlantResponse

    @PUT("plants/{plantId}")
    suspend fun update(
        @Path("deviceId") deviceId: UUID,
        @Body updatePlantRequest: UpdatePlantRequest,
    ): PlantResponse

    @POST("plants")
    suspend fun add(
        @Body addPlantRequest: AddPlantRequest
    ): PlantResponse

    @DELETE("plants/{plantId}")
    suspend fun delete(
        @Path("plantId") plantId: UUID,
    )
}
