package com.eduplay.moblie.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ColorScheme.success: Color
    @Composable
    get() = if (isSystemInDarkTheme()) green600 else green300

val ColorScheme.warning: Color
    @Composable
    get() = if (isSystemInDarkTheme()) orange600 else orange300

val ColorScheme.danger: Color
    @Composable
    get() = if (isSystemInDarkTheme()) red600 else red300