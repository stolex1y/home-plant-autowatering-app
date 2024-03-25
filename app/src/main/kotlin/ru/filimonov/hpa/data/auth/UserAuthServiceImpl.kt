package ru.filimonov.hpa.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import ru.filimonov.hpa.domain.auth.GoogleAuthTokenService
import ru.filimonov.hpa.domain.auth.UserAccount
import ru.filimonov.hpa.domain.auth.UserAuthService
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UserAuthServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseTokenRepository: FirebaseTokenRepository,
    private val googleAuthTokenService: GoogleAuthTokenService,
) : UserAuthService {

    override suspend fun getUserAccount(): UserAccount? {
        if (firebaseAuth.currentUser == null) {
            if (reauthenticate() && reloadCurrentUser()) {
                // reload success after reauth with saved refresh token
                return firebaseAuth.currentUser?.toUserAccount()
            }
        } else {
            if (reloadCurrentUser()) {
                // reload success
                return firebaseAuth.currentUser?.toUserAccount()
            } else if (reauthenticate() && reloadCurrentUser()) {
                // reload success after reauth
                return firebaseAuth.currentUser?.toUserAccount()
            }
        }
        return null
    }

    override suspend fun reauthenticate(): Boolean {
        Timber.d("try reauthenticate")
        val refreshToken = googleAuthTokenService.getRefreshToken().first() ?: return false
        try {
            val idToken = firebaseTokenRepository.getIdToken(refreshToken).getOrThrow()
            firebaseAuth.signInWithCredential(
                GoogleAuthProvider.getCredential(
                    idToken, null
                )
            ).await()
            reloadIdToken()
            Timber.d("success reauthenticate")
            return true
        } catch (ex: Throwable) {
            googleAuthTokenService.resetIdToken()
            googleAuthTokenService.resetRefreshToken()
            Timber.d("fail reauthenticate: ${ex.localizedMessage}")
        }
        return false
    }

    override suspend fun cleanSession() {
        firebaseAuth.signOut()
        googleAuthTokenService.resetRefreshToken()
        googleAuthTokenService.resetIdToken()
    }

    override fun getTokenId(): Flow<String?> {
        return googleAuthTokenService.getIdToken()
    }

    /**
     * Reload account if exists and not expired.
     */
    private suspend fun reloadCurrentUser(): Boolean {
        Timber.d("try reload current user")
        val firebaseUser = firebaseAuth.currentUser ?: return false
        val result = runCatching {
            firebaseUser.reload().await()
            true
        }.onSuccess {
            Timber.d("success reload current user")
        }.onFailure {
            Timber.d("fail reload current user")
        }
        return result.getOrNull() == true
    }

    private fun FirebaseUser.toUserAccount() = UserAccount(
        name = displayName,
        email = email,
        photoUrl = photoUrl?.toString(),
    )

    /**
     * Reload ID token if user exists and not expired.
     */
    private suspend fun reloadIdToken(): Boolean {
        Timber.d("try reload id token")
        val firebaseUser = firebaseAuth.currentUser ?: return false
        val result = googleAuthTokenService.setIdToken(firebaseUser.getIdToken(true).await().token)
            .onFailure {
                Timber.e("set id token failure", it)
            }.onSuccess {
                Timber.d("success reload id token")
            }
        return result.getOrNull() != null
    }
}
