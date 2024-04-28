package uk.ac.tees.mad.d3905133.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import uk.ac.tees.mad.d3905133.presentation.search.SearchDestination
import uk.ac.tees.mad.d3905133.presentation.account.AccountDestination
import uk.ac.tees.mad.d3905133.presentation.auth.ForgetPasswordDestination
import uk.ac.tees.mad.d3905133.presentation.auth.LoginDestination
import uk.ac.tees.mad.d3905133.presentation.auth.RegisterDestination
import uk.ac.tees.mad.d3905133.presentation.home.HomeDestination

sealed class BottomNavigationScreens(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Explore: BottomNavigationScreens(route = HomeDestination.route, resourceId = HomeDestination.titleRes, icon = Icons.Filled.Home)
    object Search: BottomNavigationScreens(route = SearchDestination.route, resourceId = SearchDestination.titleRes, icon =Icons.Filled.Search)

    object Account: BottomNavigationScreens(route = AccountDestination.route, resourceId = AccountDestination.titleRes, icon = Icons.Filled.AccountCircle)
}

sealed class AuthNavigationScreens(val route: String, @StringRes val resourceId: Int) {
    object Login: AuthNavigationScreens(route = LoginDestination.route, resourceId = LoginDestination.titleRes)
    object Register: AuthNavigationScreens(route = RegisterDestination.route, resourceId = RegisterDestination.titleRes)
    object ForgetPassword: AuthNavigationScreens(route = ForgetPasswordDestination.route, resourceId = ForgetPasswordDestination.titleRes)
}

