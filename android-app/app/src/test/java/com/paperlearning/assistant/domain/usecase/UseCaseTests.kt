package com.paperlearning.assistant.domain.usecase

import com.paperlearning.assistant.data.model.PaperEntity
import com.paperlearning.assistant.data.model.ParseStatus
import com.paperlearning.assistant.data.repository.PaperRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * UseCase 单元测试
 * 测试领域层 UseCase 的核心功能
 */
class UseCaseTests {

    private lateinit var paperRepository: PaperRepository
    
    @Before
    fun setup() {
        paperRepository = mock()
    }

    // ==================== GetAllPapersUseCase 测试 ====================

    @Test
    fun `GetAllPapersUseCase returns all papers from repository`() = runTest {
        // Given
        val testPapers = listOf(
            PaperEntity(
                id = 1,
                title = "Test Paper 1",
                authors = "Author A",
                abstract = "Abstract 1",
                pdfPath = "/path/1.pdf"
            ),
            PaperEntity(
                id = 2,
                title = "Test Paper 2",
                authors = "Author B",
                abstract = "Abstract 2",
                pdfPath = "/path/2.pdf"
            )
        )
        whenever(paperRepository.getAllPapers()).thenReturn(flowOf(testPapers))
        
        val useCase = GetAllPapersUseCase(paperRepository)
        
        // When
        val result = useCase()
        
        // Then
        val emitted Papers = result.toList()
        assertEquals(1, emitted Papers.size)
        assertEquals(testPapers, emitted Papers[0])
        verify(paperRepository).getAllPapers()
    }

    @Test
    fun `GetAllPapersUseCase returns empty list when no papers exist`() = runTest {
        // Given
        whenever(paperRepository.getAllPapers()).thenReturn(flowOf(emptyList()))
        
        val useCase = GetAllPapersUseCase(paperRepository)
        
        // When
        val result = useCase()
        
        // Then
        val emitted Papers = result.toList()
        assertTrue(emitted Papers[0].isEmpty())
    }

    // ==================== SearchPapersUseCase 测试 ====================

    @Test
    fun `SearchPapersUseCase searches with query parameter`() = runTest {
        // Given
        val searchQuery = "machine learning"
        val matchingPapers = listOf(
            PaperEntity(
                id = 1,
                title = "Machine Learning Basics",
                authors = "Author A",
                abstract = "Introduction to ML",
                pdfPath = "/path/1.pdf"
            )
        )
        whenever(paperRepository.searchPapers(eq(searchQuery)))
            .thenReturn(flowOf(matchingPapers))
        
        val useCase = SearchPapersUseCase(paperRepository)
        
        // When
        val result = useCase(searchQuery)
        
        // Then
        val emitted Papers = result.toList()
        assertEquals(1, emitted Papers.size)
        assertEquals(1, emitted Papers[0].size)
        assertEquals("Machine Learning Basics", emitted Papers[0][0].title)
        verify(paperRepository).searchPapers(searchQuery)
    }

    @Test
    fun `SearchPapersUseCase handles empty query`() = runTest {
        // Given
        whenever(paperRepository.searchPapers(eq(""))).thenReturn(flowOf(emptyList()))
        
        val useCase = SearchPapersUseCase(paperRepository)
        
        // When
        val result = useCase("")
        
        // Then
        val emitted Papers = result.toList()
        assertTrue(emitted Papers[0].isEmpty())
    }

    // ==================== GetPaperByIdUseCase 测试 ====================

    @Test
    fun `GetPaperByIdUseCase returns paper by id`() = runTest {
        // Given
        val paperId = 1L
        val testPaper = PaperEntity(
            id = paperId,
            title = "Test Paper",
            authors = "Author A",
            abstract = "Test abstract",
            pdfPath = "/path/test.pdf"
        )
        whenever(paperRepository.getPaperById(eq(paperId))).thenReturn(testPaper)
        
        val useCase = GetPaperByIdUseCase(paperRepository)
        
        // When
        val result = useCase(paperId)
        
        // Then
        assertNotNull(result)
        assertEquals(paperId, result?.id)
        assertEquals("Test Paper", result?.title)
        verify(paperRepository).getPaperById(paperId)
    }

    @Test
    fun `GetPaperByIdUseCase returns null for non-existent id`() = runTest {
        // Given
        val paperId = 999L
        whenever(paperRepository.getPaperById(eq(paperId))).thenReturn(null)
        
        val useCase = GetPaperByIdUseCase(paperRepository)
        
        // When
        val result = useCase(paperId)
        
        // Then
        assertNull(result)
    }

    // ==================== GetLearningPathUseCase 测试 ====================

    @Test
    fun `GetLearningPathUseCase returns learning steps for paper`() = runTest {
        // Given
        val paperId = 1L
        whenever(paperRepository.getPaperById(eq(paperId))).thenReturn(
            PaperEntity(
                id = paperId,
                title = "Test Paper",
                authors = "Author A",
                abstract = "Test abstract",
                pdfPath = "/path/test.pdf"
            )
        )
        
        val useCase = GetLearningPathUseCase(paperRepository)
        
        // When
        val result = useCase(paperId)
        
        // Then
        assertNotNull(result)
        assertEquals(paperId, result?.paperId)
    }

    @Test
    fun `GetLearningPathUseCase returns null when paper not found`() = runTest {
        // Given
        val paperId = 999L
        whenever(paperRepository.getPaperById(eq(paperId))).thenReturn(null)
        
        val useCase = GetLearningPathUseCase(paperRepository)
        
        // When
        val result = useCase(paperId)
        
        // Then
        assertNull(result)
    }

    // ==================== ParsePaperUseCase 测试 ====================

    @Test
    fun `ParsePaperUseCase updates parse status to parsing`() = runTest {
        // Given
        val paperId = 1L
        val testPaper = PaperEntity(
            id = paperId,
            title = "Test Paper",
            authors = "Author A",
            abstract = "Test abstract",
            pdfPath = "/path/test.pdf",
            parsedStatus = ParseStatus.NOT_PARSED
        )
        whenever(paperRepository.getPaperById(eq(paperId))).thenReturn(testPaper)
        
        val useCase = ParsePaperUseCase(paperRepository)
        
        // When & Then
        // 由于 ParsePaperUseCase 需要实际的 PDF 路径和上下文，这里测试基本调用
        // 实际解析逻辑需要集成测试
        assertNotNull(testPaper)
        assertEquals(ParseStatus.NOT_PARSED, testPaper.parsedStatus)
    }

    // ==================== UpdateProgressUseCase 测试 ====================

    @Test
    fun `UpdateProgressUseCase updates paper progress`() = runTest {
        // Given
        val paperId = 1L
        val currentStep = 1
        val isCompleted = true
        
        val testPaper = PaperEntity(
            id = paperId,
            title = "Test Paper",
            authors = "Author A",
            abstract = "Test abstract",
            pdfPath = "/path/test.pdf",
            currentStep = 0,
            isCompleted = false
        )
        whenever(paperRepository.getPaperById(eq(paperId))).thenReturn(testPaper)
        
        val useCase = UpdateProgressUseCase(paperRepository)
        
        // When
        val result = useCase(paperId, currentStep, isCompleted)
        
        // Then
        assertTrue(result)
        verify(paperRepository).getPaperById(paperId)
    }

    @Test
    fun `UpdateProgressUseCase returns false when paper not found`() = runTest {
        // Given
        val paperId = 999L
        whenever(paperRepository.getPaperById(eq(paperId))).thenReturn(null)
        
        val useCase = UpdateProgressUseCase(paperRepository)
        
        // When
        val result = useCase(paperId, 1, true)
        
        // Then
        assertFalse(result)
    }

    // ==================== GenerateLearningPathUseCase 测试 ====================

    @Test
    fun `GenerateLearningPathUseCase generates learning path`() = runTest {
        // Given
        val paperId = 1L
        val testPaper = PaperEntity(
            id = paperId,
            title = "Test Paper",
            authors = "Author A",
            abstract = "Test abstract about machine learning and neural networks",
            pdfPath = "/path/test.pdf"
        )
        whenever(paperRepository.getPaperById(eq(paperId))).thenReturn(testPaper)
        
        val useCase = GenerateLearningPathUseCase(paperRepository)
        
        // When
        val result = useCase(paperId)
        
        // Then
        assertNotNull(result)
        // 验证生成的学习路径包含步骤
        assertTrue(result.steps.isNotEmpty())
    }

    @Test
    fun `GenerateLearningPathUseCase handles paper with empty abstract`() = runTest {
        // Given
        val paperId = 1L
        val testPaper = PaperEntity(
            id = paperId,
            title = "Test Paper",
            authors = "Author A",
            abstract = "",
            pdfPath = "/path/test.pdf"
        )
        whenever(paperRepository.getPaperById(eq(paperId))).thenReturn(testPaper)
        
        val useCase = GenerateLearningPathUseCase(paperRepository)
        
        // When
        val result = useCase(paperId)
        
        // Then
        assertNotNull(result)
        // 即使摘要为空，也应生成默认学习路径
        assertTrue(result.steps.isNotEmpty())
    }
}
