package io.github.jtaeyeon05.kmp_mnist.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.times


@Immutable
class LayoutConstraints private constructor(
    val screen: Screen,
    val padding: Padding,
    val border: Border,
    val typography: Typography,
    val component: Component,
) {
    data class Screen(
        val base: Dp,
        val width: Dp,
        val height: Dp,
        val isVertical: Boolean,
        val isHorizontal: Boolean,
    )

    data class Padding(
        private val small: Dp,
        private val medium: Dp,
        private val large: Dp,

        private val innerSmall: Dp,
        private val innerMedium: Dp,
        private val innerLarge: Dp,
    ) {
        operator fun invoke(scale: Scale) = when (scale) {
            Scale.SMALL -> small
            Scale.MEDIUM -> medium
            Scale.LARGE -> large
        }

        fun inner(scale: Scale) = when (scale) {
            Scale.SMALL -> innerSmall
            Scale.MEDIUM -> innerMedium
            Scale.LARGE -> innerLarge
        }
    }

    data class Border(
        private val small: Dp,
        private val medium: Dp,
        private val large: Dp,
    ) {
        operator fun invoke(scale: Scale) = when (scale) {
            Scale.SMALL -> small
            Scale.MEDIUM -> medium
            Scale.LARGE -> large
        }
    }

    data class Typography(
        val lineHeight: Float,
        private val small: TypographySize,
        private val medium: TypographySize,
        private val large: TypographySize,
    ) {
        operator fun invoke(scale: Scale) = when (scale) {
            Scale.SMALL -> small
            Scale.MEDIUM -> medium
            Scale.LARGE -> large
        }
    }

    data class Component(
        val cellBoard: Dp,
        val predictItem: Dp,
        val predictBoard: DpSize,
        val dialog: DpSize,

        private val smallHeight: Dp,
        private val mediumHeight: Dp,
        private val largeHeight: Dp,

        private val smallIcon: Dp,
        private val mediumIcon: Dp,
        private val largeIcon: Dp,
    ) {
        fun height(scale: Scale) = when (scale) {
            Scale.SMALL -> smallHeight
            Scale.MEDIUM -> mediumHeight
            Scale.LARGE -> largeHeight
        }

        fun icon(scale: Scale) = when (scale) {
            Scale.SMALL -> smallIcon
            Scale.MEDIUM -> mediumIcon
            Scale.LARGE -> largeIcon
        }
    }

    companion object {
        fun from(
            width: Dp,
            height: Dp,
            density: Density,
        ): LayoutConstraints {
            val base = if (width <= height) width else min(width, height * 1.25f)

            val screen = Screen(
                base = base,
                width = width,
                height = height,
                isVertical = width <= height,
                isHorizontal = width >= height,
            )
            val padding = Padding(
                small = base * 0.02f,
                medium = base * 0.04f,
                large = base * 0.08f,
                innerSmall = base * 0.005f,
                innerMedium = base * 0.010f,
                innerLarge = base * 0.020f,
            )
            val border = Border(
                small = base * 0.002f,
                medium = base * 0.004f,
                large = base * 0.008f,
            )
            val typography = run {
                val lineHeight = 1.2f

                val small = TypographySize(
                    size = base * 0.016f,
                    lineHeight = lineHeight,
                    density = density,
                )
                val medium = TypographySize(
                    size = base * 0.024f,
                    lineHeight = lineHeight,
                    density = density,
                )
                val large = TypographySize(
                    size = base * 0.036f,
                    lineHeight = lineHeight,
                    density = density,
                )

                Typography(
                    lineHeight = lineHeight,
                    small = small,
                    medium = medium,
                    large = large,
                )
            }
            val component = run {
                val availableWidth = screen.width - 2 * padding(Scale.LARGE)
                val availableHeight = screen.height - 2 * padding(Scale.LARGE)
                val paddingBetweenCellAndPredict = padding(Scale.MEDIUM)

                val cellBoard: Dp
                val predictItem: Dp
                val predictBoard: DpSize
                val dialog: DpSize

                if (screen.isVertical) {
                    val estimatedPredictBoardWidth = screen.width - 2 * padding(Scale.LARGE)
                    val estimatedPredictBoardHeight = 0.5f * screen.height - 0.5f * (screen.width - 2 * padding(Scale.LARGE)) - paddingBetweenCellAndPredict - padding(Scale.LARGE)

                    if (estimatedPredictBoardWidth >= estimatedPredictBoardHeight * 5f / 2f) {
                        // 기대보다 여유 높이가 적은 경우
                        val boardBase = 0.5f * screen.height - paddingBetweenCellAndPredict - padding(Scale.LARGE)
                        cellBoard = 2f * 5f / 9f * boardBase
                        predictItem = 2f / 9f * boardBase
                        predictBoard = DpSize(
                            width = 5f * predictItem,
                            height = 2f * predictItem,
                        )
                    } else {
                        // 기대보다 여유 높이가 많은 경우
                        cellBoard = screen.width - 2 * padding(Scale.LARGE)
                        predictItem = 0.2f * cellBoard
                        predictBoard = DpSize(
                            width = 5f * predictItem,
                            height = 2f * predictItem,
                        )
                    }
                    dialog = DpSize(
                        width = availableWidth,
                        height = min(availableHeight, base * 1.5f),
                    )
                } else {
                    val estimatedPredictBoardWidth = 0.5f * screen.width - 0.5f * (screen.height - 2 * padding(Scale.LARGE)) - paddingBetweenCellAndPredict - padding(Scale.LARGE)
                    val estimatedPredictBoardHeight = screen.height - 2 * padding(Scale.LARGE)

                    if (estimatedPredictBoardHeight >= estimatedPredictBoardWidth * 5f / 2f) {
                        // 기대보다 여유 너비가 적은 경우
                        val boardBase = 0.5f * screen.width - paddingBetweenCellAndPredict - padding(Scale.LARGE)
                        cellBoard = 2f * 5f / 9f * boardBase
                        predictItem = 2f / 9f * boardBase
                        predictBoard = DpSize(
                            width = 2f * predictItem,
                            height = 5f * predictItem,
                        )
                    } else {
                        // 기대보다 여유 너비가 많은 경우
                        cellBoard = screen.height - 2 * padding(Scale.LARGE)
                        predictItem = 0.2f * cellBoard
                        predictBoard = DpSize(
                            width = 2f * predictItem,
                            height = 5f * predictItem,
                        )
                    }
                    dialog = DpSize(
                        width = min(availableWidth, base * 0.8f),
                        height = availableHeight,
                    )
                }

                Component(
                    cellBoard = cellBoard,
                    predictItem = predictItem,
                    predictBoard = predictBoard,
                    dialog = dialog,

                    smallHeight = base * 0.04f,
                    mediumHeight = base * 0.06f,
                    largeHeight = base * 0.08f,

                    smallIcon = base * 0.02f,
                    mediumIcon = base * 0.04f,
                    largeIcon = base * 0.06f,
                )
            }

            return LayoutConstraints(
                screen = screen,
                padding = padding,
                border = border,
                typography = typography,
                component = component,
            )
        }
    }
}

enum class Scale { SMALL, MEDIUM, LARGE }

@Immutable
data class TypographySize(
    val size: Dp,
    val lineHeight: Float,
    private val density: Density,
) {
    val dp: Dp get() = size
    val sp: TextUnit get() = with(density) { dp.toSp() }
    val lineDp: Dp get() = size * lineHeight
    val lineSp: TextUnit get() = with(density) { lineDp.toSp() }


    operator fun unaryPlus() = this
    operator fun unaryMinus() = this * -1f

    operator fun plus(other: TypographySize) = TypographySize(
        size = this.size + other.size,
        lineHeight = 0.5f * (this.lineHeight + other.lineHeight),
        density = this.density,
    )

    operator fun minus(other: TypographySize) = TypographySize(
        size = this.size - other.size,
        lineHeight = 0.5f * (this.lineHeight + other.lineHeight),
        density = this.density,
    )

    operator fun times(scale: Number) = TypographySize(
        size = this.size * scale.toFloat(),
        lineHeight = this.lineHeight,
        density = this.density,
    )

    operator fun div(scale: Number) = TypographySize(
        size = this.size / scale.toFloat(),
        lineHeight = this.lineHeight,
        density = this.density,
    )

    companion object {
        operator fun Number.times(typographySize: TypographySize) = TypographySize(
            size = this.toFloat() * typographySize.size,
            lineHeight = typographySize.lineHeight,
            density = typographySize.density,
        )
    }
}

@Composable
fun rememberLayoutConstraints(
    width: Dp,
    height: Dp,
): LayoutConstraints {
    val density = LocalDensity.current
    return remember(width, height, density) {
        LayoutConstraints.from(
            width = width,
            height = height,
            density = density
        )
    }
}

val LocalLayoutConstraints = staticCompositionLocalOf {
    LayoutConstraints.from(
        width = 800.dp,
        height = 1200.dp,
        density = Density(density = 3.0f)
    )
}
