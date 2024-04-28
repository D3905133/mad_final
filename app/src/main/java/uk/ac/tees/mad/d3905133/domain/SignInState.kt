package uk.ac.tees.mad.d3905133.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class SignInState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)

data class RecommendedPlace(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)

data class SignInState1(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)

@Serializable
data class LoginDetails(
    val login: String = "",
    val password: String = ""
)

@Serializable
data class LoginResponse(
    @SerialName("user-token")
    val userToken: String? = "",
    @SerialName("userStatus")
    val userStatus: String? = "",
    val email: String? = "",
    val message: String? = ""
)