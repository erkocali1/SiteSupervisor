package com.muzo.sitesupervisor.core.data.di

import com.muzo.sitesupervisor.core.data.remote.repository.AuthRepository
import com.muzo.sitesupervisor.core.data.remote.repository.AuthRepositoryImpl
import com.muzo.sitesupervisor.core.data.remote.source.AuthDataSource
import com.muzo.sitesupervisor.core.data.remote.source.AuthDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface SiteSupervisorAppModule {

    @Binds
    fun bindsAuthDataSource(
        authDataSourceImpl: AuthDataSourceImpl
    ): AuthDataSource

    @Binds
    fun bindsAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository


}