package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = YolkDarkPrimary,
  onPrimary = YolkTextPrimaryLight,
  primaryContainer = YolkDarkSurfaceVariant,
  onPrimaryContainer = YolkAmber,
  secondary = YolkAmber,
  onSecondary = YolkTextPrimaryLight,
  background = YolkDarkBackground,
  onBackground = YolkDarkTextPrimary,
  surface = YolkDarkSurface,
  onSurface = YolkDarkTextPrimary,
  surfaceVariant = YolkDarkSurfaceVariant,
  onSurfaceVariant = YolkDarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
  primary = YolkYellowPrimary,
  onPrimary = YolkWhite,
  primaryContainer = YolkYellowLight,
  onPrimaryContainer = YolkYellowDark,
  secondary = YolkOrange,
  onSecondary = YolkWhite,
  background = YolkEggshell,
  onBackground = YolkTextPrimaryLight,
  surface = YolkSurfaceLight,
  onSurface = YolkTextPrimaryLight,
  surfaceVariant = YolkSurfaceVariantLight,
  onSurfaceVariant = YolkTextSecondaryLight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Preserve yolk branding for consistent identity
  content: @Composable () -> Unit,
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
