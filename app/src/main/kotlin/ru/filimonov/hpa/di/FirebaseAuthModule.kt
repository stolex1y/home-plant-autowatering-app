package ru.filimonov.hpa.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface FirebaseAuthModule {
    companion object {
        @Provides
        @Singleton
        fun firebaseAuth(): FirebaseAuth {
            return Firebase.auth
        }
    }
}
