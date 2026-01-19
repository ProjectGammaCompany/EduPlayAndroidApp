package com.eduplay.moblie.ui.elements

import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.eduplay.moblie.R

private enum class Destination(
    val route: String,
    val icon: Int,
    val selectedIcon: Int,
    val contentDescription: Int
) {
    MAIN("main_screen", R.drawable.search, R.drawable.search, R.string.main_screen),
    MY_QUESTS(
        "my_events",
        R.drawable.stars_circle,
        R.drawable.stars_circle_filled,
        R.string.my_events
    ),
    PROFILE("profile", R.drawable.account, R.drawable.account_filled, R.string.profile)
}

@Composable
fun BottomNavBar(
    navController: NavController,
    screensToHide: List<String>
) {
    val startDestination = Destination.MAIN
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
//    Log.d("SCREEN_BOTTOM", navController.currentDestination?.route.toString())
//    Log.d("SCREEN_BOTTOM", navController.currentBackStackEntry?.toRoute() ?: "")
//    Log.d("SCREEN_BOTTOM", navController.currentBackStackEntryAsState().value?.destination?.route ?: "")
    if (
        screensToHide.none { screenName ->
            navController.currentBackStackEntryAsState().value?.destination?.route?.startsWith(screenName) ?: false
        }
    ) {
        Log.d("SCREEN_BOTTOM", navController.currentDestination?.route ?: "")

        NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
            Destination.entries.forEachIndexed { index, destination ->
                NavigationBarItem(
                    selected = selectedDestination == index,
                    onClick = {
                        navController.navigate(route = destination.route)
                        selectedDestination = index
                    },
                    icon = {
                        Icon(
                            ImageVector.vectorResource(
                                if (destination.ordinal == selectedDestination)
                                    destination.selectedIcon
                                else
                                    destination.icon
                            ),
                            contentDescription = stringResource(destination.contentDescription)
                        )
                    },
                    label = { Text(stringResource(destination.contentDescription)) }
                )
            }
        }
    }
}