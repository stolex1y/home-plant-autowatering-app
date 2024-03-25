package ru.filimonov.hpa.data.remote.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.filimonov.hpa.BuildConfig
import ru.filimonov.hpa.data.remote.repository.AuthRemoteRepository
import ru.filimonov.hpa.data.remote.repository.DeviceRemoteRepository
import ru.filimonov.hpa.domain.service.auth.UserAuthService
import timber.log.Timber
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RemoteRepositoryModule {
    companion object {
        @Provides
        @Singleton
        fun authRepository(
            retrofit: Retrofit
        ): AuthRemoteRepository {
            return retrofit.create(AuthRemoteRepository::class.java)
        }

        @Provides
        @Singleton
        fun deviceRepository(
            retrofit: Retrofit
        ): DeviceRemoteRepository {
            return retrofit.create(DeviceRemoteRepository::class.java)
        }

        @Provides
        @IntoSet
        @Singleton
        private fun logInterceptor(): Interceptor {
            return Interceptor { chain ->
                Timber.d("make request to ${chain.request().url()}")
                chain.proceed(chain.request())
            }
        }

        @Provides
        @Singleton
        private fun authenticator(
            userAuthService: Provider<UserAuthService>
        ): Authenticator {
            return Authenticator { route: Route?, response: Response ->
                runBlocking {
                    userAuthService.get().reauthenticate()
                    val idToken =
                        userAuthService.get().getIdToken().first() ?: return@runBlocking null
                    response.request().newBuilder()
                        .addHeader(BuildConfig.API_AUTH_HEADER, "Bearer $idToken")
                        .build()
                }
            }
        }

        @Provides
        @Singleton
        private fun httpClient(
            interceptors: Set<Interceptor>,
            authenticator: Authenticator,
        ): OkHttpClient {
            val clientBuilder = OkHttpClient().newBuilder()
            interceptors.forEach {
                clientBuilder.addInterceptor(it)
            }
            return clientBuilder
                .authenticator(authenticator)
                .build()
        }

        @Provides
        @Singleton
        private fun retrofit(
            httpClient: OkHttpClient
        ): Retrofit {
            return Retrofit.Builder()
                .client(httpClient)
                .baseUrl(BuildConfig.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}
