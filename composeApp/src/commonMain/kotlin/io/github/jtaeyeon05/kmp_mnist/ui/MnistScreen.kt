package io.github.jtaeyeon05.kmp_mnist.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.times
import io.github.jtaeyeon05.kmp_mnist.buildinfo.BuildInfo
import io.github.jtaeyeon05.kmp_mnist.ml.argmax
import io.github.jtaeyeon05.kmp_mnist.ml.softmax
import io.github.jtaeyeon05.kmp_mnist.ui.component.LoadingBox
import io.github.jtaeyeon05.kmp_mnist.ui.component.SquareButton
import io.github.jtaeyeon05.kmp_mnist.ui.theme.LocalLayoutConstraints
import sk.ainet.lang.tensor.pprint
import kotlin.math.roundToInt


@Composable
fun MnistScreen(
    viewModel: MnistViewModel = rememberSaveable { MnistViewModel() }
) {
    LocalLayoutConstraints.current.run {
        // CellBoard
        Box(modifier = Modifier.fillMaxSize()) {
            val contentColor = LocalContentColor.current

            Canvas(
                modifier = Modifier
                    .pointerInput(Unit) {
                        var lastPoint: Pair<Int, Int>? = null

                        detectDragGestures(
                            onDrag = { change, _ ->
                                val touchPoint = change.position
                                val paddingPx = padding.large.toPx() + border.medium.toPx()
                                val x = (viewModel.cellSize * (touchPoint.x - paddingPx) / (size.width - 2 * paddingPx)).toInt()
                                val y = (viewModel.cellSize * (touchPoint.y - paddingPx) / (size.height - 2 * paddingPx)).toInt()

                                if (lastPoint != x to y) {
                                    viewModel.draw(x = x, y = y)
                                    lastPoint = x to y
                                }
                                if (viewModel.realtimeMode) viewModel.predict()
                            },
                            onDragCancel = {
                                lastPoint = null
                                if (!viewModel.realtimeMode) viewModel.predict()
                            },
                            onDragEnd = {
                                lastPoint = null
                                if (!viewModel.realtimeMode) viewModel.predict()
                            },
                        )
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset ->
                                val paddingPx = padding.large.toPx() + border.medium.toPx()
                                val x = (viewModel.cellSize * (offset.x - paddingPx)  / (size.width - 2 * paddingPx)).toInt()
                                val y = (viewModel.cellSize * (offset.y - paddingPx) / (size.height - 2 * paddingPx)).toInt()

                                viewModel.draw(x = x, y = y)
                                viewModel.predict()
                            },
                        )
                    }
                    .padding(padding.large)
                    .size(component.cellBoard)
                    .background(
                        color = MaterialTheme.colorScheme.background
                    )
                    .border(
                        width = border.medium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    .padding(border.medium)
                    .align(Alignment.Center)
            ) {
                val cellPx = (component.cellBoard - 2 * border.medium).toPx() / viewModel.cellSize.toFloat()
                for (y in 0 ..< viewModel.cellSize) {
                    for (x in 0 ..< viewModel.cellSize) {
                        // Cell
                        drawRect(
                            color = contentColor.copy(alpha = viewModel.cellMap[y][x]),
                            topLeft = Offset(x * cellPx, y * cellPx),
                            size = Size(cellPx, cellPx)
                        )
                        // Cell Border
                        drawRect(
                            color = contentColor,
                            topLeft = Offset(x * cellPx, y * cellPx),
                            size = Size(cellPx, cellPx),
                            style = Stroke(width = border.small.toPx())
                        )
                    }
                }
            }

            // OutputBoard
            Box(
                modifier = Modifier
                    .padding(padding.large)
                    .size(component.outputBoard)
                    .background(
                        color = MaterialTheme.colorScheme.background
                    )
                    .border(
                        width = border.medium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    .align(if (screen.isVertical) Alignment.BottomCenter else Alignment.CenterEnd),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when {
                        viewModel.prediction == null -> "Prediction"
                        else -> {
                            """
                                ${argmax(viewModel.prediction!!)}
                                ${softmax(viewModel.prediction!!).joinToString("-") { "${(100f * it).roundToInt()}%" }}
                                ${viewModel.prediction!!.pprint()}
                            """.trimIndent()
                        }
                    },
                    fontSize = typography.small.sp,
                    lineHeight = typography.small.sp,
                    textAlign = TextAlign.Center,
                )
            }

            // Toolbar
            Column(
                modifier = Modifier
                    .padding(padding.small)
                    .width(component.squareButton.width)
                    .align(Alignment.TopStart),
                verticalArrangement = Arrangement.spacedBy(padding.small),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SquareButton(
                    text = when (viewModel.brushMode) {
                        BrushMode.PENCIL -> "✎"
                        BrushMode.SMALL_BRUSH -> "✑"
                        BrushMode.LARGE_BRUSH -> "✑"
                    },
                    style = LocalTextStyle.current.copy(
                        fontWeight = when (viewModel.brushMode) {
                            BrushMode.PENCIL -> FontWeight.Normal
                            BrushMode.SMALL_BRUSH -> FontWeight.Normal
                            BrushMode.LARGE_BRUSH -> FontWeight.Bold
                        },
                    ),
                    onClick = { viewModel.toggleBrushMode() },
                )
                SquareButton(
                    text = "⟲",
                    onClick = {
                        viewModel.clear()
                        viewModel.predict()
                    },
                )
                SquareButton(
                    text = "?",
                    onClick = { viewModel.showDialog() },
                )
                if (viewModel.isLoading) {
                    LoadingBox()
                }
            }

            // Version
            Text(
                modifier = Modifier
                    .padding(padding.small)
                    .align(Alignment.TopEnd),
                text = "${BuildInfo.RELEASE_NAME} [${BuildInfo.RELEASE_CODE}]\n${BuildInfo.BUILD_NUMBER}",
                fontSize = typography.small.sp,
                lineHeight = typography.small.sp,
                textAlign = TextAlign.End,
            )

            // Dialog
            if (viewModel.showDialog) {
                // TODO: Dialog
            }
        }
    }
}
