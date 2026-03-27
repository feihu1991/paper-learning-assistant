package com.paperlearning.assistant.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 步骤进度指示器组件
 * 显示学习路径中所有步骤的进度状态
 */
@Composable
fun StepProgressIndicator(
    totalSteps: Int,
    currentStepIndex: Int,
    completedSteps: List<Int>,
    modifier: Modifier = Modifier,
    onStepClick: ((Int) -> Unit)? = null
) {
    if (totalSteps == 0) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 进度条
        LinearProgressIndicator(
            progress = { (currentStepIndex + 1).toFloat() / totalSteps.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(MaterialTheme.shapes.small),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        // 步骤指示器
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (index in 0 until totalSteps) {
                StepIndicatorDot(
                    stepNumber = index + 1,
                    isCompleted = index in completedSteps,
                    isCurrent = index == currentStepIndex,
                    isClickable = onStepClick != null && index <= currentStepIndex,
                    onClick = { onStepClick?.invoke(index) }
                )
            }
        }

        // 步骤计数文本
        Text(
            text = "步骤 ${currentStepIndex + 1} / $totalSteps",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun StepIndicatorDot(
    stepNumber: Int,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isClickable: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isCompleted -> MaterialTheme.colorScheme.primary
        isCurrent -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when {
        isCompleted -> MaterialTheme.colorScheme.onPrimary
        isCurrent -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val size = if (isCurrent) 32.dp else 28.dp
    val fontSize = if (isCurrent) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelSmall

    if (isClickable) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(size)
        ) {
            StepDotContent(
                stepNumber = stepNumber,
                backgroundColor = backgroundColor,
                contentColor = contentColor,
                fontSize = fontSize,
                isCompleted = isCompleted,
                isCurrent = isCurrent
            )
        }
    } else {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            StepDotContent(
                stepNumber = stepNumber,
                backgroundColor = backgroundColor,
                contentColor = contentColor,
                fontSize = fontSize,
                isCompleted = isCompleted,
                isCurrent = isCurrent
            )
        }
    }
}

@Composable
private fun StepDotContent(
    stepNumber: Int,
    backgroundColor: Color,
    contentColor: Color,
    fontSize: androidx.compose.ui.text.TextStyle,
    isCompleted: Boolean,
    isCurrent: Boolean
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = backgroundColor,
        modifier = Modifier.fillMaxSize()
    ) {
        if (isCompleted && !isCurrent) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Filled.Done,
                contentDescription = "已完成",
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
        } else {
            Text(
                text = stepNumber.toString(),
                style = fontSize,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

/**
 * 简化的进度条组件（仅显示线性进度）
 */
@Composable
fun SimpleProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    showPercentage: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(MaterialTheme.shapes.small),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        if (showPercentage) {
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
