package ru.filimonov.hpa.domain.auth

import kotlinx.coroutines.flow.Flow

interface GoogleAuthTokenService {
    fun getIdToken(): Flow<String?>
    suspend fun setIdToken(idToken: String?): Result<Unit>
    suspend fun resetIdToken(): Result<Unit>
    fun getRefreshToken(): Flow<String?>
    suspend fun setRefreshToken(refreshToken: String?): Result<Unit>
    suspend fun resetRefreshToken(): Result<Unit>
}
