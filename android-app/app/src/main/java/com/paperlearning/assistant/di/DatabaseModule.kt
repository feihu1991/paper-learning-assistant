package com.paperlearning.assistant.di

import android.content.Context
import androidx.room.Room
import com.paperlearning.assistant.data.local.PaperDatabase
import com.paperlearning.assistant.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PaperDatabase {
        return Room.databaseBuilder(
            context,
            PaperDatabase::class.java,
            PaperDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providePaperDao(database: PaperDatabase): PaperDao {
        return database.paperDao()
    }

    @Provides
    @Singleton
    fun provideLearningStepDao(database: PaperDatabase): LearningStepDao {
        return database.learningStepDao()
    }

    @Provides
    @Singleton
    fun provideUserProgressDao(database: PaperDatabase): UserProgressDao {
        return database.userProgressDao()
    }

    @Provides
    @Singleton
    fun provideLlmConfigDao(database: PaperDatabase): LlmConfigDao {
        return database.llmConfigDao()
    }
}
