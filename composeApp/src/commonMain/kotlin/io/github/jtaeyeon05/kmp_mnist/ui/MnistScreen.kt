package io.github.jtaeyeon05.kmp_mnist.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.text.style.TextAlign
import io.github.jtaeyeon05.kmp_mnist.buildinfo.BuildInfo


@Composable
fun MnistScreen() {
    LocalLayoutConstraints.current.run {
        val contentColor = LocalContentColor.current
        val boxMap = rememberSaveable {
            SnapshotStateList(20) {
                SnapshotStateList(20) {
                    0f
                }
            }
        }
        var isBrushed by rememberSaveable { mutableStateOf(true) }

        // CellBoard
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(
                modifier = Modifier
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
                    .pointerInput(Unit) {
                        var lastPoint: Pair<Int, Int>? = null

                        detectDragGestures(
                            onDrag = { change, _ ->
                                val touchPoint = change.position
                                val x = (20f * touchPoint.x / size.width.toFloat()).toInt().coerceIn(0, 19)
                                val y = (20f * touchPoint.y / size.height.toFloat()).toInt().coerceIn(0, 19)
                                println("x: $x, y: $y")

                                if (lastPoint != x to y) {
                                    if (isBrushed) {
                                        if (x - 1 in 0 ..< 20) boxMap[x - 1][y] = (boxMap[x - 1][y] + 0.25f).coerceAtMost(1f)
                                        if (x + 1 in 0 ..< 20) boxMap[x + 1][y] = (boxMap[x + 1][y] + 0.25f).coerceAtMost(1f)
                                        if (y - 1 in 0 ..< 20) boxMap[x][y - 1] = (boxMap[x][y - 1] + 0.25f).coerceAtMost(1f)
                                        if (y + 1 in 0 ..< 20) boxMap[x][y + 1] = (boxMap[x][y + 1] + 0.25f).coerceAtMost(1f)
                                        boxMap[x][y] = (boxMap[x][y] + 0.5f).coerceAtMost(1f)
                                    } else {
                                        boxMap[x][y] = (boxMap[x][y] + 0.5f).coerceAtMost(1f)
                                    }
                                    lastPoint = x to y
                                }
                            },
                            onDragCancel = { lastPoint = null },
                            onDragEnd = { lastPoint = null },
                        )
                    }
            ) {
                val cellSize = component.cell.toPx()
                for (x in 0 until 20) {
                    for (y in 0 until 20) {
                        // Cell
                        drawRect(
                            color = contentColor.copy(alpha = boxMap[x][y]),
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
                    text = "OutputBoard",
                    fontSize = typography.medium.sp,
                    lineHeight = typography.medium.sp,
                    textAlign = TextAlign.Center,
                )
            }

            // Version
            Text(
                modifier = Modifier
                    .padding(padding.small)
                    .align(Alignment.BottomEnd),
                text = "${BuildInfo.RELEASE_NAME} [${BuildInfo.RELEASE_CODE}]\n${BuildInfo.BUILD_NUMBER}",
                fontSize = typography.small.sp,
                lineHeight = typography.small.sp,
                textAlign = TextAlign.End,
            )
        }
    }
}
