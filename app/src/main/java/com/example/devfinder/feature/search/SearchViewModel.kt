package com.example.devfinder.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devfinder.core.domain.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: GithubRepository
): ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            @OptIn(kotlinx.coroutines.FlowPreview::class)
            _query.debounce(800L)
                .filter { it.length >= 3 }
                .distinctUntilChanged()
                .collectLatest {
                    currentQuery ->
                    if (currentQuery.length < 3) {
                        _uiState.value = SearchUiState.Idle
                        return@collectLatest
                    }
                    performSearch(currentQuery)
                }
        }
    }

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.OnQueryChanged -> {
                _query.value = intent.query
                if (intent.query.isEmpty()) _uiState.value = SearchUiState.Idle
            }

            is SearchIntent.OnUserClicked -> {
                //navigation and etc
            }
        }
    }
    private suspend fun performSearch(query: String) {
        _uiState.value = SearchUiState.Loading
        repository.getUsers(query)
            .onSuccess { users -> _uiState.value = SearchUiState.Success(users) }
            .onFailure { error -> _uiState.value = SearchUiState.Error(error.message ?: "Unknown Error") }
    }
}