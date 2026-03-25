package com.paperlearning.assistant.di

import com.paperlearning.assistant.data.remote.arxiv.ArxivApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideArxivApiService(client: OkHttpClient): ArxivApiService {
        return Retrofit.Builder()
            .baseUrl("http://export.arxiv.org/api/query?")
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(ArxivApiService::class.java)
    }
}
