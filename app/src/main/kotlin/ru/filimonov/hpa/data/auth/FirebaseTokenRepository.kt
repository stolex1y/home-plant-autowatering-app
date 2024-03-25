package ru.filimonov.hpa.data.auth

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseTokenRepository @Inject constructor() {
    suspend fun getRefreshToken(customToken: String): Result<String> {
        return Result.success("AMf-vBwnYrg5jWTrEYCSkxexuMEoAACc34UzSQ6g8UPrVlTiJDpLu6DNMZMSh8dXj7WP2jLQPDEx2rqzXtqJ6HDB9DZTFgWJ2uZYfs-jozipyrXSBIUk9JubPDaC6nea1eyXza8aWicGmGHRDtmTiJGQ7x9sOlUYl3b_-rZU545ZoNyaYZohelIk47Ufnvrx8r7dV3BHUtzi5WVuuPArwiO_izI-5eUGMw")
    }

    suspend fun getIdToken(refreshToken: String): Result<String> {
        return Result.failure(Throwable("refresh token is expired"))
    }
}
