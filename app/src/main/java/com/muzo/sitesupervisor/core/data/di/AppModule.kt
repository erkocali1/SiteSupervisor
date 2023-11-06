package com.muzo.sitesupervisor.core.data.di

import com.google.firebase.FirebaseApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseApp():FirebaseApp{
        return FirebaseApp.getInstance()
    }
}