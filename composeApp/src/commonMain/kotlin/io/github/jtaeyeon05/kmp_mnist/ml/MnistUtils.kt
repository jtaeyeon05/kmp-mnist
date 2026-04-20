package io.github.jtaeyeon05.kmp_mnist.ml

import androidx.compose.ui.unit.IntSize
import sk.ainet.lang.tensor.Shape
import sk.ainet.lang.tensor.Tensor
import sk.ainet.lang.tensor.softmax
import sk.ainet.lang.types.FP32


fun softmax(input: Tensor<FP32, Float>): FloatArray {
    return input.softmax().data.copyToFloatArray()
}

fun argmax(input: Tensor<FP32, Float>): Int {
    val inputArray = input.data.copyToFloatArray()
    var maxIndex = -1
    var maxValue = Float.NEGATIVE_INFINITY
    for (i in inputArray.indices) {
        if (inputArray[i] > maxValue) {
            maxIndex = i
            maxValue = inputArray[i]
        }
    }
    return maxIndex
}

fun List<List<Float>>.toMnistInputTensor(): Tensor<FP32, Float> {
    val data = FloatArray(28 * 28)

    val size = IntSize(this[0].size, this.size)
    val padding = IntSize((28 - size.width) / 2, (28 - size.height) / 2)
    for (y in 0 ..< size.height) {
        for (x in 0 ..< size.width) {
            data[(y + padding.height) * 28 + (x + padding.width)] = this[y][x]
        }
    }

    return baseContext.fromFloatArray(
        shape = Shape(1, 1, 28, 28),
        dtype = FP32::class,
        data = data,
    )
}
