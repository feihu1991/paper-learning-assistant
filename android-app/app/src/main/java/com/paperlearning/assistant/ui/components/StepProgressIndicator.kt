package com.paperlearning.assistant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

    val composable: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted && !isCurrent) {
                // 已完成状态：显示勾选符号
                Text(
                    text = "✓",
                    color = contentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            } else {
                // 未完成状态：显示序号
                Text(
                    text = stepNumber.toString(),
                    style = fontSize,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }

    if (isClickable) {
        Box(
            modifier = Modifier
                .size(size)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            composable()
        }
    } else {
        composable()
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
