package com.kidor.vigik.di

import android.content.Context
import android.nfc.NfcAdapter
import androidx.room.Room
import com.kidor.vigik.db.AppDataBase
import com.kidor.vigik.db.DATABASE_NAME
import com.kidor.vigik.db.TagDao
import com.kidor.vigik.db.TagRepository
import com.kidor.vigik.db.TagRepositoryImp
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
    fun provideTagDao(dataBase: AppDataBase) = dataBase.tagDao()

    /**
     * Provides instance of [NfcAdapter].
     *
     * @param context The application context.
     */
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

    /**
     * Provides instance of [TagRepository].
     *
     * @param tagDao The tag DAO.
     */
    @Singleton
    @Provides
    fun provideTagRepository(tagDao: TagDao): TagRepository = TagRepositoryImp(tagDao)
}
