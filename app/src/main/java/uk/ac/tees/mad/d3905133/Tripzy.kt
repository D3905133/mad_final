package uk.ac.tees.mad.d3905133

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.d3905133.presentation.navigation.TripzyNavHost

@Composable
fun Tripzy(navController: NavHostController = rememberNavController()) {
        TripzyNavHost(
            navController = navController,
            context = LocalContext.current
        )

}

