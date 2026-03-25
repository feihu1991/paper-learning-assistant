package com.paperlearning.assistant.ui.screens.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.paperlearning.assistant.data.model.PaperEntity
import com.paperlearning.assistant.data.model.ParseStatus
import com.paperlearning.assistant.ui.screens.home.HomeScreen
import com.paperlearning.assistant.viewmodel.HomeUiState
import com.paperlearning.assistant.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * HomeScreen UI 测试
 * 测试主界面的 Compose UI 组件
 */
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: HomeViewModel
    private lateinit var mockUiState: MutableStateFlow<HomeUiState>

    @Before
    fun setup() {
        mockUiState = MutableStateFlow(HomeUiState())
        viewModel = mock()
        whenever(viewModel.uiState).thenAnswer { mockUiState }
    }

    // ==================== 基础 UI 元素测试 ====================

    @Test
    fun homeScreen_displaysTopAppBarWithTitle() {
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = {},
                onNavigateToPaperDetail = {}
            )
        }

        // 验证顶部应用栏标题
        composeTestRule.onNodeWithText("Paper Learning").assertExists()
    }

    @Test
    fun homeScreen_displaysRefreshButton() {
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = {},
                onNavigateToPaperDetail = {}
            )
        }

        // 验证刷新按钮存在
        composeTestRule.onNodeWithContentDescription("刷新").assertExists()
    }

    @Test
    fun homeScreen_displaysSearchButton() {
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = {},
                onNavigateToPaperDetail = {}
            )
        }

        // 验证搜索按钮存在
        composeTestRule.onNodeWithContentDescription("搜索").assertExists()
    }

    @Test
    fun homeScreen_displaysAddButton() {
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = {},
                onNavigateToPaperDetail = {}
            )
        }

        // 验证添加按钮存在
        composeTestRule.onNodeWithContentDescription("导入论文").assertExists()
    }

    // ==================== 加载状态测试 ====================

    @Test
    fun homeScreen_showsLoadingIndicatorWhenLoading() {
        // Given
        mockUiState.value = HomeUiState(isLoading = true)

        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = {},
                onNavigateToPaperDetail = {}
            )
        }

        // Then - 验证加载指示器存在
        composeTestRule.onNodeWithTag("loadingIndicator").assertExists()
    }

    @Test
    fun homeScreen_hidesLoadingIndicatorWhenLoaded() {
        // Given
        mockUiState.value = HomeUiState(
            isLoading = false,
            recentPapers = listOf(
                PaperEntity(
                    id = 1,
                    title = "Test Paper",
                    authors = "Author A",
                    abstract = "Test abstract",
                    pdfPath = "/path/test.pdf"
                )
            )
        )

        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = {},
                onNavigateToPaperDetail = {}
            )
        }

        // Then - 验证加载指示器不存在
        composeTestRule.onNodeWithTag("loadingIndicator").assertDoesNotExist()
    }

    // ==================== 错误状态测试 ====================

    @Test
    fun homeScreen_showsErrorMessageWhenErrorOccurs() {
        // Given
        val errorMessage = "加载失败，请重试"
        mockUiState.value = HomeUiState(
            isLoading = false,
            error = errorMessage
        )

        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = {},
                onNavigateToPaperDetail = {}
            )
        }

        // Then - 验证错误消息显示
        composeTestRule.onNodeWithText("加载失败：$errorMessage").assertExists()
    }

    @Test
    fun homeScreen_showsRetryButtonOnError() {
        // Given
        mockUiState.value = HomeUiState(
            isLoading = false,
            error = "Test error"
        )

        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = {},
                onNavigateToPaperDetail = {}
            )
        }

        // Then - 验证重试按钮存在
        composeTestRule.onNodeWithText("重试").assertExists()
    }

    // ==================== 空状态测试 ====================

    @Test
    fun homeScreen_showsEmptyStateWhenNoPapers() {
        // Given
        mockUiState.value = HomeUiState(
            isLoading = false,
            recentPapers = emptyList()
        )

        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = {},
                onNavigateToPaperDetail = {}
            )
        }

        // Then - 验证空状态消息
        composeTestRule.onNodeWithText("暂无论文").assertExists()
        composeTestRule.onNodeWithText("点击右下角按钮导入论文").assertExists()
    }

    // ==================== 论文列表测试 ====================

    @Test
    fun homeScreen_displaysPaperListWhenPapersExist() {
        // Given
        val testPapers = listOf(
            PaperEntity(
                id = 1,
                title = "Test Paper 1",
                authors = "Author A",
                abstract = "First test abstract",
                pdfPath = "/path/1.pdf"
            ),
            PaperEntity(
                id = 2,
                title = "Test Paper 2",
                authors = "Author B",
                abstract = "Second test abstract",
                pdfPath = "/path/2.pdf"
            )
        )
        mockUiState.value = HomeUiState(
            isLoading = false,
            recentPapers = testPapers
        )

        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = {},
                onNavigateToPaperDetail = {}
            )
        }

        // Then - 验证论文标题显示
        composeTestRule.onNodeWithText("Test Paper 1").assertExists()
        composeTestRule.onNodeWithText("Test Paper 2").assertExists()
        composeTestRule.onNodeWithText("最近论文").assertExists()
    }

    @Test
    fun homeScreen_displaysRecentPapersSectionHeader() {
        // Given
        mockUiState.value = HomeUiState(
            isLoading = false,
            recentPapers = listOf(
                PaperEntity(
                    id = 1,
                    title = "Test Paper",
                    authors = "Author A",
                    abstract = "Test abstract",
                    pdfPath = "/path/test.pdf"
                )
            )
        )

        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = {},
                onNavigateToPaperDetail = {}
            )
        }

        // Then - 验证"最近论文"标题显示
        composeTestRule.onNodeWithText("最近论文").assertExists()
    }

    // ==================== 学习进度卡片测试 ====================

    @Test
    fun homeScreen_showsProgressCardWhenLearningPapersExist() {
        // Given
        val learningPapers = listOf(
            PaperEntity(
                id = 1,
                title = "Learning Paper 1",
                authors = "Author A",
                abstract = "Abstract 1",
                pdfPath = "/path/1.pdf",
                parsedStatus = ParseStatus.COMPLETED
            )
        )
        mockUiState.value = HomeUiState(
            isLoading = false,
            recentPapers = learningPapers,
            learningPapers = learningPapers
        )

        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = {},
                onNavigateToPaperDetail = {}
            )
        }

        // Then - 验证进度卡片存在
        // ProgressCard 应该显示完成数量
        composeTestRule.onNodeWithText("学习进度").assertExists()
    }

    @Test
    fun homeScreen_hidesProgressCardWhenNoLearningPapers() {
        // Given
        val papers = listOf(
            PaperEntity(
                id = 1,
                title = "Paper 1",
                authors = "Author A",
                abstract = "Abstract 1",
                pdfPath = "/path/1.pdf",
                parsedStatus = ParseStatus.NOT_PARSED
            )
        )
        mockUiState.value = HomeUiState(
            isLoading = false,
            recentPapers = papers,
            learningPapers = emptyList()
        )

        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = {},
                onNavigateToPaperDetail = {}
            )
        }

        // Then - 验证进度卡片不存在（当没有学习中的论文时）
        composeTestRule.onNodeWithText("学习进度").assertDoesNotExist()
    }

    // ==================== 交互测试 ====================

    @Test
    fun homeScreen_refreshButtonTriggersRefresh() {
        var refreshCalled = false
        
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = {},
                onNavigateToPaperDetail = {},
                viewModel = object : HomeViewModel(
                    mock(),
                    mock()
                ) {
                    override fun refresh() {
                        refreshCalled = true
                    }
                }
            )
        }

        // When - 点击刷新按钮
        composeTestRule.onNodeWithContentDescription("刷新").performClick()

        // Then - 验证刷新被调用
        assertTrue("Refresh should be called", refreshCalled)
    }

    @Test
    fun homeScreen_searchButtonNavigatesToSearch() {
        var navigateToSearchCalled = false
        
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = { navigateToSearchCalled = true },
                onNavigateToPaperDetail = {}
            )
        }

        // When - 点击搜索按钮
        composeTestRule.onNodeWithContentDescription("搜索").performClick()

        // Then - 验证导航到搜索页面
        assertTrue("Should navigate to search", navigateToSearchCalled)
    }

    @Test
    fun homeScreen_addButtonIsClickable() {
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = {},
                onNavigateToPaperDetail = {}
            )
        }

        // When & Then - 验证添加按钮可点击
        composeTestRule.onNodeWithContentDescription("导入论文")
            .assertExists()
            .performClick()
    }

    // ==================== 论文卡片点击测试 ====================

    @Test
    fun homeScreen_paperCardClickNavigatesToDetail() {
        // Given
        val paperId = 1L
        var capturedPaperId: Long? = null
        
        mockUiState.value = HomeUiState(
            isLoading = false,
            recentPapers = listOf(
                PaperEntity(
                    id = paperId,
                    title = "Test Paper",
                    authors = "Author A",
                    abstract = "Test abstract",
                    pdfPath = "/path/test.pdf"
                )
            )
        )

        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = {},
                onNavigateToPaperDetail = { id -> capturedPaperId = id }
            )
        }

        // When - 点击论文卡片
        composeTestRule.onNodeWithText("Test Paper").performClick()

        // Then - 验证导航到详情页并传递正确的论文 ID
        assertNotNull("Paper ID should be captured", capturedPaperId)
        assertEquals(paperId, capturedPaperId)
    }

    // ==================== 错误恢复测试 ====================

    @Test
    fun homeScreen_retryButtonClearsErrorAndRefreshes() {
        // Given
        var clearErrorCalled = false
        var refreshCalled = false
        
        mockUiState.value = HomeUiState(
            isLoading = false,
            error = "Test error"
        )

        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = {},
                onNavigateToPaperDetail = {},
                viewModel = object : HomeViewModel(
                    mock(),
                    mock()
                ) {
                    override fun clearError() {
                        clearErrorCalled = true
                    }
                    
                    override fun refresh() {
                        refreshCalled = true
                    }
                }
            )
        }

        // When - 点击重试按钮
        composeTestRule.onNodeWithText("重试").performClick()

        // Then - 验证错误被清除并刷新
        assertTrue("ClearError should be called", clearErrorCalled)
        assertTrue("Refresh should be called", refreshCalled)
    }

    // ==================== UI 状态更新测试 ====================

    @Test
    fun homeScreen_updatesWhenUiStateChanges() {
        // Given - 初始为空状态
        mockUiState.value = HomeUiState(isLoading = false)
        
        composeTestRule.setContent {
            HomeScreen(
                onNavigateToSearch = {},
                onNavigateToPaperDetail = {}
            )
        }

        // 验证初始空状态
        composeTestRule.onNodeWithText("暂无论文").assertExists()

        // When - 更新 UI 状态为有论文
        mockUiState.value = HomeUiState(
            isLoading = false,
            recentPapers = listOf(
                PaperEntity(
                    id = 1,
                    title = "New Paper",
                    authors = "Author A",
                    abstract = "New abstract",
                    pdfPath = "/path/new.pdf"
                )
            )
        )

        // Then - 验证 UI 更新显示新论文
        composeTestRule.onNodeWithText("New Paper").assertExists()
    }
}
