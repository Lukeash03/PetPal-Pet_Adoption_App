package com.luke.petpal.di

import android.content.Context
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.luke.petpal.data.repository.AuthRepository
import com.luke.petpal.data.repository.ChatRepository
import com.luke.petpal.data.repository.HomeRepository
import com.luke.petpal.data.repository.UserProfileRepository
import com.luke.petpal.domain.repository.AuthRepositoryImpl
import com.luke.petpal.domain.repository.ChatRepositoryImpl
import com.luke.petpal.domain.repository.HomeRepositoryImpl
import com.luke.petpal.domain.repository.UserProfileRepositoryImpl
import com.luke.petpal.domain.repository.usecase.CompressImagesUseCase
import com.luke.petpal.domain.repository.usecase.ValidateEmail
import com.luke.petpal.domain.repository.usecase.ValidatePassword
import com.luke.petpal.domain.repository.usecase.ValidatePetBreed
import com.luke.petpal.domain.repository.usecase.ValidatePetGender
import com.luke.petpal.domain.repository.usecase.ValidatePetName
import com.luke.petpal.domain.repository.usecase.ValidatePetSpecies
import com.luke.petpal.domain.repository.usecase.ValidateUsername
import com.luke.petpal.presentation.auth.googlesignin.GoogleAuthUIClient
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
    fun provideValidateUsername(): ValidateUsername = ValidateUsername()

    @Provides
    fun provideValidateEmail(): ValidateEmail = ValidateEmail()

    @Provides
    fun provideValidatePassword(): ValidatePassword = ValidatePassword()

    @Provides
    fun provideValidatePetName(): ValidatePetName = ValidatePetName()

    @Provides
    fun provideValidatePetSpecies(): ValidatePetSpecies = ValidatePetSpecies()

    @Provides
    fun provideValidatePetBreed(): ValidatePetBreed = ValidatePetBreed()

    @Provides
    fun provideValidatePetGender(): ValidatePetGender = ValidatePetGender()

    @Provides
    fun provideCompressImageUseCase(): CompressImagesUseCase = CompressImagesUseCase()

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase = Firebase.database("\"https://petpal-pet-adoption-default-rtdb.asia-southeast1.firebasedatabase.app\"")

    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    fun provideHomeRepository(impl: HomeRepositoryImpl): HomeRepository = impl

    @Provides
    fun provideProfileImageRepository(impl: UserProfileRepositoryImpl): UserProfileRepository = impl

    @Provides
    fun provideChatRepository(impl: ChatRepositoryImpl): ChatRepository = impl

    @Provides
    @Singleton
    fun provideGoogleAuthUIClient(@ApplicationContext context: Context): GoogleAuthUIClient {
        val oneTapClient = Identity.getSignInClient(context)
        return GoogleAuthUIClient(context, oneTapClient)
    }
}