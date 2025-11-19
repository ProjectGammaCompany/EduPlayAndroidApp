package com.eduplay.moblie

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eduplay.moblie.ui.screens.AuthorizationScreen
import com.eduplay.moblie.ui.theme.EduPlayTheme
import dagger.hilt.android.AndroidEntryPoint
import com.eduplay.moblie.ui.viewmodel.AuthViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EduPlayTheme {

                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "auth_screen") {
                    composable("auth_screen") {
                        AuthorizationScreen(
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}