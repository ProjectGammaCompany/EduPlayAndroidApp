package com.eduplay.moblie.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.eduplay.moblie.ui.viewmodel.AppThemeViewModel
import com.eduplay.moblie.ui.viewmodel.ThemeViewModel
import com.eduplay.moblie.useCases.managers.AppSettingsManager

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
    secondaryContainer = LightBlue500
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
    primaryContainer = LightBlue500,
    onPrimaryContainer = Blue500,
    onPrimaryFixed = Color.White,
    outline = LightBlue100
)

@Composable
fun EduPlayTheme(
    appThemeViewModel: ThemeViewModel = hiltViewModel<AppThemeViewModel>(),
    content: @Composable () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()
    val theme = appThemeViewModel.getTheme().collectAsState(AppSettingsManager.Themes.SYSTEM)
    val colorScheme = when (theme.value) {
        AppSettingsManager.Themes.SYSTEM -> if (darkTheme) DarkColorScheme else LightColorScheme
        AppSettingsManager.Themes.LIGHT -> LightColorScheme
        AppSettingsManager.Themes.DARK -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}