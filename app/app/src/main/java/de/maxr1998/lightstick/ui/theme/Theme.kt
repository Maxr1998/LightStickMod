package de.maxr1998.lightstick.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColors(
    primary = Gold,
    secondary = Gold,
    secondaryVariant = Gold,
)

@Composable
fun LightstickControllerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColorScheme,
        typography = Typography,
        content = content,
    )
}