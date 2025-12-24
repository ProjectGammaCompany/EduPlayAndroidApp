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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eduplay.moblie.ui.elements.BottomNavBar
import com.eduplay.moblie.ui.screens.AuthorizationScreen
import com.eduplay.moblie.ui.screens.EventScreen
import com.eduplay.moblie.ui.theme.EduPlayTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EduPlayTheme {
                val context = LocalContext.current
                (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavBar(navController) }
                ) { innerPadding ->

                    NavHost(navController = navController, startDestination = "main_screen") {
                        composable("auth_screen") {
                            AuthorizationScreen(
                                navController = navController
                            )
                        }
                        composable("main_screen") {
                            EventScreen(innerPadding)
                        }
                    }

                }
            }
        }
    }
}

//@Preview
//@Composable
//fun funny() {
//    val navController = rememberNavController()
//    Scaffold(
//        bottomBar = { BottomNavBar(navController) }
//    ) { innerPadding ->
//        Box(modifier = Modifier.padding(innerPadding)) {
//            MainScreen()
//        }
//    }
//}
