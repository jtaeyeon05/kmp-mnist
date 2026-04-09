package io.github.jtaeyeon05.kmp_mnist.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import kmp_mnist.composeapp.generated.resources.Res
import kmp_mnist.composeapp.generated.resources.Mona12
import kmp_mnist.composeapp.generated.resources.Mona12_Bold
import org.jetbrains.compose.resources.Font


val Mona12: FontFamily
    @Composable
    get () {
        val regular = Font(
            resource = Res.font.Mona12,
            weight = FontWeight.Normal,
            style = FontStyle.Normal,
        )
        val bold = Font(
            resource = Res.font.Mona12_Bold,
            weight = FontWeight.Bold,
            style = FontStyle.Normal,
        )
        return FontFamily(regular, bold)
    }

private val AppTypography: Typography
    @Composable
    get() = Typography().let { baseline ->
        Typography(
            displayLarge = baseline.displayLarge.copy(fontFamily = Mona12),
            displayMedium = baseline.displayMedium.copy(fontFamily = Mona12),
            displaySmall = baseline.displaySmall.copy(fontFamily = Mona12),
            headlineLarge = baseline.headlineLarge.copy(fontFamily = Mona12),
            headlineMedium = baseline.headlineMedium.copy(fontFamily = Mona12),
            headlineSmall = baseline.headlineSmall.copy(fontFamily = Mona12),
            titleLarge = baseline.titleLarge.copy(fontFamily = Mona12),
            titleMedium = baseline.titleMedium.copy(fontFamily = Mona12),
            titleSmall = baseline.titleSmall.copy(fontFamily = Mona12),
            bodyLarge = baseline.bodyLarge.copy(fontFamily = Mona12),
            bodyMedium = baseline.bodyMedium.copy(fontFamily = Mona12),
            bodySmall = baseline.bodySmall.copy(fontFamily = Mona12),
            labelLarge = baseline.labelLarge.copy(fontFamily = Mona12),
            labelMedium = baseline.labelMedium.copy(fontFamily = Mona12),
            labelSmall = baseline.labelSmall.copy(fontFamily = Mona12),
        )
    }


object Colors {
    val primary = Color(0xFFFF0000)
    val onPrimary = Color(0xFF000000)
    val primaryContainer = Color(0xFF800000)
    val onPrimaryContainer = Color(0xFFFFFFFF)

    val background = Color(0xFF000000)
    val onBackground = Color(0xFFFFFFFF)

    val surface = Color(0xFF121212)
    val onSurface = Color(0xFFE6E6E6)
    val surfaceVariant = Color(0xFF2C2C2C)
    val onSurfaceVariant = Color(0xFFC4C4C4)
    val surfaceTint = Color(0xFFFFFFFF)
    val inverseSurface = Color(0xFFE6E6E6)
    val inverseOnSurface = Color(0xFF000000)

    val surfaceBright = Color(0xFF242424)
    val surfaceDim = Color(0xFF121212)
    val surfaceContainer = Color(0xFF1E1E1E)
    val surfaceContainerHigh = Color(0xFF2A2A2A)
    val surfaceContainerHighest = Color(0xFF363636)
    val surfaceContainerLow = Color(0xFF1A1A1A)
    val surfaceContainerLowest = Color(0xFF0D0D0D)

    val outline = Color(0xFF8E8E8E)
    val outlineVariant = Color(0xFF444444)
    val scrim = Color(0xFF000000)

    val transparent = Color.Transparent
}

private val colorScheme = ColorScheme(
    primary = Colors.primary,
    onPrimary = Colors.onPrimary,
    primaryContainer = Colors.primaryContainer,
    onPrimaryContainer = Colors.onPrimaryContainer,
    inversePrimary = Colors.transparent,
    primaryFixed = Colors.primary,
    primaryFixedDim = Colors.primaryContainer,
    onPrimaryFixed = Colors.onPrimary,
    onPrimaryFixedVariant = Colors.onPrimaryContainer,

    secondary = Colors.transparent,
    onSecondary = Colors.transparent,
    secondaryContainer = Colors.transparent,
    onSecondaryContainer = Colors.transparent,
    secondaryFixed = Colors.transparent,
    secondaryFixedDim = Colors.transparent,
    onSecondaryFixed = Colors.transparent,
    onSecondaryFixedVariant = Colors.transparent,

    tertiary = Colors.transparent,
    onTertiary = Colors.transparent,
    tertiaryContainer = Colors.transparent,
    onTertiaryContainer = Colors.transparent,
    tertiaryFixed = Colors.transparent,
    tertiaryFixedDim = Colors.transparent,
    onTertiaryFixed = Colors.transparent,
    onTertiaryFixedVariant = Colors.transparent,

    error = Colors.transparent,
    onError = Colors.transparent,
    errorContainer = Colors.transparent,
    onErrorContainer = Colors.transparent,

    background = Colors.background,
    onBackground = Colors.onBackground,

    surface = Colors.surface,
    onSurface = Colors.onSurface,
    surfaceVariant = Colors.surfaceVariant,
    onSurfaceVariant = Colors.onSurfaceVariant,
    surfaceTint = Colors.surfaceTint,
    inverseSurface = Colors.inverseSurface,
    inverseOnSurface = Colors.inverseOnSurface,
    surfaceBright = Colors.surfaceBright,
    surfaceDim = Colors.surfaceDim,
    surfaceContainer = Colors.surfaceContainer,
    surfaceContainerHigh = Colors.surfaceContainerHigh,
    surfaceContainerHighest = Colors.surfaceContainerHighest,
    surfaceContainerLow = Colors.surfaceContainerLow,
    surfaceContainerLowest = Colors.surfaceContainerLowest,

    outline = Colors.outline,
    outlineVariant = Colors.outlineVariant,
    scrim = Colors.scrim,
)


@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
