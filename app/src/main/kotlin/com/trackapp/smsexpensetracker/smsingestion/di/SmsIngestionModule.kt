package com.trackapp.smsexpensetracker.smsingestion.di

import com.trackapp.smsexpensetracker.smsingestion.data.repository.AndroidSmsProviderRepository
import com.trackapp.smsexpensetracker.smsingestion.domain.repository.SmsProviderRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/** See ADR-001 (bolt 002-sms-ingestion): application-scoped, not WorkManager. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
abstract class SmsIngestionModule {

    @Binds
    @Singleton
    abstract fun bindSmsProviderRepository(impl: AndroidSmsProviderRepository): SmsProviderRepository

    companion object {
        @Provides
        @Singleton
        @ApplicationScope
        fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}
