package uk.ac.tees.mad.d3905133.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import uk.ac.tees.mad.d3905133.data.TripzyRepository
import uk.ac.tees.mad.d3905133.domain.LoginDetails
import uk.ac.tees.mad.d3905133.domain.LoginResponse
import uk.ac.tees.mad.d3905133.domain.Resource
import uk.ac.tees.mad.d3905133.domain.SignInResult
import uk.ac.tees.mad.d3905133.domain.SignInState
import uk.ac.tees.mad.d3905133.domain.SignInState1
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.d3905133.domain.UserResponse
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: TripzyRepository
) : ViewModel() {

    private val _signInState = Channel<SignInState>()
    val signInState = _signInState.receiveAsFlow()

    private val _state = MutableStateFlow(SignInState1())
    val state = _state.asStateFlow()

    private val _currentUser = MutableStateFlow(FirebaseAuth.getInstance().currentUser)
    val currentUser = _currentUser.asStateFlow()


    fun onSignInWithGoogleResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage
            )
        }
    }

    fun resetState() {
        _state.update { SignInState1() }
    }

    fun loginUser(email: String, password: String) = viewModelScope.launch {
        repository.loginUser(email, password).collect { result ->
            when (result) {
                is Resource.Error -> {
                    _signInState.send(SignInState(isError = result.message))
                }

                is Resource.Loading -> {
                    _signInState.send(SignInState(isLoading = true))
                }

                is Resource.Success -> {
                    _signInState.send(SignInState(isSuccess = "Sign In Success"))
                }
            }
        }
    }

    private val _loginUiState = MutableStateFlow(LoginDetails())
    val loginUiState = _loginUiState.asStateFlow()

    private val _currentUserStatus = Channel<CurrentUser>()
    val currentUserStatus = _currentUserStatus.receiveAsFlow()

    fun updateUiState(loginDetails: LoginDetails) {
        _loginUiState.value = loginDetails
    }

    init {
        getUserDetails()
    }

    fun getUserDetails() =
        viewModelScope.launch {

            repository.getCurrentUser().collect{ result ->
                when(result) {
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
