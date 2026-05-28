package com.example.devfinder.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devfinder.core.domain.GithubRepository
import com.example.devfinder.core.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: GithubRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Idle)
    val uiState = _uiState.asStateFlow()

    init {
        observeFavorites()
        syncFavorites()
    }
    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getFavorites()
                .catch {
                    _uiState.value = FavoritesUiState.Error("Não foi possível carregar os favoritos")
                }
                .collect { favoriteList ->
                    if (favoriteList.isEmpty()) {
                        _uiState.value = FavoritesUiState.Empty
                    } else {
                        _uiState.value = FavoritesUiState.Success(favoriteList)
                    }
                }
        }
    }
    private fun syncFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            val favoritesList = repository.getFavorites().first()
            val userListToUpdate = mutableListOf<User>()
            favoritesList.forEach { user ->
                val response = repository.getUser(user.login)
                response.getOrNull()?.let {
                    userListToUpdate.add(it)
                }
            }
            if (userListToUpdate.isNotEmpty()) {
                repository.updateFavorites(userListToUpdate)
            }
        }
    }
}