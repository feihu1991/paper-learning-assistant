package com.paperlearning.assistant.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperlearning.assistant.data.model.PaperEntity
import com.paperlearning.assistant.data.model.ParseStatus
import com.paperlearning.assistant.data.repository.PaperRepository
import com.paperlearning.assistant.domain.usecase.GetAllPapersUseCase
import com.paperlearning.assistant.domain.usecase.ImportPaperUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val recentPapers: List<PaperEntity> = emptyList(),
    val learningPapers: List<PaperEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val importSuccess: Boolean = false,
    val importedPaperId: Long? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllPapersUseCase: GetAllPapersUseCase,
    private val paperRepository: PaperRepository,
    private val importPaperUseCase: ImportPaperUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadPapers()
    }

    private fun loadPapers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                getAllPapersUseCase().collect { papers ->
                    _uiState.value = _uiState.value.copy(
                        recentPapers = papers.take(10),
                        learningPapers = papers.filter { it.parsedStatus == ParseStatus.COMPLETED },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load papers", e)
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    /**
     * Import a PDF paper from a content URI.
     * Delegates to ImportPaperUseCase which copies the file to internal storage
     * and creates a database entry.
     *
     * @param uri The content URI of the selected PDF file
     */
    fun importPaper(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val result = importPaperUseCase(context, uri)
                if (result.success && result.paperId != null) {
                    Log.i(TAG, "Paper imported: id=${result.paperId}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        importSuccess = true,
                        importedPaperId = result.paperId
                    )
                } else {
                    Log.e(TAG, "Paper import failed: ${result.errorMessage}")
                    _uiState.value = _uiState.value.copy(
                        error = "导入失败：${result.errorMessage}",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to import paper", e)
                _uiState.value = _uiState.value.copy(
                    error = "导入失败：${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun refresh() {
        loadPapers()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Reset import success state after navigation.
     */
    fun resetImportState() {
        _uiState.value = _uiState.value.copy(importSuccess = false, importedPaperId = null)
    }
}
