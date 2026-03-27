package com.paperlearning.assistant.ui.screens.learning

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.paperlearning.assistant.ui.components.StepProgressIndicator
import com.paperlearning.assistant.viewmodel.LearningViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningScreen(
    onNavigateBack: () -> Unit,
    viewModel: LearningViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 处理导航返回
    LaunchedEffect(uiState.navigateBack) {
        if (uiState.navigateBack) {
            onNavigateBack()
            viewModel.resetNavigation()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.paper?.title ?: "学习",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    // 学习模式选择
                    if (uiState.learningSteps.isNotEmpty()) {
                        ExposedDropdownMenuBox(
                            expanded = false,
                            onExpandedChange = { }
                        ) {
                            AssistChip(
                                onClick = { },
                                label = {
                                    Text(
                                        text = uiState.learningMode.displayName,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            // 底部导航栏
            if (uiState.learningSteps.isNotEmpty() && !uiState.isCompleted) {
                Surface(
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 上一步按钮
                        OutlinedButton(
                            onClick = { viewModel.goToPreviousStep() },
                            modifier = Modifier.weight(1f),
                            enabled = uiState.currentStepIndex > 0,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("上一步")
                        }

                        // 下一步按钮
                        Button(
                            onClick = { viewModel.goToNextStep() },
                            modifier = Modifier.weight(1f),
                            enabled = uiState.currentStepIndex < uiState.learningSteps.size - 1,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("下一步")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.error != null -> {
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
                            text = "加载失败：${uiState.error}",
                            color = MaterialTheme.colorScheme.error
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

                uiState.learningSteps.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "暂无学习路径",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "请先解析论文以生成学习路径",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    // 学习完成状态
                    if (uiState.isCompleted) {
                        LearningCompletedScreen(
                            paperTitle = uiState.paper?.title ?: "",
                            totalSteps = uiState.learningSteps.size,
                            onNavigateBack = onNavigateBack,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // 正常学习界面
                        LearningContent(
                            learningSteps = uiState.learningSteps,
                            currentStepIndex = uiState.currentStepIndex,
                            completedSteps = uiState.completedSteps,
                            onStepCompleted = { viewModel.markCurrentStepAsCompleted() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LearningContent(
    learningSteps: List<com.paperlearning.assistant.data.model.LearningStepEntity>,
    currentStepIndex: Int,
    completedSteps: List<Int>,
    onStepCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 进度指示器
        StepProgressIndicator(
            totalSteps = learningSteps.size,
            currentStepIndex = currentStepIndex,
            completedSteps = completedSteps,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 当前步骤内容
        val currentStep = learningSteps[currentStepIndex]
        StepContent(
            step = currentStep,
            isCompleted = currentStepIndex in completedSteps,
            onMarkCompleted = onStepCompleted
        )

        // 步骤列表（可选：显示所有步骤概览）
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "学习路径概览",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                learningSteps.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 步骤状态图标
                        Icon(
                            imageVector = when {
                                index == currentStepIndex -> Icons.Default.PlayArrow
                                index in completedSteps -> Icons.Filled.CheckCircle
                                else -> Icons.Filled.RadioButtonUnchecked
                            },
                            contentDescription = null,
                            tint = when {
                                index == currentStepIndex -> MaterialTheme.colorScheme.primary
                                index in completedSteps -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            },
                            modifier = Modifier.size(20.dp)
                        )
                        
                        // 步骤标题
                        Text(
                            text = "${index + 1}. ${step.title}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = when {
                                index == currentStepIndex -> MaterialTheme.colorScheme.onSurface
                                index in completedSteps -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        
                        // 当前步骤标记
                        if (index == currentStepIndex) {
                            AssistChip(
                                onClick = { },
                                label = { Text("当前", style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
            }
        }

        // 底部间距（为底部导航栏留出空间）
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun LearningCompletedScreen(
    paperTitle: String,
    totalSteps: Int,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 完成图标
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(96.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 完成标题
        Text(
            text = "🎉 学习完成！",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 论文标题
        Text(
            text = paperTitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 完成的步骤数
        Text(
            text = "已完成 $totalSteps 个学习步骤",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 返回按钮
        Button(
            onClick = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("返回主页")
        }
    }
}
