package uk.ac.tees.mad.d3905133.domain

data class UserResponse(
    val item: CurrentUser?,
    val key: String?
) {
    data class CurrentUser(
        val name: String = "",
        val email: String = "",
        val address: String = "",
        val profileImage: String = ""
    )
}