package uk.ac.tees.mad.d3905133.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.d3905133.data.TripzyRepository
import uk.ac.tees.mad.d3905133.domain.Resource
import uk.ac.tees.mad.d3905133.domain.UserResponse
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository: TripzyRepository
) : ViewModel() {
    private val _currentUserStatus = Channel<CurrentUser>()
    val currentUserStatus = _currentUserStatus.receiveAsFlow()

    init {
        getUserDetails()
    }

    fun getUserDetails() =
        viewModelScope.launch {
            repository.getCurrentUser().collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _currentUserStatus.send(CurrentUser(isError = result.message))
                    }

                    is Resource.Loading -> {
                        _currentUserStatus.send(CurrentUser(isLoading = true))
                    }

                    is Resource.Success -> {
                        _currentUserStatus.send(CurrentUser(isSuccess = result.data))
                    }
                }
            }
        }
}

data class CurrentUser(
    val isLoading: Boolean = false,
    val isSuccess: UserResponse? = null,
    val isError: String? = null
)