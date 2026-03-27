package com.paperlearning.assistant.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.paperlearning.assistant.data.model.ParseStatus
import com.paperlearning.assistant.ui.components.ActionButton
import com.paperlearning.assistant.ui.components.SectionCard
import com.paperlearning.assistant.viewmodel.PaperDetailViewModel

/**
 * 论文详情屏幕
 * 显示论文的元数据、摘要、学习进度等信息
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaperDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLearning: () -> Unit,
    viewModel: PaperDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 监听导航事件
    if (uiState.navigateToLearning) {
        LaunchedEffect(Unit) {
            viewModel.resetNavigation()
            onNavigateToLearning()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "论文详情",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    // 更多操作菜单
                    IconButton(onClick = { /* TODO: 分享 */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "分享"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    // 加载状态
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.error != null -> {
                    // 错误状态
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "加载失败",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            viewModel.clearError()
                            // 重新加载
                        }) {
                            Text("重试")
                        }
                    }
                }

                uiState.paper == null -> {
                    // 论文不存在
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "论文不存在",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) {
                            Text("返回")
                        }
                    }
                }

                else -> {
                    // 内容状态
                    PaperDetailContent(
                        paper = uiState.paper,
                        learningSteps = uiState.learningSteps,
                        isParsing = uiState.isParsing,
                        onParseClick = { viewModel.parsePaper() },
                        onStartLearningClick = { viewModel.startLearning() }
                    )
                }
            }
        }
    }
}

/**
 * 论文详情内容
 */
@Composable
private fun PaperDetailContent(
    paper: com.paperlearning.assistant.data.model.PaperEntity,
    learningSteps: List<com.paperlearning.assistant.data.model.LearningStepEntity>,
    isParsing: Boolean,
    onParseClick: () -> Unit,
    onStartLearningClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 论文标题
        Text(
            text = paper.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // 作者信息
        if (!paper.authors.isNullOrBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = paper.authors,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // ArXiv ID
        if (!paper.arxivId.isNullOrBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Tag,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = paper.arxivId,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 状态标签
        StatusBadge(status = paper.parsedStatus)

        Spacer(modifier = Modifier.height(8.dp))

        // 摘要部分
        if (!paper.paperAbstract.isNullOrBlank()) {
            SectionCard(title = "摘要") {
                Text(
                    text = paper.paperAbstract,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.5
                )
            }
        }

        // 学习进度（如果有）
        if (learningSteps.isNotEmpty()) {
            SectionCard(title = "学习进度") {
                learningSteps.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 步骤序号
                        Box(
                            modifier = Modifier.size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (step.isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "已完成",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (step.isCompleted) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                    if (index < learningSteps.lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 操作按钮区域
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (paper.parsedStatus) {
                ParseStatus.NOT_PARSED -> {
                    // 未解析：显示解析按钮
                    ActionButton(
                        text = "解析论文",
                        icon = Icons.Default.AutoGraph,
                        onClick = onParseClick,
                        isLoading = isParsing,
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                }
                ParseStatus.PARSING -> {
                    // 解析中：显示等待提示
                    ActionButton(
                        text = "正在解析...",
                        icon = Icons.Default.Refresh,
                        onClick = { },
                        isLoading = true,
                        enabled = false,
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                }
                ParseStatus.COMPLETED -> {
                    // 已完成：显示开始学习按钮
                    ActionButton(
                        text = "开始学习",
                        icon = Icons.Default.School,
                        onClick = onStartLearningClick,
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                }
                ParseStatus.FAILED -> {
                    // 解析失败：显示重试按钮
                    ActionButton(
                        text = "重新解析",
                        icon = Icons.Default.Refresh,
                        onClick = onParseClick,
                        isLoading = isParsing,
                        containerColor = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // 底部间距
        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * 状态徽章组件
 */
@Composable
private fun StatusBadge(status: ParseStatus) {
    val (label, color) = when (status) {
        ParseStatus.COMPLETED -> "已解析" to MaterialTheme.colorScheme.primary
        ParseStatus.PARSING -> "解析中" to MaterialTheme.colorScheme.tertiary
        ParseStatus.FAILED -> "解析失败" to MaterialTheme.colorScheme.error
        ParseStatus.NOT_PARSED -> "未解析" to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small,
        contentColor = color
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (status) {
                    ParseStatus.COMPLETED -> Icons.Default.CheckCircle
                    ParseStatus.PARSING -> Icons.Default.Refresh
                    ParseStatus.FAILED -> Icons.Default.Error
                    ParseStatus.NOT_PARSED -> Icons.Default.Info
                },
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
