package io.github.jtaeyeon05.kmp_mnist.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jtaeyeon05.kmp_mnist.ml.initializeModel
import io.github.jtaeyeon05.kmp_mnist.ml.toMnistInputTensor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import sk.ainet.lang.tensor.Tensor
import sk.ainet.lang.types.FP32


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

    private var predictJob: Job? = null

    private var isModelLoaded by mutableStateOf(false)

    init {
        viewModelScope.launch {
            initializeModel()
            isModelLoaded = true
        }
    }

    val isLoading
        get() = !isModelLoaded || predictJob?.isActive == true

    fun predict() {
        predictJob?.cancel()
        predictJob = viewModelScope.launch(Dispatchers.Default) {
            prediction = io.github.jtaeyeon05.kmp_mnist.ml.predict(cellMap.toMnistInputTensor())
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
