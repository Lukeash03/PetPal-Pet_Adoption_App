package com.luke.petpal.di

import android.content.Context
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.luke.petpal.data.repository.AuthRepository
import com.luke.petpal.domain.repository.AuthRepositoryImpl
import com.luke.petpal.presentation.auth.GoogleAuthUIClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun provideGoogleAuthUIClient(@ApplicationContext context: Context): GoogleAuthUIClient {
        val oneTapClient = Identity.getSignInClient(context)
        return GoogleAuthUIClient(context, oneTapClient)
    }
}