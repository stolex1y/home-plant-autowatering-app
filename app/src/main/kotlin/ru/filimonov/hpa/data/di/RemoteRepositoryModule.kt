package ru.filimonov.hpa.data.di

import com.google.gson.Gson
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
import ru.filimonov.hpa.data.remote.repository.DeviceConfigurationRemoteRepository
import ru.filimonov.hpa.data.remote.repository.DeviceRemoteRepository
import ru.filimonov.hpa.data.remote.repository.PlantRemoteRepository
import ru.filimonov.hpa.data.remote.repository.readings.SensorReadingsRemoteRepository
import ru.filimonov.hpa.data.remote.repository.readings.SoilMoistureReadingsRemoteRepository
import ru.filimonov.hpa.domain.service.auth.UserAuthService
import timber.log.Timber
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RemoteRepositoryModule {
    companion object {
        const val DEVICE_RETROFIT = "DEVICE_RETROFIT"

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
        @Singleton
        fun plantRepository(
            retrofit: Retrofit
        ): PlantRemoteRepository {
            return retrofit.create(PlantRemoteRepository::class.java)
        }

        @Provides
        @Singleton
        fun deviceConfigurationRepository(
            @Named(DEVICE_RETROFIT) retrofit: Retrofit
        ): DeviceConfigurationRemoteRepository {
            return retrofit.create(DeviceConfigurationRemoteRepository::class.java)
        }

        @Provides
        @Singleton
        fun sensorReadingsRepository(
            retrofit: Retrofit
        ): SensorReadingsRemoteRepository {
            return retrofit.create(SensorReadingsRemoteRepository::class.java)
        }

        @Provides
        @Singleton
        fun soilMoistureReadingsRepository(
            retrofit: Retrofit
        ): SoilMoistureReadingsRemoteRepository {
            return retrofit.create(SoilMoistureReadingsRemoteRepository::class.java)
        }

        @Provides
        @IntoSet
        @Singleton
        @JvmSuppressWildcards
        fun logInterceptor(): Interceptor {
            return Interceptor { chain ->
                Timber.d("make request to ${chain.request().url}")
                chain.proceed(chain.request())
            }
        }

        @Provides
        @IntoSet
        @Singleton
        @JvmSuppressWildcards
        fun authInterceptor(
            userAuthService: Provider<UserAuthService>
        ): Interceptor {
            return Interceptor { chain ->
                runBlocking {
                    val srcRequest = chain.request()
                    if (srcRequest.url.pathSegments.firstOrNull() == "reauth") {
                        return@runBlocking chain.proceed(srcRequest)
                    }

                    val idToken = userAuthService.get().getIdToken().first()
                    val requestBuilder = srcRequest.newBuilder()
                        .url(srcRequest.url)

                    if (idToken != null) {
                        requestBuilder.addHeader(BuildConfig.API_AUTH_HEADER, "Bearer $idToken")
                    }

                    requestBuilder.build()
                        .run(chain::proceed)
                }
            }
        }

        @Provides
        @Singleton
        fun authenticator(
            userAuthService: Provider<UserAuthService>
        ): Authenticator {
            return Authenticator { route: Route?, response: Response ->
                runBlocking {
                    userAuthService.get().reauthenticate()
                    val idToken =
                        userAuthService.get().getIdToken().first() ?: return@runBlocking null
                    response.request.newBuilder()
                        .addHeader(BuildConfig.API_AUTH_HEADER, "Bearer $idToken")
                        .build()
                }
            }
        }

        @Provides
        @Singleton
        @JvmSuppressWildcards
        fun httpClient(
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
        fun backendRetrofit(
            httpClient: OkHttpClient,
            gson: Gson,
        ): Retrofit {
            return Retrofit.Builder()
                .client(httpClient)
                .baseUrl(BuildConfig.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        @Provides
        @Singleton
        @Named(DEVICE_RETROFIT)
        fun deviceRetrofit(
            httpClient: OkHttpClient,
            gson: Gson,
        ): Retrofit {
            return Retrofit.Builder()
                .client(httpClient)
                .baseUrl(BuildConfig.DEVICE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
    }
}
