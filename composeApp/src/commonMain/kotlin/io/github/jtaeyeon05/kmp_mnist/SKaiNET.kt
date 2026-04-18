package io.github.jtaeyeon05.kmp_mnist

import sk.ainet.context.DirectCpuExecutionContext
import sk.ainet.lang.nn.DefaultNeuralNetworkExecutionContext
import sk.ainet.lang.nn.dsl.relu
import sk.ainet.lang.nn.dsl.sequential
import sk.ainet.lang.tensor.Shape
import sk.ainet.lang.tensor.Tensor
import sk.ainet.lang.tensor.pprint
import sk.ainet.lang.types.FP16


private val baseContext = DirectCpuExecutionContext()
private val evalContext = DefaultNeuralNetworkExecutionContext()


fun input(
    cellMap: List<List<Float>>
): Tensor<FP16, Float> {
    val data = FloatArray(28 * 28)
    for (y in 0 ..< 20) {
        for (x in 0 ..< 20) {
            data[(y + 4) * 28 + (x + 4)] = cellMap[y][x]
        }
    }

    return baseContext.fromFloatArray(
        shape = Shape(1, 1, 28, 28),
        dtype = FP16::class,
        data = data,
    )
}

fun model() = sequential<FP16, Float>(evalContext) {
    // Layer 1
    sequential {
        input(inputSize = 1)
        conv2d(outChannels = 32, kernelSize = 3 to 3, stride = 1 to 1, padding = 1 to 1)
        input(inputSize = 32)
        relu()
        conv2d(outChannels = 32, kernelSize = 3 to 3, stride = 1 to 1, padding = 1 to 1)
        input(inputSize = 32)
        relu()
        maxPool2d(kernelSize = 2 to 2, stride = 2 to 2)
    }
    // Layer 2
    sequential {
        conv2d(outChannels = 64, kernelSize = 3 to 3, stride = 1 to 1, padding = 1 to 1)
        input(inputSize = 64)
        relu()
        conv2d(outChannels = 64, kernelSize = 3 to 3, stride = 1 to 1, padding = 1 to 1)
        input(inputSize = 64)
        relu()
        maxPool2d(kernelSize = 2 to 2, stride = 2 to 2)
    }
    // Layer 3
    sequential {
        flatten()
        input(inputSize = 64 * 7 * 7)
        dense(outputDimension = 512)
        input(inputSize = 512)
        relu()
        dense(outputDimension = 10)
        input(inputSize = 10)
    }
}

fun argmax(input: Tensor<FP16, Float>): Int {
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

fun test(
    cellMap: List<List<Float>>
): String {
    val input = input(cellMap)
    val model = model()
    val output = model.forward(
        input = input,
        ctx = evalContext
    )
    return output.pprint()
}
