package com.paperlearning.assistant.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperlearning.assistant.data.model.LearningMode
import com.paperlearning.assistant.data.model.LearningStepEntity
import com.paperlearning.assistant.data.model.PaperEntity
import com.paperlearning.assistant.data.model.UserProgressEntity
import com.paperlearning.assistant.data.repository.LearningRepository
import com.paperlearning.assistant.data.repository.PaperRepository
import com.paperlearning.assistant.domain.usecase.GetLearningPathUseCase
import com.paperlearning.assistant.domain.usecase.GetPaperByIdUseCase
import com.paperlearning.assistant.domain.usecase.UpdateProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LearningUiState(
    val paper: PaperEntity? = null,
    val learningSteps: List<LearningStepEntity> = emptyList(),
    val currentStepIndex: Int = 0,
    val completedSteps: List<Int> = emptyList(),
    val learningMode: LearningMode = LearningMode.STANDARD,
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val error: String? = null,
    val navigateBack: Boolean = false
)

@HiltViewModel
class LearningViewModel @Inject constructor(
    private val paperRepository: PaperRepository,
    private val learningRepository: LearningRepository,
    private val getLearningPathUseCase: GetLearningPathUseCase,
    private val getPaperByIdUseCase: GetPaperByIdUseCase,
    private val updateProgressUseCase: UpdateProgressUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val paperId: Long = savedStateHandle.get<Long>("paperId") ?: 0L

    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState: StateFlow<LearningUiState> = _uiState.asStateFlow()

    init {
        loadLearningSession()
    }

    private fun loadLearningSession() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // 加载论文信息
                val paper = getPaperByIdUseCase(paperId)
                if (paper == null) {
                    _uiState.value = _uiState.value.copy(
                        error = "论文不存在",
                        isLoading = false
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(paper = paper)

                // 加载学习步骤
                getLearningPathUseCase(paperId)
                    .collect { steps ->
                        _uiState.value = _uiState.value.copy(
                            learningSteps = steps,
                            isLoading = false
                        )

                        // 加载用户进度
                        loadUserProgress()
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    private suspend fun loadUserProgress() {
        val progress = learningRepository.getUserProgressByPaperId(paperId)
        if (progress != null) {
            val completedStepsList = progress.completedSteps
                .trim('[', ']')
                .split(",")
                .filter { it.isNotBlank() }
                .map { it.toInt() }

            _uiState.value = _uiState.value.copy(
                currentStepIndex = progress.currentStep,
                completedSteps = completedStepsList,
                learningMode = progress.learningMode,
                isCompleted = progress.completedAt != null
            )
        } else {
            // 如果没有进度记录，创建新的学习会话
            startNewSession()
        }
    }

    private fun startNewSession() {
        viewModelScope.launch {
            val totalSteps = _uiState.value.learningSteps.size
            if (totalSteps > 0) {
                learningRepository.startLearningSession(
                    paperId = paperId,
                    learningMode = LearningMode.STANDARD,
                    totalSteps = totalSteps
                )
            }
        }
    }

    fun goToStep(stepIndex: Int) {
        val steps = _uiState.value.learningSteps
        if (stepIndex < 0 || stepIndex >= steps.size) return

        viewModelScope.launch {
            learningRepository.updateCurrentStep(paperId, stepIndex)
            _uiState.value = _uiState.value.copy(currentStepIndex = stepIndex)
        }
    }

    fun goToNextStep() {
        val currentIndex = _uiState.value.currentStepIndex
        val steps = _uiState.value.learningSteps
        if (currentIndex < steps.size - 1) {
            goToStep(currentIndex + 1)
        }
    }

    fun goToPreviousStep() {
        val currentIndex = _uiState.value.currentStepIndex
        if (currentIndex > 0) {
            goToStep(currentIndex - 1)
        }
    }

    fun markCurrentStepAsCompleted() {
        val currentIndex = _uiState.value.currentStepIndex
        
        viewModelScope.launch {
            learningRepository.markStepAsCompleted(paperId, currentIndex)
            
            val completedSteps = _uiState.value.completedSteps.toMutableList()
            if (currentIndex !in completedSteps) {
                completedSteps.add(currentIndex)
            }

            val isAllCompleted = completedSteps.size >= _uiState.value.learningSteps.size

            _uiState.value = _uiState.value.copy(
                completedSteps = completedSteps,
                isCompleted = isAllCompleted
            )

            // 更新进度
            updateProgressUseCase(
                UpdateProgressUseCase.UpdateRequest(
                    paperId = paperId,
                    currentStep = currentIndex,
                    completedSteps = completedSteps,
                    learningMode = _uiState.value.learningMode,
                    isCompleted = isAllCompleted
                )
            )
        }
    }

    fun setLearningMode(mode: LearningMode) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(learningMode = mode)
            
            updateProgressUseCase(
                UpdateProgressUseCase.UpdateRequest(
                    paperId = paperId,
                    currentStep = _uiState.value.currentStepIndex,
                    completedSteps = _uiState.value.completedSteps,
                    learningMode = mode,
                    isCompleted = _uiState.value.isCompleted
                )
            )
        }
    }

    fun completeLearning() {
        viewModelScope.launch {
            val allStepIndices = _uiState.value.learningSteps.indices.toList()
            
            updateProgressUseCase(
                UpdateProgressUseCase.UpdateRequest(
                    paperId = paperId,
                    currentStep = _uiState.value.learningSteps.size - 1,
                    completedSteps = allStepIndices,
                    learningMode = _uiState.value.learningMode,
                    isCompleted = true
                )
            )

            _uiState.value = _uiState.value.copy(
                completedSteps = allStepIndices,
                isCompleted = true,
                navigateBack = true
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetNavigation() {
        _uiState.value = _uiState.value.copy(navigateBack = false)
    }

    fun exitLearning() {
        _uiState.value = _uiState.value.copy(navigateBack = true)
    }
}
