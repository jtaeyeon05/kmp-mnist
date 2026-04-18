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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import io.github.jtaeyeon05.kmp_mnist.buildinfo.BuildInfo
import io.github.jtaeyeon05.kmp_mnist.test


@Composable
fun MnistScreen() {
    LocalLayoutConstraints.current.run {
        val contentColor = LocalContentColor.current
        val cellMap = rememberSaveable {
            SnapshotStateList(20) {
                SnapshotStateList(20) {
                    0f
                }
            }
        }
        var brushMode by rememberSaveable { mutableStateOf(1) }  // 0: Pen, 1: Small Brush, 2: Big Brush (TMP)
        var testOutput by rememberSaveable { mutableStateOf("Output") }

        fun updateCell(x: Int, y: Int, delta: Float) {
            if (x in 0 ..< 20 && y in 0 ..< 20) {
                cellMap[y][x] = (cellMap[y][x] + delta).coerceIn(0f .. 1f)
            }
        }

        fun draw(x: Int, y: Int, brushMode: Int = 0) {
            when (brushMode) {
                0 -> {
                    updateCell(x = x, y = y, delta = 1.0f)
                }
                1 -> {
                    updateCell(x = x, y = y, delta = 1.0f)
                    updateCell(x = x - 1, y = y, delta = 0.5f)
                    updateCell(x = x + 1, y = y, delta = 0.5f)
                    updateCell(x = x, y = y - 1, delta = 0.5f)
                    updateCell(x = x, y = y + 1, delta = 0.5f)
                }
                2 -> {
                    updateCell(x, y, 1.00f)
                    updateCell(x - 1, y, 0.75f)
                    updateCell(x + 1, y, 0.75f)
                    updateCell(x, y - 1, 0.75f)
                    updateCell(x, y + 1, 0.75f)
                    updateCell(x - 1, y - 1, 0.50f)
                    updateCell(x + 1, y - 1, 0.50f)
                    updateCell(x - 1, y + 1, 0.50f)
                    updateCell(x + 1, y + 1, 0.50f)
                    updateCell(x - 2, y, 0.25f)
                    updateCell(x + 2, y, 0.25f)
                    updateCell(x, y - 2, 0.25f)
                    updateCell(x, y + 2, 0.25f)
                }
            }
        }

        fun clear() {
            for (y in 0 ..< 20) {
                for (x in 0 ..< 20) {
                    cellMap[y][x] = 0f
                }
            }
        }

        // TODO
        fun test() {
            testOutput = test(cellMap.map { it.toList() })
        }

        // CellBoard
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(
                modifier = Modifier
                    .pointerInput(Unit) {
                        var lastPoint: Pair<Int, Int>? = null

                        detectDragGestures(
                            onDrag = { change, _ ->
                                val touchPoint = change.position
                                val x = (20f * (touchPoint.x - padding.large.toPx()) / (size.width - 2 * padding.large.toPx())).toInt()
                                val y = (20f * (touchPoint.y - padding.large.toPx()) / (size.height - 2 * padding.large.toPx())).toInt()

                                if (lastPoint != x to y) {
                                    draw(x = x, y = y, brushMode = brushMode)
                                    test()
                                    lastPoint = x to y
                                }
                            },
                            onDragCancel = { lastPoint = null },
                            onDragEnd = { lastPoint = null },
                        )
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset ->
                                val x = (20f * (offset.x - padding.large.toPx())  / (size.width - 2 * padding.large.toPx())).toInt()
                                val y = (20f * (offset.y - padding.large.toPx()) / (size.height - 2 * padding.large.toPx())).toInt()

                                draw(x = x, y = y, brushMode = brushMode)
                                test()
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
                    .align(Alignment.Center)
            ) {
                val cellSize = component.cell.toPx()
                for (y in 0 until 20) {
                    for (x in 0 until 20) {
                        // Cell
                        drawRect(
                            color = contentColor.copy(alpha = cellMap[y][x]),
                            topLeft = Offset(x * cellSize, y * cellSize),
                            size = Size(cellSize, cellSize)
                        )
                        // Cell Border
                        drawRect(
                            color = contentColor,
                            topLeft = Offset(x * cellSize, y * cellSize),
                            size = Size(cellSize, cellSize),
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
                    text = testOutput,
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
                    text = when (brushMode) {
                        0 -> "✎"
                        1 -> "✑"
                        else -> "✑"
                    },
                    style = LocalTextStyle.current.copy(
                        fontWeight = when (brushMode) {
                            0 -> FontWeight.Normal
                            1 -> FontWeight.Normal
                            else -> FontWeight.Bold
                        },
                    ),
                    onClick = {
                        brushMode += 1
                        if (brushMode > 2) brushMode = 0
                    },
                )
                SquareButton(
                    text = "⟲",
                    onClick = { clear() },
                )
                SquareButton(
                    text = "?",
                    onClick = { /* TODO */ },
                )
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
        }
    }
}
