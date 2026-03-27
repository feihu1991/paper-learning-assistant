package com.paperlearning.assistant.data.local

import androidx.room.TypeConverter
import com.paperlearning.assistant.data.model.LearningMode
import com.paperlearning.assistant.data.model.ParseStatus
import com.paperlearning.assistant.data.model.StepType

/**
 * Room TypeConverters for custom types.
 * Converts enum types to/from their ordinal (Int) for database storage.
 */
class Converters {

    @TypeConverter
    fun fromParseStatus(status: ParseStatus): Int {
        return status.ordinal
    }

    @TypeConverter
    fun toParseStatus(value: Int): ParseStatus {
        return ParseStatus.entries.getOrElse(value) { ParseStatus.NOT_PARSED }
    }

    @TypeConverter
    fun fromLearningMode(mode: LearningMode): Int {
        return mode.ordinal
    }

    @TypeConverter
    fun toLearningMode(value: Int): LearningMode {
        return LearningMode.entries.getOrElse(value) { LearningMode.STANDARD }
    }

    @TypeConverter
    fun fromStepType(stepType: StepType): Int {
        return stepType.ordinal
    }

    @TypeConverter
    fun toStepType(value: Int): StepType {
        return StepType.entries.getOrElse(value) { StepType.BACKGROUND }
    }
}
