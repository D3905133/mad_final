package uk.ac.tees.mad.d3905133.presentation.search

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.d3905133.data.TripzyRepository
import uk.ac.tees.mad.d3905133.domain.ApiResult
import uk.ac.tees.mad.d3905133.domain.LocationResult
import uk.ac.tees.mad.d3905133.domain.Recents
import javax.inject.Inject

data class UiState(
    val isFocused: Boolean,
    val searchQuery: String
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tripzyRepository: TripzyRepository
) : ViewModel() {
    private val isFocused: Boolean = checkNotNull(savedStateHandle[SearchDestination.isFocusedArg])

    val recentSearches =
        tripzyRepository.getAllRecentSearch()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                listOf<Recents>()
            )


    private val searchQuery = ""

    private val _uiState = MutableStateFlow(UiState(isFocused, searchQuery))
    val uiState = _uiState.asStateFlow()

    private val _searchAutoComplete =
        MutableStateFlow<ApiResult<LocationResult>>(ApiResult.Loading())
    val searchAutoComplete = _searchAutoComplete.asStateFlow()

    private fun fetchSearchAutoComplete() {
        viewModelScope.launch {
            tripzyRepository.getSearchAutoComplete(string = _uiState.value.searchQuery)
                .flowOn(Dispatchers.Default)
                .catch {
                    _searchAutoComplete.value =
                        ApiResult.Error(it.message ?: "Something went wrong")
                }
                .collect {
                    _searchAutoComplete.value = it
                }

        }
    }


    fun changeFocus(focus: Boolean) {
        _uiState.update {
            it.copy(isFocused = focus)
        }
    }

    fun onQueryChange(text: String) {
        _uiState.update {
            it.copy(searchQuery = text)
        }
        fetchSearchAutoComplete()
    }

    suspend fun updateRecentSearch(recent: Recents) {
        Log.d("Recent", _uiState.value.searchQuery)
        viewModelScope.launch {
            tripzyRepository.addRecentSearch(recent)
        }
    }

    suspend fun deleteRecentSearch(recent: Int) {
        viewModelScope.launch {
            tripzyRepository.deleteRecentSearch(recent)
        }
    }
}