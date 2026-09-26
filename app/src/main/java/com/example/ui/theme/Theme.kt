package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.model.AppThemeMode

private val BlueColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = BlueOnPrimary,
    primaryContainer = BluePrimaryContainer,
    onPrimaryContainer = BlueOnPrimaryContainer,
    secondary = BlueSecondary,
    onSecondary = Color.White,
    background = BlueBackground,
    surface = BlueSurface,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    outline = Color(0xFFCBD5E1)
)

private val PurpleColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = PurpleOnPrimary,
    primaryContainer = PurplePrimaryContainer,
    onPrimaryContainer = PurpleOnPrimaryContainer,
    secondary = PurpleSecondary,
    onSecondary = Color.White,
    background = PurpleBackground,
    surface = PurpleSurface,
    onBackground = Color(0xFF1E1B4B),
    onSurface = Color(0xFF1E1B4B),
    surfaceVariant = Color(0xFFF5F3FF),
    outline = Color(0xFFDDD6FE)
)

private val GreenColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = GreenOnPrimary,
    primaryContainer = GreenPrimaryContainer,
    onPrimaryContainer = GreenOnPrimaryContainer,
    secondary = GreenSecondary,
    onSecondary = Color.White,
    background = GreenBackground,
    surface = GreenSurface,
    onBackground = Color(0xFF064E3B),
    onSurface = Color(0xFF064E3B),
    surfaceVariant = Color(0xFFECFDF5),
    outline = Color(0xFFA7F3D0)
)

private val OrangeColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = OrangeOnPrimary,
    primaryContainer = OrangePrimaryContainer,
    onPrimaryContainer = OrangeOnPrimaryContainer,
    secondary = OrangeSecondary,
    onSecondary = Color.White,
    background = OrangeBackground,
    surface = OrangeSurface,
    onBackground = Color(0xFF431407),
    onSurface = Color(0xFF431407),
    surfaceVariant = Color(0xFFFFF7ED),
    outline = Color(0xFFFED7AA)
)

private val DarkNavyColorScheme = darkColorScheme(
    primary = DarkNavyPrimary,
    onPrimary = DarkNavyOnPrimary,
    primaryContainer = DarkNavyPrimaryContainer,
    onPrimaryContainer = DarkNavyOnPrimaryContainer,
    secondary = DarkNavySecondary,
    onSecondary = Color.White,
    background = DarkNavyBackground,
    surface = DarkNavySurface,
    surfaceVariant = DarkNavySurfaceVariant,
    onBackground = DarkNavyOnSurface,
    onSurface = DarkNavyOnSurface,
    outline = Color(0xFF334155)
)

private val DarkModeColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = Color.White,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface,
    outline = Color(0xFF475569)
)

@Composable
fun StudyMateTheme(
    themeMode: AppThemeMode = AppThemeMode.BLUE,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = when (themeMode) {
        AppThemeMode.BLUE -> if (darkTheme) DarkNavyColorScheme else BlueColorScheme
        AppThemeMode.PURPLE -> PurpleColorScheme
        AppThemeMode.GREEN -> GreenColorScheme
        AppThemeMode.ORANGE -> OrangeColorScheme
        AppThemeMode.DARK_NAVY -> DarkNavyColorScheme
        AppThemeMode.SYSTEM_DARK -> DarkModeColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
