package com.eduplay.moblie

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eduplay.moblie.models.AnswerOption
import com.eduplay.moblie.models.TaskType
import com.eduplay.moblie.repository.responseTypes.Block
import com.eduplay.moblie.repository.responseTypes.StageType
import com.eduplay.moblie.repository.responseTypes.Task
import com.eduplay.moblie.repository.responseTypes.TaskAnswerStatus
import com.eduplay.moblie.ui.elements.BottomNavBar
import com.eduplay.moblie.ui.screens.AuthorizationScreen
import com.eduplay.moblie.ui.screens.EventResultScreen
import com.eduplay.moblie.ui.screens.EventScreen
import com.eduplay.moblie.ui.screens.EventStageScreen
import com.eduplay.moblie.ui.screens.FakeSplashScreen
import com.eduplay.moblie.ui.screens.MainScreen
import com.eduplay.moblie.ui.screens.MyEventsScreen
import com.eduplay.moblie.ui.screens.ProfileScreen
import com.eduplay.moblie.ui.screens.TaskScreen.TaskScreen
import com.eduplay.moblie.ui.theme.EduPlayTheme
import com.eduplay.moblie.ui.viewmodel.EventStageViewModelInterface
import com.eduplay.moblie.ui.viewmodel.EventStageViewmodel
import com.eduplay.moblie.ui.viewmodel.SplashViewModel
import com.eduplay.moblie.useCases.FileDownloadStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val hideBottomBarScreens = listOf("auth_screen", "play_event", "fake_splash")
    private val viewModel: SplashViewModel by viewModels()
    private val fakeStage: EventStageViewmodel by viewModels()

    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        val startDestination = mutableStateOf("main_screen")
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition{viewModel.isLoading.value}
//        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
//
//            splashScreenViewProvider.remove()
//
//        }


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

                    NavHost(navController = navController, startDestination = "fake_splash") {
                        composable("fake_splash") {
                            FakeSplashScreen(
                                viewModel.isLoading,
                                viewModel.isAuthorised,
                                navController
                            )
                        }
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
                                pathArgs.arguments?.getString("eventId") ?: "",
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
