package com.example.mcmovilcare.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Teal80, secondary = TealGrey80, tertiary = Azul80,
    primaryContainer = Color(0xFF004F50), onPrimaryContainer = Color(0xFF9CF1F2),
    secondaryContainer = Color(0xFF324B4B), onSecondaryContainer = Color(0xFFCCE8E8)
)

private val LightColorScheme = lightColorScheme(
    primary = Teal40, secondary = TealGrey40, tertiary = Azul40,
    primaryContainer = Color(0xFF9CF1F2), onPrimaryContainer = Color(0xFF002020),
    secondaryContainer = Color(0xFFCCE8E8), onSecondaryContainer = Color(0xFF051F20)
)

@Composable
fun MCmovilCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}