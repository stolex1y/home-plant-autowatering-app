package ru.filimonov.hpa.di

import android.content.Context
import android.util.LruCache
import coil.ImageLoader
import coil.request.CachePolicy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import ru.filimonov.hpa.common.utils.time.DateUtils.toShortFormatString
import ru.filimonov.hpa.common.utils.time.DateUtils.toZonedDateTime
import timber.log.Timber
import java.time.ZonedDateTime
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CoilModule {

    class HttpCachingInterceptor : okhttp3.Interceptor {
        companion object {
            private const val CACHE_OBJECT_COUNT = 15
        }

        private val dataCache = LruCache<String, ByteArray>(CACHE_OBJECT_COUNT)
        private val lastAccessTimestamp = LruCache<String, Long>(CACHE_OBJECT_COUNT)

        override fun intercept(chain: okhttp3.Interceptor.Chain): Response {
            val cacheKey = chain.request().url.toString()
            Timber.d("start processing request to $cacheKey")
            val lastTimestamp = lastAccessTimestamp.get(cacheKey)
            val cachedData = dataCache.get(cacheKey)
            var request = chain.request()
            if (lastTimestamp != null && cachedData != null) {
                Timber.d(
                    "add if-modified-since ${
                        lastTimestamp.toZonedDateTime().toShortFormatString()
                    }"
                )
                request =
                    request.newBuilder().addHeader("If-Modified-Since", lastTimestamp.toString())
                        .build()
            }
            val response = chain.proceed(request)
            /* TODO
                if (response.isNotModified()) {
                Timber.d("return cached data")
                return response.newBuilder().code(200).body(cachedData.toResponseBody()).build()
            }*/
            Timber.d("cache received data")
            val newData = response.body?.bytes()
            dataCache.put(cacheKey, newData)
            Timber.d("update last access time to ${ZonedDateTime.now().toShortFormatString()}")
            lastAccessTimestamp.put(cacheKey, System.currentTimeMillis())
            return response.newBuilder().body(newData?.toResponseBody()).build()
        }
    }

    companion object {
        private const val IMAGE_HTTP_CLIENT = "image_http_client"

        @Provides
        @Singleton
        @JvmSuppressWildcards
        @Named(IMAGE_HTTP_CLIENT)
        fun httpClient(
            interceptors: Set<okhttp3.Interceptor>,
            authenticator: Authenticator,
        ): OkHttpClient {
            val clientBuilder = OkHttpClient().newBuilder()
            interceptors.forEach {
                clientBuilder.addInterceptor(it)
            }
            clientBuilder.addInterceptor(HttpCachingInterceptor())
            return clientBuilder
                .cache(null)
                .authenticator(authenticator)
                .build()
        }

        @Provides
        @Singleton
        fun imageLoader(
            @Named(IMAGE_HTTP_CLIENT) httpClient: OkHttpClient,
            @ApplicationContext context: Context,
        ): ImageLoader {
            return ImageLoader.Builder(context)
                .diskCachePolicy(CachePolicy.DISABLED)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .okHttpClient(httpClient)
                .build()
        }
    }
}
