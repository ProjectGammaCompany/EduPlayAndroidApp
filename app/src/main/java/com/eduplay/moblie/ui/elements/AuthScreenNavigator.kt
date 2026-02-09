package com.eduplay.moblie.ui.elements

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun AuthScreenNavigator(navController: NavController) {
    navController.navigate("auth_screen") {
        Log.d("AUTH_NAVIGATION", navController.currentBackStackEntry?.destination?.route ?: "no route")
        navController.currentBackStackEntry?.destination?.route?.let {
            popUpTo(
                it
            ) { inclusive = true }
        }
    }
}