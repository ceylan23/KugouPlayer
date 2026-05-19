package com.kugou.player.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kugou.player.data.MusicRepository
import com.kugou.player.model.Song
import com.kugou.player.model.UiState
import com.kugou.player.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<UiState<List<Song>>>(UiState.Empty)
    val searchResults: StateFlow<UiState<List<Song>>> = _searchResults.asStateFlow()

    private val _hotSearches = MutableStateFlow<List<String>>(emptyList())
    val hotSearches: StateFlow<List<String>> = _hotSearches.asStateFlow()

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    private var currentQuery: String = ""
    private var currentPage: Int = 1
    private var isLoadingMore: Boolean = false
    private var hasMore: Boolean = true

    private val allResults = mutableListOf<Song>()

    init {
        loadHotSearches()
    }

    fun search(query: String) {
        if (query.isBlank()) return
        currentQuery = query
        currentPage = 1
        hasMore = true
        allResults.clear()

        viewModelScope.launch {
            _searchResults.value = UiState.Loading
            try {
                val results = repository.searchSongs(query, currentPage, Constants.SEARCH_PAGE_SIZE)
                if (results.isEmpty()) {
                    _searchResults.value = UiState.Empty
                    hasMore = false
                } else {
                    allResults.addAll(results)
                    _searchResults.value = UiState.Success(allResults.toList())
                    hasMore = results.size >= Constants.SEARCH_PAGE_SIZE
                }
            } catch (e: Exception) {
                _searchResults.value = UiState.Error(e.message ?: "搜索失败")
            }
        }
    }

    fun loadMore() {
        if (isLoadingMore || !hasMore || currentQuery.isBlank()) return
        isLoadingMore = true
        currentPage++

        viewModelScope.launch {
            try {
                val results = repository.searchSongs(currentQuery, currentPage, Constants.SEARCH_PAGE_SIZE)
                if (results.isEmpty()) {
                    hasMore = false
                } else {
                    allResults.addAll(results)
                    _searchResults.value = UiState.Success(allResults.toList())
                    hasMore = results.size >= Constants.SEARCH_PAGE_SIZE
                }
            } catch (e: Exception) {
                currentPage--
            } finally {
                isLoadingMore = false
            }
        }
    }

    fun loadHotSearches() {
        viewModelScope.launch {
            try {
                val hotList = repository.getHotSearches()
                _hotSearches.value = hotList
            } catch (_: Exception) {
                _hotSearches.value = emptyList()
            }
        }
    }

    fun loadSuggestions(query: String) {
        if (query.isBlank()) {
            _suggestions.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                // Suggestions not implemented yet, use hot searches as fallback
                _suggestions.value = emptyList()
            } catch (_: Exception) {
                _suggestions.value = emptyList()
            }
        }
    }

    fun clearResults() {
        currentQuery = ""
        currentPage = 1
        allResults.clear()
        _searchResults.value = UiState.Empty
    }
}
