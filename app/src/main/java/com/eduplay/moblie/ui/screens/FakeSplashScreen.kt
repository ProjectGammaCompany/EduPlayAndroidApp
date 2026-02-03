package com.eduplay.moblie.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.eduplay.moblie.R
import com.eduplay.moblie.ui.elements.AuthScreenNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

@Composable
fun FakeSplashScreen(isLoading: StateFlow<Boolean>, isAuthorised: StateFlow<Boolean>, navController: NavController) {

    if (!isLoading.collectAsState().value) {
         if (!isAuthorised.collectAsState().value) {
                AuthScreenNavigator(navController)
            } else {
                navController.navigate("main_screen") {
                    navController.currentBackStackEntry?.destination?.route?.let {
                        popUpTo(
                            it
                        ) { inclusive = true }
                    }
                }
            }
    }


    Box(
        modifier = Modifier.fillMaxSize()
            .background(colorResource(R.color.eduplay_launcher_background))
    )
    {

        Image(
            painter = painterResource(
                R.drawable.eduplay_logo_white
            ),
            contentScale = ContentScale.Fit,
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier
                .align(Alignment.Center)
                .width(142.dp)
       )
    }
}

//@Preview
//@Composable
//private fun FakeSplashPreview() {
//        FakeSplashScreen(
//            remember { mutableStateOf(true) },
//            remember { mutableStateOf(false) },
//            rememberNavController()
//        )
//}