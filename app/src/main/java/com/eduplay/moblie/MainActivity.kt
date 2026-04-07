package com.eduplay.moblie

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eduplay.moblie.services.EventDownloadService
import com.eduplay.moblie.ui.elements.BluetoothPermissionElement
import com.eduplay.moblie.ui.elements.BottomNavBar
import com.eduplay.moblie.ui.screens.AuthorizationScreen
import com.eduplay.moblie.ui.screens.EventResultScreen
import com.eduplay.moblie.ui.screens.EventScreen
import com.eduplay.moblie.ui.screens.EventStageScreen
import com.eduplay.moblie.ui.screens.FakeSplashScreen
import com.eduplay.moblie.ui.screens.MainScreen
import com.eduplay.moblie.ui.screens.MyEventsScreen
import com.eduplay.moblie.ui.screens.NotificationScreen
import com.eduplay.moblie.ui.screens.ProfileScreen
import com.eduplay.moblie.ui.screens.SearchScreen
import com.eduplay.moblie.ui.theme.EduPlayTheme
import com.eduplay.moblie.ui.viewmodel.BluetoothViewModel
import com.eduplay.moblie.ui.viewmodel.SplashViewModel
import com.eduplay.moblie.ui.viewmodel.factories.BluetoothViewModelFactory
import com.eduplay.moblie.useCases.AppSettingsManager
import com.eduplay.moblie.useCases.BluetoothDataExchangeUseCase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val hideBottomBarScreens = listOf("auth_screen", "play_event", "fake_splash")
    private val viewModel: SplashViewModel by viewModels()

    private val startDownloadService = {
        startService(Intent(this, EventDownloadService::class.java))
    }

    private val adapter = mutableStateOf<BluetoothAdapter?>(null)
    private val manager = mutableStateOf<BluetoothManager?>(null)

    private val bluetoothViewModel: BluetoothViewModel by viewModels {
        BluetoothViewModelFactory(
            adapter as State<BluetoothAdapter?>,
            BluetoothDataExchangeUseCase()
        )
    }

    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        val updateAdapter = { adapter: BluetoothAdapter? -> this.adapter.value = adapter }
        val updateManger = { manager: BluetoothManager? -> this.manager.value = manager }
        val splashScreen = installSplashScreen()
        val isCompetitionMode = mutableStateOf(false)
        val toggleCompetitionMode = { mode: Boolean -> isCompetitionMode.value = mode }
        val onStopCompetition = {
            isCompetitionMode.value = false
            bluetoothViewModel.stopScan(this)
            // bluetoothViewModel.stopAllSocketConnections()
        }
        super.onCreate(savedInstanceState)


        splashScreen.setKeepOnScreenCondition { viewModel.isLoading.value }


        enableEdgeToEdge()
        setContent {
            EduPlayTheme {
                val context = LocalContext.current
                (context as? Activity)?.requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_LOCKED
                val navController = rememberNavController()

                BluetoothPermissionElement(bluetoothViewModel.askForPermissions)

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
                            MainScreen(
                                innerPadding,
                                navController,
                                isCompetitionMode = isCompetitionMode,
                                onStopCompetition = onStopCompetition
                            )
                        }
                        composable(
                            "event_screen/{eventId}",
                            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
                        ) { pathArgs ->
                            EventScreen(
                                innerPadding,
                                pathArgs.arguments?.getString("eventId") ?: "",
                                navController,
                                manager,
                                adapter,
                                updateManger = updateManger,
                                updateAdapter = updateAdapter,
                                isCompetitionMode = isCompetitionMode,
                                toggleCompetitionMode = toggleCompetitionMode,
                                bluetoothViewModel = bluetoothViewModel,
                                onDownloadEvent = startDownloadService
                            )
                        }

                        composable("my_events") {
                            MyEventsScreen(innerPadding, navController)
                        }

                        composable("profile") {
                            ProfileScreen(innerPadding, navController, contentResolver)
                        }
                        composable("search") {
                            SearchScreen(navController, innerPadding)
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
                                navController,
                                bluetoothViewModel = bluetoothViewModel,
                                isCompetitionMode = isCompetitionMode
                            )
                        }

                        composable("notifications") {
                            NotificationScreen(innerPadding, navController)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothViewModel.stopScan(this)
        bluetoothViewModel.stopAllSocketConnections()
    }
}
