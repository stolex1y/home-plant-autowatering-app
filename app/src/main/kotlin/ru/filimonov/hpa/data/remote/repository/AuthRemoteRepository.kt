package ru.filimonov.hpa.data.remote.repository

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.filimonov.hpa.data.remote.model.auth.AuthResponse
import ru.filimonov.hpa.data.remote.model.auth.ReauthRequest
import ru.filimonov.hpa.data.remote.model.auth.ReauthResponse

interface AuthRemoteRepository {
    @GET("auth")
    suspend fun auth(): AuthResponse

    @POST("reauth")
    suspend fun reauth(@Body reauthRequest: ReauthRequest): ReauthResponse
}
