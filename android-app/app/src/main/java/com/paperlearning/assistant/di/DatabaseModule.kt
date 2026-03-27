package com.paperlearning.assistant.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.AutoMigrationSpec
import com.paperlearning.assistant.data.local.PaperDatabase
import com.paperlearning.assistant.data.local.dao.*
import com.paperlearning.assistant.util.PdfParser
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
        )
            // Fallback to destructive migration when schema changes.
            // For production, replace with explicit migration strategy:
            //   .addMigrations(migration1to2, migration2to3, ...)
            //   .autoMigrations(autoMigration1to2, autoMigration2to3, ...)
            .fallbackToDestructiveMigration()
            .build()
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

    @Provides
    @Singleton
    fun providePdfParser(): PdfParser {
        return PdfParser()
    }
}
