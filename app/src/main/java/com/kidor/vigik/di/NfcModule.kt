package com.kidor.vigik.di

import android.content.Context
import android.nfc.NfcAdapter
import com.kidor.vigik.data.AppDataBase
import com.kidor.vigik.data.tag.TagDao
import com.kidor.vigik.data.tag.TagRepository
import com.kidor.vigik.data.tag.TagRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module related to NFC features.
 */
@Module
@InstallIn(SingletonComponent::class)
object NfcModule {

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
