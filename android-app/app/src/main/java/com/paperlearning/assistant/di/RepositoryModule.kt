package com.paperlearning.assistant.di

import com.paperlearning.assistant.data.local.dao.LearningStepDao
import com.paperlearning.assistant.data.local.dao.LlmConfigDao
import com.paperlearning.assistant.data.local.dao.PaperDao
import com.paperlearning.assistant.data.local.dao.UserProgressDao
import com.paperlearning.assistant.data.repository.ArxivRepository
import com.paperlearning.assistant.data.repository.LearningRepository
import com.paperlearning.assistant.data.repository.PaperRepository
import com.paperlearning.assistant.data.repository.SettingsRepository
import com.paperlearning.assistant.data.remote.arxiv.ArxivApiService
import com.paperlearning.assistant.data.security.ApiKeyStore
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

    @Provides
    @Singleton
    fun provideLearningRepository(
        learningStepDao: LearningStepDao,
        userProgressDao: UserProgressDao
    ): LearningRepository {
        return LearningRepository(learningStepDao, userProgressDao)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        llmConfigDao: LlmConfigDao,
        apiKeyStore: ApiKeyStore
    ): SettingsRepository {
        return SettingsRepository(llmConfigDao, apiKeyStore)
    }

    @Provides
    @Singleton
    fun provideArxivRepository(
        arxivApiService: ArxivApiService
    ): ArxivRepository {
        return ArxivRepository(arxivApiService)
    }
}
