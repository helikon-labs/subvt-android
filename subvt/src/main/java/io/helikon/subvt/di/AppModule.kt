package io.helikon.subvt.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.helikon.subvt.data.db.SubVTDatabase
import io.helikon.subvt.data.repository.AppServiceRepository
import io.helikon.subvt.data.repository.NetworkRepository
import io.helikon.subvt.data.repository.NetworkStatusRepository
import io.helikon.subvt.data.repository.UserPreferencesRepository

@Module
@InstallIn(ViewModelComponent::class)
class AppModule {
    @Provides
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context,
    ): UserPreferencesRepository = UserPreferencesRepository(context)

    @Provides
    fun provideAppServiceRepository(
        @ApplicationContext context: Context,
    ): AppServiceRepository = AppServiceRepository(context)

    @Provides
    fun provideNetworkRepository(
        @ApplicationContext context: Context,
    ): NetworkRepository = NetworkRepository(SubVTDatabase.getInstance(context).networkDAO())

    @Provides
    fun provideNetworkStatusRepository(): NetworkStatusRepository = NetworkStatusRepository()
}
