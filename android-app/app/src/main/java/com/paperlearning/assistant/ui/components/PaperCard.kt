package com.paperlearning.assistant.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.paperlearning.assistant.data.model.ParseStatus
import com.paperlearning.assistant.data.model.PaperEntity

@Composable
fun PaperCard(
    paper: PaperEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 左侧图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .wrapContentSize(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = null,
                    tint = when (paper.parsedStatus) {
                        ParseStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                        ParseStatus.PARSING -> MaterialTheme.colorScheme.tertiary
                        ParseStatus.FAILED -> MaterialTheme.colorScheme.error
                        ParseStatus.NOT_PARSED -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(32.dp)
                )
                
                if (paper.parsedStatus == ParseStatus.COMPLETED) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "已完成",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            // 右侧内容
            Column(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = paper.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!paper.authors.isNullOrBlank()) {
                    Text(
                        text = paper.authors,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 状态标签
                    StatusChip(status = paper.parsedStatus)

                    // ArXiv ID
                    if (!paper.arxivId.isNullOrBlank()) {
                        Text(
                            text = paper.arxivId,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: ParseStatus) {
    val (label, color) = when (status) {
        ParseStatus.COMPLETED -> "已完成" to MaterialTheme.colorScheme.primaryContainer
        ParseStatus.PARSING -> "解析中" to MaterialTheme.colorScheme.tertiaryContainer
        ParseStatus.FAILED -> "失败" to MaterialTheme.colorScheme.errorContainer
        ParseStatus.NOT_PARSED -> "未解析" to MaterialTheme.colorScheme.surfaceVariant
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
