package io.github.jtaeyeon05.kmp_mnist.ui

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
        val small: Dp,
        val medium: Dp,
        val large: Dp,
    )

    data class Border(
        val small: Dp,
        val medium: Dp,
        val large: Dp,
    )

    data class Typography(
        val small: TypographySize,
        val medium: TypographySize,
        val large: TypographySize,
        val lineHeight: Float,
    )

    data class Component(
        val cell: Dp,
        val cellBoard: Dp,
        val outputBoard: DpSize,
    )

    class TypographySize(
        val size: Dp,
        val lineHeight: Float,
        private val density: Density,
    ) {
        val dp: Dp get() = size
        val sp: TextUnit get() = with(density) { dp.toSp() }
        val lineDp: Dp get() = size * lineHeight
        val lineSp: TextUnit get() = with(density) { lineDp.toSp() }
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
            )
            val border = Border(
                small = base * 0.002f,
                medium = base * 0.004f,
                large = base * 0.008f,
            )
            val typography = run {
                val lineHeight = 1.2f

                val small = TypographySize(
                    size = base * 0.02f,
                    lineHeight = lineHeight,
                    density = density,
                )
                val medium = TypographySize(
                    size = base * 0.04f,
                    lineHeight = lineHeight,
                    density = density,
                )
                val large = TypographySize(
                    size = base * 0.08f,
                    lineHeight = lineHeight,
                    density = density,
                )

                Typography(
                    small = small,
                    medium = medium,
                    large = large,
                    lineHeight = lineHeight,
                )
            }
            val component = run {
                val availableWidth = screen.width - 2 * padding.large
                val availableHeight = screen.height - 2 * padding.large

                val cell: Dp
                val cellBoard: Dp
                val outputBoard: DpSize

                if (screen.isVertical) {
                    if (availableWidth <= availableHeight - 2 * (0.2f * availableHeight + padding.medium)) {
                        cellBoard = availableWidth
                        cell = cellBoard * 0.05f
                        outputBoard = DpSize(
                            width = cellBoard,
                            height = 0.5f * (availableHeight - cellBoard) - padding.medium,
                        )
                    } else {
                        val outputBoardHeight = 0.2f * availableHeight
                        cellBoard = availableHeight - 2 * (0.2f * availableHeight + padding.medium)
                        cell = cellBoard * 0.05f
                        outputBoard = DpSize(
                            width = cellBoard,
                            height = outputBoardHeight,
                        )
                    }
                } else {
                    if (availableHeight <= availableWidth - 2 * (0.2f * availableWidth + padding.medium)) {
                        cellBoard = availableHeight
                        cell = cellBoard * 0.05f
                        outputBoard = DpSize(
                            width = 0.5f * (availableWidth - cellBoard) - padding.medium,
                            height = cellBoard,
                        )
                    } else {
                        val outputBoardWidth = 0.2f * availableWidth
                        cellBoard = availableWidth - 2 * (0.2f * availableWidth + padding.medium)
                        cell = cellBoard * 0.05f
                        outputBoard = DpSize(
                            width = outputBoardWidth,
                            height = cellBoard,
                        )
                    }
                }

                Component(
                    cell = cell,
                    cellBoard = cellBoard,
                    outputBoard = outputBoard,
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
