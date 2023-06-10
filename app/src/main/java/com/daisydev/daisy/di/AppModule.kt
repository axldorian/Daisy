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

    // Función que provee la url base de la API
    @Provides
    @Named("WEB_API")
    fun baseUrl(): String = Constants().baseUrl

    // Función que provee el id del proyecto de AppWrite
    @Provides
    @Named("PROJECT_ID")
    fun projectId(): String = Constants().projectId

    // Función que provee el cliente de AppWrite
    @Singleton
    @Provides
    fun provideAppWriteClient(
        @ApplicationContext context: Context,
        @Named("WEB_API") baseUrl: String,
        @Named("PROJECT_ID") projectId: String
    ): Client =
        Client(context).setEndpoint(baseUrl).setProject(projectId)

    // Función que provee el dispatcher de la corutina
    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    // Función que provee el repositorio de AppWrite
    @Provides
    fun provideAppWriteRepository(
        client: Client,
        dispatcher: CoroutineDispatcher
    ): AppWriteRepository =
        AppWriteRepository(client, dispatcher)
}