package ru.filimonov.hpa.domain.service.auth

import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow
import ru.filimonov.hpa.domain.model.UserAccount

interface UserAuthService {
    /**
     * Get an updated user account,
     * also updating the token or re-authentication, if it has expired.
     *
     * @return null - if the token has expired or it is null.
     */
    suspend fun getUserAccount(): UserAccount?

    /**
     * Re-authenticate using a refresh token.
     *
     * @return true - if successfully reathenticate, false - if refresh token's null or expired or other error
     */
    suspend fun reauthenticate(): Boolean

    /**
     * Delete all saved tokens.
     */
    suspend fun cleanSession()

    /**
     * Get auth token.
     */
    fun getIdToken(): Flow<String?>

    suspend fun authenticate(authCredential: AuthCredential): Result<Unit>
}
