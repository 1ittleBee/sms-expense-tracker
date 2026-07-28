package com.trackapp.smsexpensetracker.smsingestion.di

import com.trackapp.smsexpensetracker.smsingestion.data.gateway.PermissionCoordinatorImpl
import com.trackapp.smsexpensetracker.smsingestion.domain.service.PermissionCoordinator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PermissionModule {
    @Binds
    @Singleton
    abstract fun bindPermissionCoordinator(impl: PermissionCoordinatorImpl): PermissionCoordinator
}
