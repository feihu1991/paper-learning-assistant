package com.paperlearning.assistant.di

import com.paperlearning.assistant.data.local.dao.PaperDao
import com.paperlearning.assistant.data.repository.PaperRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePaperRepository(
        paperDao: PaperDao
    ): PaperRepository {
        return PaperRepository(paperDao)
    }
}
