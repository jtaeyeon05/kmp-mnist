package io.github.jtaeyeon05.kmp_mnist.ml

import kmp_mnist.composeapp.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.Buffer
import sk.ainet.context.DirectCpuExecutionContext
import sk.ainet.io.gguf.GGUFReader
import sk.ainet.lang.nn.DefaultNeuralNetworkExecutionContext
import sk.ainet.lang.nn.Module
import sk.ainet.lang.nn.dsl.CONV2D
import sk.ainet.lang.nn.dsl.sequential
import sk.ainet.lang.tensor.Shape
import sk.ainet.lang.tensor.Tensor
import sk.ainet.lang.tensor.flatten
import sk.ainet.lang.tensor.matmul
import sk.ainet.lang.tensor.plus
import sk.ainet.lang.tensor.relu
import sk.ainet.lang.tensor.t
import sk.ainet.lang.types.FP32


val baseContext = DirectCpuExecutionContext()
val evalContext = DefaultNeuralNetworkExecutionContext()

var mnistModel: Module<FP32, Float>? = null
    private set

suspend fun mnistModel(): Module<FP32, Float> {
    val source = Buffer().apply { write(Res.readBytes("files/MnistCNN.gguf")) }
    val reader = GGUFReader(source)
    val tensorMap = reader.tensors.associateBy { it.name }

    fun CONV2D<FP32, Float>.applyTensors(id: String) {
        val weightsReaderTensor = tensorMap["$id.weight"] ?: throw IllegalArgumentException("Couldn't Find $id.weight")
        val biasReaderTensor = tensorMap["$id.bias"] ?: throw IllegalArgumentException("Couldn't Find $id.bias")

        val weightsTensor = evalContext.fromFloatArray<FP32, Float>(
            shape = weightsShape,
            dtype = FP32::class,
            data = FloatArray(weightsReaderTensor.data.size) { i ->
                (weightsReaderTensor.data[i] as Number).toFloat()
            }
        )
        val biasTensor = evalContext.fromFloatArray<FP32, Float>(
            shape = biasShape,
            dtype = FP32::class,
            data = FloatArray(biasReaderTensor.data.size) { i ->
                (biasReaderTensor.data[i] as Number).toFloat()
            }
        )

        this.weights { weightsTensor }
        this.bias { biasTensor }
    }

    return sequential<FP32, Float>(evalContext) {
        // Layer 1
        sequential {
            conv2d(id = "layer1.conv1_1") {
                inChannels = 1
                outChannels = 16
                trainable = false
                kernelSize(size = 3)
                stride(size = 1)
                padding(size = 1)
                applyTensors(id = "layer1.conv1_1")
            }
            activation { it.relu() }
            conv2d(id = "layer1.conv1_2") {
                inChannels = 16
                outChannels = 16
                trainable = false
                kernelSize(size = 3)
                stride(size = 1)
                padding(size = 1)
                applyTensors(id = "layer1.conv1_2")
            }
            activation { it.relu() }
            maxPool2d {
                kernelSize(size = 2)
                stride(size = 2)
            }
        }
        // Layer 2
        sequential {
            conv2d(id = "layer2.conv2_1") {
                inChannels = 16
                outChannels = 32
                trainable = false
                kernelSize(size = 3)
                stride(size = 1)
                padding(size = 1)
                applyTensors(id = "layer2.conv2_1")
            }
            activation { it.relu() }
            conv2d(id = "layer2.conv2_2") {
                inChannels = 32
                outChannels = 32
                trainable = false
                kernelSize(size = 3)
                stride(size = 1)
                padding(size = 1)
                applyTensors(id = "layer2.conv2_2")
            }
            activation { it.relu() }
            maxPool2d {
                kernelSize(size = 2)
                stride(size = 2)
            }
        }

        // Layer 3
        sequential {
            /**
             * Note: 라이브러리 오류로 인해 Activation을 통해 구현
             * - 이상적인 상황이라면 아래와 같은 코드로 layer3이 정상적으로 작동해야 하나, 여러 버그로 인해 Activation을 통해 구현함.
             *   1. flatten() 버그
             *   1-1. sk.ainet.lang.nn.dsl.NetworkBuilder 1077줄, 1431줄에 의해 flatten의 lastDimension 연산이 미구현되어 1568 (32 * 7 * 7)로 고정됨.
             *   1-2. flatten() 사용 시, 결과 텐서가 0.0으로 고정됨.
             *   2. dense() 버그
             *   2-1. dense의 파라미터가 정상적으로 저장되고, 연산되지 않아 사용 시, 결과 텐서가 0.0으로 고정됨.
             * - 수동 구현으로 인해 Trainable에 등록되지 않아 학습은 불가능함.
             *
             * flatten()
             * dense(outputDimension = 256, id = "layer3.fc3_1")
             * activation { it.relu() }
             * dense(outputDimension = 10, id = "layer3.fc3_2")
             */
            activation { it.flatten(startDim = 1, endDim = 3) }
            activation {
                val weights = run {
                    val readerTensor = tensorMap["layer3.fc3_1.weight"] ?: throw IllegalArgumentException("Cannot Find layer3.fc3_1.weight")
                    val array = FloatArray(readerTensor.data.size) { i ->
                        (readerTensor.data[i] as Number).toFloat()
                    }

                    baseContext.fromFloatArray<FP32, Float>(
                        shape = Shape(256, 1568),
                        dtype = FP32::class,
                        data = array
                    ).t()
                }
                val bias = run {
                    val readerTensor = tensorMap["layer3.fc3_1.bias"] ?: throw IllegalArgumentException("Cannot Find layer3.fc3_1.bias")
                    val array = FloatArray(readerTensor.data.size) { i ->
                        (readerTensor.data[i] as Number).toFloat()
                    }

                    baseContext.fromFloatArray<FP32, Float>(
                        shape = Shape(256, 1),
                        dtype = FP32::class,
                        data = array
                    ).t()
                }
                it.matmul(weights) + bias
            }
            activation { it.relu() }
            activation {
                val weights = run {
                    val readerTensor = tensorMap["layer3.fc3_2.weight"] ?: throw IllegalArgumentException("Cannot Find layer3.fc3_2.weight")
                    val array = FloatArray(readerTensor.data.size) { i ->
                        (readerTensor.data[i] as Number).toFloat()
                    }

                    baseContext.fromFloatArray<FP32, Float>(
                        shape = Shape(10, 256),
                        dtype = FP32::class,
                        data = array
                    ).t()
                }
                val bias = run {
                    val readerTensor = tensorMap["layer3.fc3_2.bias"] ?: throw IllegalArgumentException("Cannot Find layer3.fc3_2.bias")
                    val array = FloatArray(readerTensor.data.size) { i ->
                        (readerTensor.data[i] as Number).toFloat()
                    }

                    baseContext.fromFloatArray<FP32, Float>(
                        shape = Shape(10, 1),
                        dtype = FP32::class,
                        data = array
                    ).t()
                }
                it.matmul(weights) + bias
            }
        }
    }
}

suspend fun initializeMnistModel() = withContext(Dispatchers.Default) {
    mnistModel = mnistModel()
}

suspend fun predictMnistModel(
    input: Tensor<FP32, Float>
): Tensor<FP32, Float>? = withContext(Dispatchers.Default) {
    if (mnistModel == null) return@withContext null

    mnistModel!!.zeroGrad()
    val output = mnistModel!!.forward(
        input = input,
        ctx = evalContext
    )
    output
}
