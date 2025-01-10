package com.kidor.vigik.di

import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.kidor.vigik.data.AppDataBase
import com.kidor.vigik.data.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.conscrypt.Conscrypt
import java.security.Security
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import javax.net.ssl.SSLContext

private const val SHARED_PREFERENCES_FILE_NAME = "app_shared_preferences"
private const val CONNECTION_TIMEOUT_IN_SECOND = 30L
private const val READ_TIMEOUT_IN_SECOND = 10L

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
     * Provides unique instance of [CoroutineScope] based on [SupervisorJob].
     */
    @Singleton
    @Provides
    fun provideApplicationScope(): CoroutineScope {
        // The SupervisorJob will not propagate cancellation to other children when a child coroutine fails.
        return CoroutineScope(SupervisorJob())
    }

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
     * Provides instance of [OkHttpClient].
     */
    @Provides
    fun providesOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        // Bring TLSv1.3 support if the system is older than Android Q
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val conscryptProvider = Conscrypt.newProvider()
            val trustManager = Conscrypt.getDefaultX509TrustManager()
            Security.insertProviderAt(conscryptProvider, 1)
            val sslContext = SSLContext.getInstance("TLS", conscryptProvider)
            sslContext.init(null, arrayOf(trustManager), null)
            builder.sslSocketFactory(sslContext.socketFactory, trustManager)
        }
        return builder
            .connectTimeout(CONNECTION_TIMEOUT_IN_SECOND, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_IN_SECOND, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }
}

/**
 * Annotation to inject [Dispatchers.IO].
 */
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher
