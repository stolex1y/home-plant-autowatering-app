package ru.filimonov.hpa.data.remote.repository

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.filimonov.hpa.data.remote.model.AuthResponse
import ru.filimonov.hpa.data.remote.model.ReauthRequest
import ru.filimonov.hpa.data.remote.model.ReauthResponse

interface AuthRemoteRepository {
    companion object {
        const val AUTH_PATH = "auth"
        const val REAUTH_PATH = "reauth"
    }

    @GET(AUTH_PATH)
    suspend fun auth(): AuthResponse

    @POST(REAUTH_PATH)
    suspend fun reauth(@Body reauthRequest: ReauthRequest): ReauthResponse
}
