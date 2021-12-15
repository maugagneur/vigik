package com.kidor.vigik.di

import android.content.Context
import android.nfc.NfcAdapter
import androidx.room.Room
import com.kidor.vigik.db.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module to tell Hilt how to provide instances of types that cannot be constructor-injected.
 *
 * As these types are scoped to the application lifecycle using @Singleton, they're installed
 * in Hilt's SingletonComponent.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDataBase =
        Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun provideTagDao(dataBase: AppDataBase) = dataBase.tagDao()

    @Singleton
    @Provides
    fun provideNfcAdapter(@ApplicationContext context: Context): NfcAdapter =
        NfcAdapter.getDefaultAdapter(context)
}

/**
 * The binding for tag repository is on its own module so that we can replace it easily in tests.
 */
@Module
@InstallIn(SingletonComponent::class)
object TagRepositoryModule {

    @Singleton
    @Provides
    fun provideTagRepository(tagDao: TagDao): TagRepository = TagRepositoryImp(tagDao)
}