package com.paperlearning.assistant.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperlearning.assistant.data.model.LearningStepEntity
import com.paperlearning.assistant.data.model.PaperEntity
import com.paperlearning.assistant.data.model.ParseStatus
import com.paperlearning.assistant.data.repository.LearningRepository
import com.paperlearning.assistant.data.repository.PaperRepository
import com.paperlearning.assistant.domain.usecase.GetPaperByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaperDetailUiState(
    val paper: PaperEntity? = null,
    val learningSteps: List<LearningStepEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isParsing: Boolean = false,
    val error: String? = null,
    val navigateToLearning: Boolean = false
)

@HiltViewModel
class PaperDetailViewModel @Inject constructor(
    private val paperRepository: PaperRepository,
    private val learningRepository: LearningRepository,
    private val getPaperByIdUseCase: GetPaperByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val paperId: Long = savedStateHandle.get<Long>("paperId") ?: 0L

    private val _uiState = MutableStateFlow(PaperDetailUiState())
    val uiState: StateFlow<PaperDetailUiState> = _uiState.asStateFlow()

    init {
        loadPaperDetails()
    }

    private fun loadPaperDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val paper = getPaperByIdUseCase(paperId)
                if (paper == null) {
                    _uiState.value = _uiState.value.copy(
                        error = "论文不存在",
                        isLoading = false
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(paper = paper)

                // 如果已解析，加载学习步骤
                if (paper.parsedStatus == ParseStatus.COMPLETED) {
                    learningRepository.getLearningStepsByPaperId(paper.id)
                        .collect { steps ->
                            _uiState.value = _uiState.value.copy(
                                learningSteps = steps,
                                isLoading = false
                            )
                        }
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun parsePaper() {
        val paper = _uiState.value.paper ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isParsing = true,
                error = null
            )

            try {
                // TODO: 实现论文解析逻辑
                // parsePaperUseCase.execute(paper)
                
                // 重新加载论文详情
                loadPaperDetails()
                
                _uiState.value = _uiState.value.copy(
                    isParsing = false,
                    navigateToLearning = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isParsing = false
                )
            }
        }
    }

    fun startLearning() {
        _uiState.value = _uiState.value.copy(navigateToLearning = true)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetNavigation() {
        _uiState.value = _uiState.value.copy(navigateToLearning = false)
    }
}
