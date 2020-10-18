package com.example.groupchatting.di

import com.example.groupchatting.repository.MainRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRepository(
        auth : FirebaseAuth,
        firestoreRef : FirebaseFirestore,
        storageRef : StorageReference
    ) = MainRepository(auth, firestoreRef, storageRef)
}