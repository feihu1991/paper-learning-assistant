package com.paperlearning.assistant.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.paperlearning.assistant.domain.model.Quiz
import com.paperlearning.assistant.domain.model.QuizDifficulty

/**
 * 测验卡片组件
 * 支持选择题，显示答案和解析
 */
@Composable
fun QuizCard(
    quiz: Quiz,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showAnswer: Boolean = false,
    selectedAnswerIndex: Int? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 题目标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Help,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Text(
                    text = "问题",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // 难度标签
                DifficultyChip(difficulty = quiz.difficulty)
            }

            // 问题内容
            Text(
                text = quiz.question,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // 选项列表
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quiz.options.forEachIndexed { index, option ->
                    OptionButton(
                        option = option,
                        index = index,
                        isSelected = selectedAnswerIndex == index,
                        showAnswer = showAnswer,
                        isCorrect = index == quiz.correctAnswerIndex,
                        onClick = { onAnswerSelected(index) },
                        enabled = !showAnswer
                    )
                }
            }

            // 答案和解析（显示答案时）
            if (showAnswer) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                AnswerSection(
                    correctAnswerIndex = quiz.correctAnswerIndex,
                    explanation = quiz.explanation,
                    selectedAnswerIndex = selectedAnswerIndex
                )
            }
        }
    }
}

@Composable
private fun OptionButton(
    option: String,
    index: Int,
    isSelected: Boolean,
    showAnswer: Boolean,
    isCorrect: Boolean,
    onClick: () -> Unit,
    enabled: Boolean
) {
    val backgroundColor = when {
        showAnswer && isCorrect -> MaterialTheme.colorScheme.primaryContainer
        showAnswer && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    
    val contentColor = when {
        showAnswer && isCorrect -> MaterialTheme.colorScheme.onPrimaryContainer
        showAnswer && isSelected && !isCorrect -> MaterialTheme.colorScheme.onErrorContainer
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        color = backgroundColor,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 选项标记
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (showAnswer && isCorrect) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "正确答案",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                } else if (showAnswer && isSelected && !isCorrect) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "错误答案",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }
            }
            
            // 选项内容
            Text(
                text = option,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AnswerSection(
    correctAnswerIndex: Int,
    explanation: String,
    selectedAnswerIndex: Int?
) {
    val isCorrect = selectedAnswerIndex == correctAnswerIndex
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 答案状态
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Info,
                contentDescription = null,
                tint = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(20.dp)
            )
            
            Text(
                text = if (isCorrect) "回答正确！" else "正确答案",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
            )
        }
        
        // 正确答案
        Text(
            text = "答案：${correctAnswerIndex + 1}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // 解析
        if (explanation.isNotBlank()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.small
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Text(
                            text = "解析",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    
                    Text(
                        text = explanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DifficultyChip(difficulty: QuizDifficulty) {
    val (label, color) = when (difficulty) {
        QuizDifficulty.EASY -> "简单" to MaterialTheme.colorScheme.primaryContainer
        QuizDifficulty.MEDIUM -> "中等" to MaterialTheme.colorScheme.tertiaryContainer
        QuizDifficulty.HARD -> "困难" to MaterialTheme.colorScheme.errorContainer
    }

    Surface(
        color = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
