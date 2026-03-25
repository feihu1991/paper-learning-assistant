package com.paperlearning.assistant.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.paperlearning.assistant.ui.components.SearchBar
import com.paperlearning.assistant.ui.components.PaperListItem
import com.paperlearning.assistant.viewmodel.SearchViewModel
import com.paperlearning.assistant.viewmodel.SearchTab
import com.paperlearning.assistant.viewmodel.ArxivPaper

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onPaperClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        SearchBar(
            query = uiState.query,
            onQueryChange = { viewModel.updateQuery(it) },
            onSearch = {
                when (uiState.selectedTab) {
                    SearchTab.LOCAL -> viewModel.localSearch()
                    SearchTab.ARXIV -> viewModel.searchArxiv()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Row
        TabRow(
            selectedTabIndex = if (uiState.selectedTab == SearchTab.LOCAL) 0 else 1,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = uiState.selectedTab == SearchTab.LOCAL,
                onClick = { viewModel.selectTab(SearchTab.LOCAL) },
                text = { Text("本地论文") }
            )
            Tab(
                selected = uiState.selectedTab == SearchTab.ARXIV,
                onClick = { viewModel.selectTab(SearchTab.ARXIV) },
                text = { Text("arXiv") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loading Indicator
        if (uiState.isSearching) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Results List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (uiState.selectedTab) {
                    SearchTab.LOCAL -> {
                        items(uiState.localPapers, key = { it.id }) { paper ->
                            PaperListItem(
                                title = paper.title,
                                authors = paper.authors,
                                abstract = paper.abstract,
                                onClick = { onPaperClick(paper.id.toString()) }
                            )
                        }
                    }
                    SearchTab.ARXIV -> {
                        items(uiState.searchResults, key = { it.arxivId }) { paper ->
                            PaperListItem(
                                title = paper.title,
                                authors = paper.authors.joinToString(", "),
                                abstract = paper.abstract,
                                onClick = { onPaperClick(paper.arxivId) }
                            )
                        }
                    }
                }

                // Empty state
                if ((uiState.selectedTab == SearchTab.LOCAL && uiState.localPapers.isEmpty()) ||
                    (uiState.selectedTab == SearchTab.ARXIV && uiState.searchResults.isEmpty())
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (uiState.query.isBlank()) {
                                    "输入关键词搜索论文"
                                } else {
                                    "未找到相关论文"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Error message
        uiState.error?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("关闭")
                    }
                }
            }
        }
    }
}
