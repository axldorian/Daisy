package com.daisydev.daisy.di

import android.content.Context
import com.daisydev.daisy.repository.remote.AppWriteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.appwrite.Client
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton
import com.daisydev.daisy.util.Constants

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Named("WEB_API")
    fun baseUrl(): String = Constants().baseUrl

    @Provides
    @Named("PROJECT_ID")
    fun projectId(): String = Constants().projectId

    @Singleton
    @Provides
    fun provideAppWriteClient(
        @ApplicationContext context: Context,
        @Named("WEB_API") baseUrl: String,
        @Named("PROJECT_ID") projectId: String
    ): Client =
        Client(context).setEndpoint(baseUrl).setProject(projectId)

    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Provides
    fun provideAppWriteRepository(
        client: Client,
        dispatcher: CoroutineDispatcher
    ): AppWriteRepository =
        AppWriteRepository(client, dispatcher)
}