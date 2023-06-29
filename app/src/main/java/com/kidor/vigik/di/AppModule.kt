package com.kidor.vigik.di

import android.content.Context
import android.nfc.NfcAdapter
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.kidor.vigik.data.AppDataBase
import com.kidor.vigik.data.DATABASE_NAME
import com.kidor.vigik.data.tag.TagDao
import com.kidor.vigik.data.tag.TagRepository
import com.kidor.vigik.data.tag.TagRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

private const val SHARED_PREFERENCES_FILE_NAME = "app_shared_preferences"

/**
 * Module to tell Hilt how to provide instances of types that cannot be constructor-injected.
 *
 * As these types are scoped to the application lifecycle using @Singleton, they're installed
 * in Hilt's SingletonComponent.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides instance of [Dispatchers.IO].
     */
    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provides unique instance of [DataStore] for [Preferences].
     *
     * @param context Application's context.
     */
    @Singleton
    @Provides
    fun providesPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(SHARED_PREFERENCES_FILE_NAME) }
        )

    /**
     * Provides instance of [AppDataBase].
     *
     * @param context The application context.
     */
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDataBase =
        Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            DATABASE_NAME
        ).build()

    /**
     * Provides instance of [TagDao].
     *
     * @param dataBase The application database.
     */
    @Singleton
    @Provides
    fun provideTagDao(dataBase: AppDataBase): TagDao = dataBase.tagDao()

    /**
     * Provides instance of [NfcAdapter] if available.
     *
     * @param context The application context.
     */
    @Singleton
    @Provides
    fun provideNfcAdapter(@ApplicationContext context: Context): NfcAdapter? =
        NfcAdapter.getDefaultAdapter(context)
}

/**
 * The binding for tag repository is on its own module so that we can replace it easily in tests.
 */
@Module
@InstallIn(SingletonComponent::class)
object TagRepositoryModule {

    /**
     * Provides instance of [TagRepository].
     *
     * @param tagDao The tag DAO.
     */
    @Singleton
    @Provides
    fun provideTagRepository(tagDao: TagDao): TagRepository = TagRepositoryImp(tagDao)
}

/**
 * Annotation to inject [Dispatchers.IO].
 */
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher
