package ru.filimonov.hpa.data.remote.repository

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @GET("plants")
    suspend fun getAll(): List<PlantResponse>

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
    ): Response<Unit>

    @Multipart
    @PUT("plants/{plantId}/photo")
    suspend fun updatePhoto(
        @Path("plantId") plantId: UUID,
        @Part photo: MultipartBody.Part
    ): Response<Unit>
}
