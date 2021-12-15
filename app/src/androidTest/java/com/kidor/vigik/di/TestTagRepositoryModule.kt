package com.kidor.vigik.di

import com.kidor.vigik.db.TagRepository
import com.kidor.vigik.db.FakeTagRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * TagRepository binding to use in tests.
 *
 * Hilt will inject a [FakeTagRepository] instead of a [TagRepositoryImp].
 */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [TagRepositoryModule::class])
abstract class TestTagRepositoryModule {

    @Binds
    @Singleton
    abstract fun provideTagRepository(repository: FakeTagRepository): TagRepository
}