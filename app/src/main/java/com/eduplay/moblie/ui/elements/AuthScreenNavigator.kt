package com.eduplay.moblie.ui.elements

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun AuthScreenNavigator(navController: NavController) {
    navController.navigate("auth_screen") {
        navController.currentBackStackEntry?.destination?.route?.let {
            popUpTo(
                it
            ) { inclusive = true }
        }
    }
}