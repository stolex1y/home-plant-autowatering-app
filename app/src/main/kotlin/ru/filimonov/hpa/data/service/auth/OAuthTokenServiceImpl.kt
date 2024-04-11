package ru.filimonov.hpa.data.service.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import ru.filimonov.hpa.common.coroutine.CoroutineNames
import ru.filimonov.hpa.common.utils.combineResults
import ru.filimonov.hpa.data.di.DataStoreModule
import ru.filimonov.hpa.domain.service.auth.OAuthTokenService
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class OAuthTokenServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @Named(DataStoreModule.AUTH_PREFERENCES) private val dataStore: DataStore<Preferences>,
    @Named(CoroutineNames.IO_DISPATCHER) private val dispatcher: CoroutineDispatcher,
) : OAuthTokenService {
    companion object {
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_ID_TOKEN = stringPreferencesKey("id_token")
    }

    override fun getIdToken(): Flow<String?> {
        return dataStore.data
            .map { pref ->
                pref[KEY_ID_TOKEN]
            }.distinctUntilChanged()
            .flowOn(dispatcher)
            .onStart { Timber.d("get id token flow") }
            .onEach { Timber.d("get new id token from flow") }
    }

    override suspend fun setIdToken(idToken: String?): Result<Unit> {
        return withContext(dispatcher) {
            runCatching {
                dataStore.edit { pref ->
                    if (idToken == null) {
                        pref.remove(KEY_ID_TOKEN)
                        Timber.d("success reset id token")
                    } else {
                        pref[KEY_ID_TOKEN] = idToken
                        Timber.d("success update id token")
                    }
                }
                return@runCatching
            }
        }
    }

    override suspend fun resetIdToken(): Result<Unit> {
        return setIdToken(null)
    }

    override fun getRefreshToken(): Flow<String?> {
        return dataStore.data
            .map { pref ->
                pref[KEY_REFRESH_TOKEN]
            }.distinctUntilChanged()
            .flowOn(dispatcher)
            .onStart { Timber.d("get refresh token flow") }
            .onEach { Timber.d("get new refresh token from flow") }
    }

    override suspend fun resetAll(): Result<Unit> {
        return combineResults(resetIdToken(), resetRefreshToken())
    }

    override suspend fun setRefreshToken(refreshToken: String?): Result<Unit> {
        return withContext(dispatcher) {
            runCatching {
                dataStore.edit { pref ->
                    if (refreshToken == null) {
                        pref.remove(KEY_REFRESH_TOKEN)
                        Timber.d("success reset refresh token")
                    } else {
                        pref[KEY_REFRESH_TOKEN] = refreshToken
                        Timber.d("success update refresh token")
                    }
                }
                return@runCatching
            }
        }
    }

    override suspend fun resetRefreshToken(): Result<Unit> {
        return setRefreshToken(null)
    }
}
