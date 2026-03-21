package com.eduplay.moblie.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Blue200,
    secondary = Purpule10,
    tertiary = Purpul20,
    background = Blue20,
    surface = Blue20,
    onPrimary = LightBlue500,
    onSecondary = LightBlue500,
    onTertiary = LightBlue500,
    primaryContainer = LightBlue200,
    onBackground = LightBlue500,
    secondaryContainer = Purpul20
)

private val LightColorScheme = lightColorScheme(
    primary = Blue500,
    secondary = Purpule400,
    tertiary = Purpule200,
    background = Color.White,
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    primaryContainer = LightBlue500

)

@Composable
fun EduPlayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}