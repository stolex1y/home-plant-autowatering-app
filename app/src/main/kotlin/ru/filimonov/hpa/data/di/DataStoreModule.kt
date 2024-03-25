package ru.filimonov.hpa.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DataStoreModule {
    companion object {
        const val AUTH_PREFERENCES = "auth"
    }

    private val Context.userAuthPreferences by preferencesDataStore(AUTH_PREFERENCES)

    @Provides
    @Singleton
    @Named(AUTH_PREFERENCES)
    fun preferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.userAuthPreferences
    }
}
