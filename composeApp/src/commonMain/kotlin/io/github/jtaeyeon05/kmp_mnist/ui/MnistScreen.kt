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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.times
import io.github.jtaeyeon05.kmp_mnist.buildinfo.BuildInfo
import io.github.jtaeyeon05.kmp_mnist.ml.argmax
import io.github.jtaeyeon05.kmp_mnist.ml.initializeModel
import io.github.jtaeyeon05.kmp_mnist.ml.predict
import io.github.jtaeyeon05.kmp_mnist.ml.softmax
import io.github.jtaeyeon05.kmp_mnist.ml.toMnistInputTensor
import io.github.jtaeyeon05.kmp_mnist.ui.component.LoadingBox
import io.github.jtaeyeon05.kmp_mnist.ui.component.SquareButton
import io.github.jtaeyeon05.kmp_mnist.ui.theme.LocalLayoutConstraints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import sk.ainet.lang.tensor.Tensor
import sk.ainet.lang.tensor.pprint
import sk.ainet.lang.types.FP32
import kotlin.math.roundToInt


@Composable
fun MnistScreen() {
    LocalLayoutConstraints.current.run {
        val predictScope = rememberCoroutineScope()
        val contentColor = LocalContentColor.current

        val cellSize by rememberSaveable { mutableStateOf(20) }
        val cellMap = rememberSaveable(cellSize) {
            SnapshotStateList(cellSize) {
                SnapshotStateList(cellSize) {
                    0f
                }
            }
        }
        var prediction by remember { mutableStateOf<Tensor<FP32, Float>?>(null) }
        var predictJob by remember { mutableStateOf<Job?>(null) }
        var isModelLoaded by rememberSaveable { mutableStateOf(false) }

        var brushMode by rememberSaveable { mutableStateOf(1) }  // 0: Pen, 1: Small Brush, 2: Big Brush (TMP)
        var realtimeComputationMode by rememberSaveable { mutableStateOf(false) }

        fun predict() {
            predictJob?.cancel()
            predictJob = predictScope.launch(Dispatchers.Default) {
                prediction = predict(cellMap.toMnistInputTensor())
            }
        }

        fun updateCell(x: Int, y: Int, delta: Float) {
            if (x in 0 ..< cellSize && y in 0 ..< cellSize) {
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
            for (y in 0 ..< cellSize) {
                for (x in 0 ..< cellSize) {
                    cellMap[y][x] = 0f
                }
            }
            predict()
        }

        LaunchedEffect(Unit) {
            initializeModel()
            isModelLoaded = true
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
                                val paddingPx = padding.large.toPx() + border.medium.toPx()
                                val x = (cellSize * (touchPoint.x - paddingPx) / (size.width - 2 * paddingPx)).toInt()
                                val y = (cellSize * (touchPoint.y - paddingPx) / (size.height - 2 * paddingPx)).toInt()

                                if (lastPoint != x to y) {
                                    draw(x = x, y = y, brushMode = brushMode)
                                    lastPoint = x to y
                                }
                                if (realtimeComputationMode) predict()
                            },
                            onDragCancel = {
                                lastPoint = null
                                if (!realtimeComputationMode) predict()
                            },
                            onDragEnd = {
                                lastPoint = null
                                if (!realtimeComputationMode) predict()
                            },
                        )
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset ->
                                val paddingPx = padding.large.toPx() + border.medium.toPx()
                                val x = (cellSize * (offset.x - paddingPx)  / (size.width - 2 * paddingPx)).toInt()
                                val y = (cellSize * (offset.y - paddingPx) / (size.height - 2 * paddingPx)).toInt()

                                draw(x = x, y = y, brushMode = brushMode)
                                predict()
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
                val cellPx = (component.cellBoard - 2 * border.medium).toPx() / cellSize.toFloat()
                for (y in 0 ..< cellSize) {
                    for (x in 0 ..< cellSize) {
                        // Cell
                        drawRect(
                            color = contentColor.copy(alpha = cellMap[y][x]),
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
                        prediction == null -> "Prediction"
                        else -> {
                            """
                                ${argmax(prediction!!)}
                                ${softmax(prediction!!).joinToString("-") { "${(100f * it).roundToInt()}%" }}
                                ${prediction!!.pprint()}
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
                if (!isModelLoaded || predictJob?.isActive ?: false) {
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
        }
    }
}
