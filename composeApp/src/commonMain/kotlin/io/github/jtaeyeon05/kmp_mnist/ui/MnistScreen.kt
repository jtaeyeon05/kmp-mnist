package io.github.jtaeyeon05.kmp_mnist.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.times
import io.github.jtaeyeon05.kmp_mnist.buildinfo.BuildInfo
import io.github.jtaeyeon05.kmp_mnist.ml.argmax
import io.github.jtaeyeon05.kmp_mnist.ml.softmax
import io.github.jtaeyeon05.kmp_mnist.tappable
import io.github.jtaeyeon05.kmp_mnist.ui.component.LoadingBox
import io.github.jtaeyeon05.kmp_mnist.ui.component.RectangleTextButton
import io.github.jtaeyeon05.kmp_mnist.ui.theme.LocalLayoutConstraints
import io.github.jtaeyeon05.kmp_mnist.ui.theme.Scale
import kotlin.math.abs
import kotlin.math.roundToInt


@Composable
fun MnistScreen(
    viewModel: MnistViewModel = remember { MnistViewModel() }
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
                                val paddingPx = padding(Scale.LARGE).toPx() + border(Scale.MEDIUM).toPx()
                                val x1 = (viewModel.cellSize * (touchPoint.x - paddingPx) / (size.width - 2 * paddingPx)).toInt()
                                val y1 = (viewModel.cellSize * (touchPoint.y - paddingPx) / (size.height - 2 * paddingPx)).toInt()

                                if (lastPoint != null) {
                                    val (x0, y0) = lastPoint!!

                                    if (x0 != x1 || y0 != y1) {
                                        val steps = maxOf(abs(x1 - x0), abs(y1 - y0))
                                        for (i in 0 .. steps) {
                                            val x = x0 + i * (x1 - x0) / steps
                                            val y = y0 + i * (y1 - y0) / steps
                                            viewModel.draw(x = x, y = y)
                                        }

                                        lastPoint = x1 to y1
                                        if (viewModel.realtimeMode) viewModel.predict(realtime = true)
                                    }
                                } else {
                                    viewModel.draw(x = x1, y = y1)

                                    lastPoint = x1 to y1
                                    if (viewModel.realtimeMode) viewModel.predict(realtime = true)
                                }
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
                    .tappable { offset ->
                        val paddingPx = padding(Scale.LARGE).toPx() + border(Scale.MEDIUM).toPx()
                        val x = (viewModel.cellSize * (offset.x - paddingPx)  / (size.width - 2 * paddingPx)).toInt()
                        val y = (viewModel.cellSize * (offset.y - paddingPx) / (size.height - 2 * paddingPx)).toInt()

                        viewModel.draw(x = x, y = y)
                        viewModel.predict()
                    }
                    .padding(padding(Scale.LARGE))
                    .size(component.cellBoard)
                    .background(
                        color = MaterialTheme.colorScheme.background
                    )
                    .border(
                        width = border(Scale.MEDIUM),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    .padding(border(Scale.MEDIUM))
                    .align(Alignment.Center)
            ) {
                val cellPx = (component.cellBoard - 2 * border(Scale.MEDIUM)).toPx() / viewModel.cellSize.toFloat()
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
                            style = Stroke(width = border(Scale.SMALL).toPx())
                        )
                    }
                }
            }

            // PredictBoard
            Box(
                modifier = Modifier
                    .padding(padding(Scale.LARGE))
                    .size(component.predictBoard)
                    .background(MaterialTheme.colorScheme.background)
                    .border(
                        width = border(Scale.MEDIUM),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    .padding(border(Scale.MEDIUM))
                    .align(if (screen.isVertical) Alignment.BottomCenter else Alignment.CenterEnd),
                contentAlignment = Alignment.Center,
            ) {
                val softmax = if (viewModel.prediction != null) softmax(viewModel.prediction!!) else FloatArray(10) { 0f }
                val result = if (viewModel.prediction != null) argmax(viewModel.prediction!!).let { if (softmax[it] >= 0.75f) it else null } else null
                val (rows, columns) = (if (screen.isVertical) 2 else 5) to (if (screen.isVertical) 5 else 2)

                Column {
                    for (i1 in 0..< rows) {
                        Row {
                            for (i2 in 0..< columns) {
                                val index = (i1 * rows + i2 + 1) % 10
                                Column(
                                    modifier = Modifier
                                        .size(component.predictItem)
                                        .background(
                                            color = MaterialTheme.colorScheme.onBackground
                                                .copy(alpha = 0.5f * softmax[index])
                                                .compositeOver(MaterialTheme.colorScheme.background),
                                        )
                                        .border(
                                            width = border(Scale.MEDIUM),
                                            color = MaterialTheme.colorScheme.onBackground,
                                        ),
                                    verticalArrangement = Arrangement.spacedBy(
                                        space = padding.inner(Scale.SMALL),
                                        alignment = Alignment.CenterVertically
                                    ),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    val textColor = if (index == result) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground
                                    Text(
                                        text = "",
                                        style = LocalTextStyle.current.copy(
                                            fontSize = typography(Scale.MEDIUM).sp,
                                            lineHeight = typography(Scale.MEDIUM).lineSp,
                                            lineHeightStyle = LineHeightStyle(
                                                alignment = LineHeightStyle.Alignment.Center,
                                                trim = LineHeightStyle.Trim.None
                                            ),
                                        )
                                    )
                                    Text(
                                        text = "$index",
                                        style = LocalTextStyle.current.copy(
                                            color = textColor,
                                            textAlign = TextAlign.Center,
                                            fontSize = typography(Scale.LARGE).sp,
                                            lineHeight = typography(Scale.LARGE).lineSp,
                                            lineHeightStyle = LineHeightStyle(
                                                alignment = LineHeightStyle.Alignment.Center,
                                                trim = LineHeightStyle.Trim.None
                                            ),
                                        )
                                    )
                                    Text(
                                        text = "${(100 * softmax[index]).roundToInt()}%",
                                        style = LocalTextStyle.current.copy(
                                            color = textColor,
                                            textAlign = TextAlign.Center,
                                            fontSize = typography(Scale.MEDIUM).sp,
                                            lineHeight = typography(Scale.MEDIUM).lineSp,
                                            lineHeightStyle = LineHeightStyle(
                                                alignment = LineHeightStyle.Alignment.Center,
                                                trim = LineHeightStyle.Trim.None
                                            ),
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Toolbar
            Column(
                modifier = Modifier
                    .padding(padding(Scale.SMALL))
                    .width(component.height(Scale.LARGE))
                    .align(Alignment.TopStart),
                verticalArrangement = Arrangement.spacedBy(padding(Scale.SMALL)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                RectangleTextButton(
                    scale = Scale.LARGE,
                    onClick = { viewModel.toggleBrushMode() },
                ) {
                    Text(
                        text = when (viewModel.brushMode) {
                            BrushMode.PENCIL -> "✎"
                            BrushMode.SMALL_BRUSH -> "✑"
                            BrushMode.LARGE_BRUSH -> "✑"
                        },
                        fontWeight = when (viewModel.brushMode) {
                            BrushMode.PENCIL -> FontWeight.Normal
                            BrushMode.SMALL_BRUSH -> FontWeight.Normal
                            BrushMode.LARGE_BRUSH -> FontWeight.Bold
                        },
                    )
                }
                RectangleTextButton(
                    scale = Scale.LARGE,
                    onClick = {
                        viewModel.clear()
                        viewModel.predict()
                    },
                ) {
                    Text(text = "⟲")
                }
                RectangleTextButton(
                    scale = Scale.LARGE,
                    onClick = { viewModel.showDialog() },
                ) {
                    Text(text = "?")
                }
                if (viewModel.isLoading) {
                    LoadingBox(
                        scale = Scale.LARGE,
                    )
                }
            }

            // Version
            Text(
                modifier = Modifier
                    .padding(padding(Scale.SMALL))
                    .align(Alignment.TopEnd),
                text = "${BuildInfo.RELEASE_NAME} [${BuildInfo.RELEASE_CODE}]\n${BuildInfo.BUILD_NUMBER}",
                fontSize = typography(Scale.MEDIUM).sp,
                lineHeight = typography(Scale.MEDIUM).sp,
                textAlign = TextAlign.End,
            )

            // Dialog
            if (viewModel.showDialog) {
                MnistDialog(viewModel = viewModel)
            }
        }
    }
}
