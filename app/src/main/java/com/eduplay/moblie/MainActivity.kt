package com.eduplay.moblie

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eduplay.moblie.ui.elements.BottomNavBar
import com.eduplay.moblie.ui.screens.AuthorizationScreen
import com.eduplay.moblie.ui.screens.EventResultScreen
import com.eduplay.moblie.ui.screens.EventScreen
import com.eduplay.moblie.ui.screens.EventStageScreen
import com.eduplay.moblie.ui.screens.MainScreen
import com.eduplay.moblie.ui.screens.MyEventsScreen
import com.eduplay.moblie.ui.screens.ProfileScreen
import com.eduplay.moblie.ui.theme.EduPlayTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val hideBottomBarScreens = listOf("auth_screen", "play_event")

    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EduPlayTheme {
                val context = LocalContext.current
                (context as? Activity)?.requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_LOCKED
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavBar(navController, hideBottomBarScreens) }
                ) { innerPadding ->

                    NavHost(navController = navController, startDestination = "auth_screen") {
                        composable("auth_screen") {
                            AuthorizationScreen(
                                navController = navController
                            )
                        }
                        composable("main_screen") {
                            MainScreen(innerPadding, navController)
                        }
                        composable(
                            "event_screen/{eventId}",
                            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
                        ) { pathArgs ->
                            EventScreen(
                                innerPadding,
                                pathArgs.arguments?.getString("userId") ?: "",
                                navController
                            )
                        }

                        composable("my_events") {
                            MyEventsScreen(innerPadding, navController)
                        }

                        composable("profile") {
                            ProfileScreen(innerPadding, navController)
                        }
                        composable(
                            "event_result/{eventId}",
                            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
                        ) { pathArgs ->
                            EventResultScreen(
                                innerPadding,
                                pathArgs.arguments?.getString("eventId") ?: "",
                                navController
                            )
                        }

                        composable(
                            "play_event/{eventId}",
                            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
                        ) { pathArgs ->
                            EventStageScreen(
                                pathArgs.arguments?.getString("eventId") ?: "",
                                innerPadding,
                                navController
                            )
                        }
                    }

                }
            }
        }
    }
}
