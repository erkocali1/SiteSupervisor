package com.muzo.sitesupervisor.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.muzo.sitesupervisor.core.common.dataStore
import com.muzo.sitesupervisor.core.constans.Constants.Companion.DATABASE_NAME
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.local.room.PostDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseApp(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFireStoreInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }


    @Singleton
    @Provides
    fun provideFirebaseStorageInstance(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Singleton
    @Provides
    fun provideRunDao(db: PostDataBase) = db.getPostDAo()

    @Provides
    @Singleton
    fun providePostDataBase(@ApplicationContext app: Context) =
        Room.databaseBuilder(app, PostDataBase::class.java, DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideMyDataStorePreference(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideMyDataStoreManager(dataStore: DataStore<Preferences>): MyDataStore {
        return MyDataStore(dataStore)
    }


}