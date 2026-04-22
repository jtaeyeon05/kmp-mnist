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
        when (brushMode) {
            BrushMode.PENCIL -> {
                updateCell(x = x, y = y, delta = 1.0f)
            }
            BrushMode.SMALL_BRUSH -> {
                updateCell(x = x, y = y, delta = 1.0f)
                updateCell(x = x - 1, y = y, delta = 0.5f)
                updateCell(x = x + 1, y = y, delta = 0.5f)
                updateCell(x = x, y = y - 1, delta = 0.5f)
                updateCell(x = x, y = y + 1, delta = 0.5f)
            }
            BrushMode.LARGE_BRUSH -> {
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
    }

    fun makeCellMap(cellSize: Int): SnapshotStateList<SnapshotStateList<Float>> {
        val cellSize = cellSize.coerceIn(1 .. 28)
        return SnapshotStateList(cellSize) {
            SnapshotStateList(cellSize) {
                0f
            }
        }
    }

    fun updateCellSize(cellSize: Int) {
        this.cellSize = cellSize.coerceIn(1 .. 28)
        this.cellMap = makeCellMap(this.cellSize)
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
