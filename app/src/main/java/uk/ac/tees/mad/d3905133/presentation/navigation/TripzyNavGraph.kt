package uk.ac.tees.mad.d3905133.presentation.navigation

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.tees.mad.d3905133.constants.bottomNavigationItems
import uk.ac.tees.mad.d3905133.presentation.account.AccountDestination
import uk.ac.tees.mad.d3905133.presentation.account.AccountScreen
import uk.ac.tees.mad.d3905133.presentation.auth.AskDetailDestination
import uk.ac.tees.mad.d3905133.presentation.auth.AskDetailScreen
import uk.ac.tees.mad.d3905133.presentation.auth.AskLoginDestination
import uk.ac.tees.mad.d3905133.presentation.auth.AskLoginScreen
import uk.ac.tees.mad.d3905133.presentation.auth.ConfirmEmailDestination
import uk.ac.tees.mad.d3905133.presentation.auth.ConfirmEmailScreen
import uk.ac.tees.mad.d3905133.presentation.auth.ForgetPasswordDestination
import uk.ac.tees.mad.d3905133.presentation.auth.ForgotPasswordScreen
import uk.ac.tees.mad.d3905133.presentation.auth.GoogleAuthUiClient
import uk.ac.tees.mad.d3905133.presentation.auth.LoginDestination
import uk.ac.tees.mad.d3905133.presentation.auth.LoginScreen
import uk.ac.tees.mad.d3905133.presentation.auth.LoginViewModel
import uk.ac.tees.mad.d3905133.presentation.auth.RegisterDestination
import uk.ac.tees.mad.d3905133.presentation.auth.RegisterScreen
import uk.ac.tees.mad.d3905133.presentation.home.HomeDestination
import uk.ac.tees.mad.d3905133.presentation.home.HomeScreen
import uk.ac.tees.mad.d3905133.presentation.placeDetail.PlaceDetailDestination
import uk.ac.tees.mad.d3905133.presentation.placeDetail.PlaceDetailScreen
import uk.ac.tees.mad.d3905133.presentation.search.SearchDestination
import uk.ac.tees.mad.d3905133.presentation.search.SearchScreen
import uk.ac.tees.mad.d3905133.ui.SplashDestination
import uk.ac.tees.mad.d3905133.ui.SplashScreen

@Composable
fun TripzyNavHost(
    navController: NavHostController,
    context: Context
) {
    val scope = rememberCoroutineScope()
    val firebase = FirebaseAuth.getInstance()
    val user = firebase.currentUser

    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }
    var nav by remember {
        mutableStateOf("home")
    }
    val startDestination =
        if (user != null) {
            HomeDestination.route
        } else {
            "auth_nav"
        }

    NavHost(navController = navController, startDestination = "bottom_nav") {

        navigation(startDestination = SplashDestination.route, route = "bottom_nav") {
            composable(SplashDestination.route) {
                SplashScreen(
                    onComplete = {
                        scope.launch(Dispatchers.Main) {
                            navController.popBackStack()
                            navController.navigate(startDestination)
                        }
                    }
                )
            }
            composable(route = HomeDestination.route) {
                HomeScreen(
                    navController = navController,
                    onSearchClick = {
                        val f = true
                        navController.navigate("${SearchDestination.route}/$f") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onBottomItemClick = {
                        nav = bottomNavigationItems[it].route
                        val f = false
                        if (bottomNavigationItems[it] == BottomNavigationScreens.Search) {
                            nav = "${nav}/$f"
                        }
                        navController.navigate(nav) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onItemClick = {
                        navController.navigate("${PlaceDetailDestination.route}/$it")
                    }
                )
            }
            composable(
                route = SearchDestination.routeWithArgs,
                arguments = listOf(navArgument(SearchDestination.isFocusedArg) {
                    type = NavType.BoolType
                })
            ) {
                SearchScreen(
                    navController = navController,
                    onBottomItemClick = {
                        nav = bottomNavigationItems[it].route
                        val f = false
                        if (bottomNavigationItems[it] == BottomNavigationScreens.Search) {
                            nav = "${nav}/$f"
                        }
                        navController.navigate(nav) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onCardClick = {
                        Log.d("Route", it.toString())
                        navController.navigate("${PlaceDetailDestination.route}/$it")
                    }
                )
            }
            composable(route = AccountDestination.route) {
                AccountScreen(
                    navController = navController,
                    onBottomItemClick = {
                        nav = bottomNavigationItems[it].route
                        val f = false
                        if (bottomNavigationItems[it] == BottomNavigationScreens.Search) {
                            nav = "${nav}/$f"
                        }
                        navController.navigate(nav) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    currentUser = googleAuthUiClient.getSignedInUser(),
                    onSignOut = {
                        scope.launch {
                            googleAuthUiClient.signOut()
                        }
                        navController.navigate(AskLoginDestination.route)
                        Toast.makeText(
                            context,
                            "User Signed out",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }

            composable(
                route = PlaceDetailDestination.routeWithArgs,
                arguments = listOf(navArgument(PlaceDetailDestination.placeIdArg) {
                    type = NavType.IntType
                })
            ) {
                PlaceDetailScreen(
                    onNavigateUp = {
                        navController.popBackStack()
                    }
                )
            }
        }

        //AuthNavigation
        navigation(startDestination = AskLoginDestination.route, route = "auth_nav") {
            composable(route = AskLoginDestination.route) {
                val viewModel: LoginViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                val currentUserStatus = viewModel.currentUserStatus.collectAsState(initial = null)
                var isGoogleSigned by remember {
                    mutableStateOf(false)
                }
                LaunchedEffect(key1 = state.isSignInSuccessful) {
                    if (state.isSignInSuccessful) {
                        Toast.makeText(
                            context,
                            "Sign in successful",
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.resetState()
                        viewModel.getUserDetails()
                        isGoogleSigned = true
                    }
                }

                LaunchedEffect(currentUserStatus.value?.isSuccess) {
                    if (currentUserStatus.value?.isSuccess != null && isGoogleSigned) {
                        navController.navigate(HomeDestination.route)
                        Log.d("USER", currentUserStatus.value!!.isSuccess.toString())
                    }
                }

                LaunchedEffect(currentUserStatus.value?.isError) {
                    if (currentUserStatus.value?.isError != null && isGoogleSigned ) {
                        val currentUser = googleAuthUiClient.getSignedInUser()
                        if (currentUser != null) {
                            navController.navigate(AskDetailDestination.route)
                        }
                    }
                }

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                    onResult = { result ->
                        if (result.resultCode == Activity.RESULT_OK) {
                            scope.launch {
                                val signInResult = googleAuthUiClient.signInWithIntent(
                                    intent = result.data ?: return@launch
                                )
                                viewModel.onSignInWithGoogleResult(signInResult)
                            }
                        }
                    }
                )


                AskLoginScreen(
                    loginWithEmail = { navController.navigate(LoginDestination.route) },
                    loginWithGoogle = {
                        scope.launch {
                            val signInIntentSender = googleAuthUiClient.signIn()
                            launcher.launch(
                                IntentSenderRequest.Builder(
                                    signInIntentSender ?: return@launch
                                ).build()
                            )

                        }
                    },
                    onSkip = { }
                )
            }
            composable(route = LoginDestination.route) {
                LoginScreen(
                    signInSuccess = { navController.navigate(HomeDestination.route) },
                    onNavigateUp = { navController.popBackStack() },
                    onRegisterButtonClicked = { navController.navigate(RegisterDestination.route) },
                    onForgotPasswordButtonClicked = {
                        navController.navigate(
                            ForgetPasswordDestination.route
                        )
                    }
                )
            }
            composable(route = RegisterDestination.route) {
                RegisterScreen(
                    registerSuccess = {
                        navController.navigate(ConfirmEmailDestination.route)
                    },
                    onLogin = { navController.popBackStack() },
                    onNavigateUp = { navController.popBackStack() })
            }

            composable(route = ForgetPasswordDestination.route) {
                ForgotPasswordScreen(
                    onNavigateUp = {
                        navController.popBackStack()
                    }
                )
            }

            composable(route = ConfirmEmailDestination.route) {
                ConfirmEmailScreen(
                    onNavigateUp = { navController.popBackStack() },
                    onEmailVerify = { navController.navigate(AskDetailDestination.route) }
                )
            }
            composable(route = AskDetailDestination.route) {
                AskDetailScreen(
                    navigateToHome = {
                        navController.navigate(HomeDestination.route)
                        Toast.makeText(context, "Details saved", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }
}