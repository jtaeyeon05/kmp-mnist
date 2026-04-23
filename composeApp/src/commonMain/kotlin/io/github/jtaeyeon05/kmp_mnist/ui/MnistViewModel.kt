package io.github.jtaeyeon05.kmp_mnist.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jtaeyeon05.kmp_mnist.ml.initializeMnistModel
import io.github.jtaeyeon05.kmp_mnist.ml.predictMnistModel
import io.github.jtaeyeon05.kmp_mnist.ml.toMnistInputTensor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sk.ainet.lang.tensor.Tensor
import sk.ainet.lang.types.FP32
import kotlin.time.Duration.Companion.milliseconds


enum class BrushMode { PENCIL, SMALL_BRUSH, LARGE_BRUSH }

class MnistViewModel: ViewModel() {
    var prediction by mutableStateOf<Tensor<FP32, Float>?>(null)
        private set
    var cellSize by mutableStateOf(20)
        private set
    var cellMap by mutableStateOf(makeCellMap(cellSize))
        private set
    var brushMode by mutableStateOf(BrushMode.SMALL_BRUSH)
        private set
    var realtimeMode by mutableStateOf(false)
        private set
    var showDialog by mutableStateOf(false)
        private set

    val isLoading by derivedStateOf { !isModelLoaded || isPredicting }

    private var predictJob: Job? = null
    private var loadingJob: Job? = null
    private var isModelLoaded by mutableStateOf(false)
    private var isPredicting by mutableStateOf(false)

    init {
        viewModelScope.launch {
            initializeMnistModel()
            predict()
            isModelLoaded = true
        }
    }

    fun predict(realtime: Boolean = false) {
        if (realtime && predictJob?.isActive == true) return
        predictJob?.cancel()
        loadingJob?.cancel()

        isPredicting = true
        predictJob = viewModelScope.launch(Dispatchers.Default) {
            try {
                prediction = predictMnistModel(cellMap.toMnistInputTensor())
            } finally {
                if (predictJob == coroutineContext[Job]) {
                    loadingJob = viewModelScope.launch {
                        delay(50.milliseconds)
                        isPredicting = false
                    }
                }
            }
        }
    }

    fun updateCell(x: Int, y: Int, delta: Float) {
        if (x in 0 ..< cellSize && y in 0 ..< cellSize) {
            cellMap[y][x] = (cellMap[y][x] + delta).coerceIn(0f .. 1f)
        }
    }

    fun draw(x: Int, y: Int, brushMode: BrushMode = this.brushMode) {
        val delta1 = 0.8f
        val delta2 = 0.6f
        val delta3 = 0.4f
        val delta4 = 0.2f

        when (brushMode) {
            BrushMode.PENCIL -> {
                updateCell(x = x, y = y, delta = delta1)
            }
            BrushMode.SMALL_BRUSH -> {
                updateCell(x = x, y = y, delta = delta1)
                updateCell(x = x - 1, y = y, delta = delta3)
                updateCell(x = x + 1, y = y, delta = delta3)
                updateCell(x = x, y = y - 1, delta = delta3)
                updateCell(x = x, y = y + 1, delta = delta3)
            }
            BrushMode.LARGE_BRUSH -> {
                updateCell(x = x, y = y, delta = delta1)
                updateCell(x = x - 1, y = y, delta = delta2)
                updateCell(x = x + 1, y = y, delta = delta2)
                updateCell(x = x, y = y - 1, delta = delta2)
                updateCell(x = x, y = y + 1, delta = delta2)
                updateCell(x = x - 1, y = y - 1, delta = delta3)
                updateCell(x = x + 1, y = y - 1, delta = delta3)
                updateCell(x = x - 1, y = y + 1, delta = delta3)
                updateCell(x = x + 1, y = y + 1, delta = delta3)
                updateCell(x = x - 2, y = y, delta = delta4)
                updateCell(x = x + 2, y = y, delta = delta4)
                updateCell(x = x, y = y - 2, delta = delta4)
                updateCell(x = x, y = y + 2, delta = delta4)
            }
        }
    }

    fun clear() {
        for (y in 0 ..< cellSize) {
            for (x in 0 ..< cellSize) {
                cellMap[y][x] = 0f
            }
        }
    }

    private fun makeCellMap(cellSize: Int): SnapshotStateList<SnapshotStateList<Float>> {
        val cellSize = cellSize.coerceIn(1 .. 28)
        return SnapshotStateList(cellSize) {
            SnapshotStateList(cellSize) {
                0f
            }
        }
    }

    private fun copyCellMap(toCellMap: SnapshotStateList<SnapshotStateList<Float>>, fromCellMap: SnapshotStateList<SnapshotStateList<Float>>): SnapshotStateList<SnapshotStateList<Float>> {
        val toSize = toCellMap.size
        val fromSize = fromCellMap.size

        if (toSize < fromSize) {
            val padding = (fromSize - toSize) / 2
            for (y in 0 ..< toSize) {
                for (x in 0 ..< toSize) {
                    toCellMap[y][x] = fromCellMap[y + padding][x + padding]
                }
            }
        } else if (toSize > fromSize) {
            val padding = (toSize - fromSize) / 2
            for (y in 0 ..< fromSize) {
                for (x in 0 ..< fromSize) {
                    toCellMap[y + padding][x + padding] = fromCellMap[y][x]
                }
            }
        } else {
            for (y in 0 ..< toSize) {
                for (x in 0 ..< toSize) {
                    toCellMap[y][x] = fromCellMap[y][x]
                }
            }
        }

        return toCellMap
    }

    fun updateCellSize(cellSize: Int) {
        this.cellSize = cellSize.coerceIn(1 .. 28)
        this.cellMap = copyCellMap(
            toCellMap = makeCellMap(this.cellSize),
            fromCellMap = this.cellMap,
        )
    }

    fun toggleBrushMode() {
        brushMode = when (brushMode) {
            BrushMode.PENCIL -> BrushMode.SMALL_BRUSH
            BrushMode.SMALL_BRUSH -> BrushMode.LARGE_BRUSH
            BrushMode.LARGE_BRUSH -> BrushMode.PENCIL
        }
    }

    fun toggleRealtimeMode() {
        realtimeMode = !realtimeMode
    }

    fun showDialog() {
        showDialog = true
    }

    fun dismissDialog() {
        showDialog = false
    }
}
