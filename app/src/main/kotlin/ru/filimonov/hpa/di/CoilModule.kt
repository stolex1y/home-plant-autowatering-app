package ru.filimonov.hpa.di

import android.content.Context
import coil.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CoilModule {
    companion object {
        @Provides
        @Singleton
        fun imageLoader(
            httpClient: OkHttpClient,
            @ApplicationContext context: Context
        ): ImageLoader {
            return ImageLoader.Builder(context).okHttpClient(httpClient).build()
        }
    }
}