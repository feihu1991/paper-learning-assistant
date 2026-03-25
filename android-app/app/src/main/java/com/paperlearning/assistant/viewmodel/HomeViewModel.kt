package com.paperlearning.assistant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperlearning.assistant.data.model.PaperEntity
import com.paperlearning.assistant.data.model.ParseStatus
import com.paperlearning.assistant.data.repository.PaperRepository
import com.paperlearning.assistant.domain.usecase.GetAllPapersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val recentPapers: List<PaperEntity> = emptyList(),
    val learningPapers: List<PaperEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllPapersUseCase: GetAllPapersUseCase,
    private val paperRepository: PaperRepository
) : ViewModel() {

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
                _uiState.value = _uiState.value.copy(
                    error = e.message,
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
}
