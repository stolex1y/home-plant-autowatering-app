package ru.filimonov.hpa.data.service.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import ru.filimonov.hpa.data.remote.isClientError
import ru.filimonov.hpa.data.remote.model.auth.ReauthRequest
import ru.filimonov.hpa.data.remote.repository.AuthRemoteRepository
import ru.filimonov.hpa.domain.model.UserAccount
import ru.filimonov.hpa.domain.service.auth.OAuthTokenService
import ru.filimonov.hpa.domain.service.auth.UserAuthService
import timber.log.Timber
import javax.inject.Inject

internal class UserAuthServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authRemoteRepository: AuthRemoteRepository,
    private val oAuthTokenService: OAuthTokenService,
) : UserAuthService {

    override suspend fun getUserAccount(): UserAccount? {
        if (firebaseAuth.currentUser != null) {
            if (reloadCurrentUser()) {
                Timber.d("return reloaded user account")
                return firebaseAuth.currentUser?.toUserAccount()
            }
        }
        Timber.d("return empty user account")
        return null
    }

    override suspend fun reauthenticate(): Boolean {
        Timber.d("try reauthenticate")
        val refreshToken = oAuthTokenService.getRefreshToken().first()
        if (refreshToken == null) {
            Timber.d("fail reauthenticate: refresh token is empty")
            return false
        }
        try {
            val idToken = authRemoteRepository.reauth(ReauthRequest(refreshToken)).idToken
            oAuthTokenService.setIdToken(idToken = idToken)
            Timber.d("success reauthenticate")
            return true
        } catch (ex: Throwable) {
            Timber.d("fail reauthenticate: ${ex.localizedMessage}")
            if (ex is HttpException && ex.isClientError()) {
                cleanSession()
            }
            return false
        }
    }

    override suspend fun cleanSession() {
        Timber.d("clean session, remove all saved tokens")
        firebaseAuth.signOut()
        oAuthTokenService.resetAll()
    }

    override fun getIdToken(): Flow<String?> {
        return oAuthTokenService.getIdToken()
    }

    override suspend fun authenticate(authCredential: AuthCredential): Result<Unit> {
        return runCatching {
            Timber.d("try authenticate")
            firebaseAuth.signInWithCredential(authCredential).await()
            val idToken = firebaseAuth.currentUser?.getIdToken(true)?.await()?.token!!
            oAuthTokenService.setIdToken(idToken = idToken).onFailure {
                Timber.e(it, "setting id token failed")
                return Result.failure(it)
            }
            val refreshToken = authRemoteRepository.auth().refreshToken
            oAuthTokenService.setRefreshToken(refreshToken = refreshToken).onFailure {
                Timber.e(it, "setting refresh token failed")
                return Result.failure(it)
            }
            Timber.d("successful authentication")
        }.onFailure {
            Timber.e(it, "authentication failed")
            cleanSession()
        }
    }

    /**
     * Reload account if exists and not expired.
     */
    private suspend fun reloadCurrentUser(): Boolean {
        Timber.d("try reload current user")
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            Timber.d("current user is empty")
            return false
        }
        try {
            firebaseUser.reload().await()
            Timber.d("success reload current user")
            return true
        } catch (ex: Throwable) {
            Timber.e(ex, "fail reload current user")
            return false
        }
    }

    private fun FirebaseUser.toUserAccount() = UserAccount(
        name = displayName,
        email = email,
        photoUrl = photoUrl?.toString(),
    )
}
