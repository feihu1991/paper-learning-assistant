package com.paperlearning.assistant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperlearning.assistant.data.model.PaperEntity
import com.paperlearning.assistant.data.remote.arxiv.ArxivRepository
import com.paperlearning.assistant.data.repository.PaperRepository
import com.paperlearning.assistant.domain.usecase.GetAllPapersUseCase
import com.paperlearning.assistant.domain.usecase.SearchPapersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArxivPaper(
    val arxivId: String,
    val title: String,
    val authors: List<String>,
    val abstract: String,
    val published: String,
    val pdfUrl: String
)

data class SearchUiState(
    val query: String = "",
    val searchResults: List<ArxivPaper> = emptyList(),
    val localPapers: List<PaperEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null,
    val selectedTab: SearchTab = SearchTab.LOCAL
)

enum class SearchTab {
    LOCAL, ARXIV
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getAllPapersUseCase: GetAllPapersUseCase,
    private val searchPapersUseCase: SearchPapersUseCase,
    private val paperRepository: PaperRepository,
    private val arxivRepository: ArxivRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        loadLocalPapers()
    }

    private fun loadLocalPapers() {
        viewModelScope.launch {
            getAllPapersUseCase().collect { papers ->
                _uiState.value = _uiState.value.copy(localPapers = papers)
            }
        }
    }

    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun searchArxiv() {
        val query = _uiState.value.query
        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSearching = true,
                error = null
            )

            try {
                val results = arxivRepository.search(query)
                _uiState.value = _uiState.value.copy(
                    searchResults = results,
                    isSearching = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isSearching = false
                )
            }
        }
    }

    fun localSearch() {
        val query = _uiState.value.query
        viewModelScope.launch {
            if (query.isBlank()) {
                loadLocalPapers()
            } else {
                searchPapersUseCase(query).collect { papers ->
                    _uiState.value = _uiState.value.copy(localPapers = papers)
                }
            }
        }
    }

    fun selectTab(tab: SearchTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
