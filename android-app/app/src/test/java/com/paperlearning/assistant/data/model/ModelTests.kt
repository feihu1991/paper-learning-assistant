package com.paperlearning.assistant.data.model

import org.junit.Test
import org.junit.Assert.*

class ModelTests {
    @Test
    fun `ParseStatus values are correct`() {
        assertEquals(ParseStatus.NOT_PARSED.order, 0)
        assertEquals(ParseStatus.PARSING.order, 1)
        assertEquals(ParseStatus.COMPLETED.order, 2)
        assertEquals(ParseStatus.FAILED.order, 3)
    }
    
    @Test
    fun `LearningMode estimatedMinutes are correct`() {
        assertEquals(LearningMode.FAST.estimatedMinutes, 5)
        assertEquals(LearningMode.STANDARD.estimatedMinutes, 20)
        assertEquals(LearningMode.DEEP.estimatedMinutes, 60)
    }
    
    @Test
    fun `PaperEntity creates with default values`() {
        val paper = PaperEntity(
            title = "Test Paper",
            authors = """["Author A", "Author B"]""",
            abstract = "Test abstract",
            pdfPath = "/path/to/file.pdf"
        )
        assertEquals(0L, paper.id)
        assertEquals(ParseStatus.NOT_PARSED, paper.parsedStatus)
        assertNotNull(paper.createdAt)
    }
}
