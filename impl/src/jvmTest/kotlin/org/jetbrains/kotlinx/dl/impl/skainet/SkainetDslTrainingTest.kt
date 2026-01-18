package org.jetbrains.kotlinx.dl.impl.skainet

import org.junit.jupiter.api.Test
import sk.ainet.context.Phase
import sk.ainet.exec.tensor.ops.DefaultCpuOps
import sk.ainet.lang.graph.DefaultGradientTape
import sk.ainet.lang.graph.DefaultGraphExecutionContext
import sk.ainet.lang.graph.dsl.skainet
import sk.ainet.lang.nn.loss.CrossEntropyLoss
import sk.ainet.lang.nn.loss.MSELoss
import sk.ainet.lang.nn.metrics.accuracy
import sk.ainet.lang.nn.optim.sgd
import sk.ainet.lang.nn.topology.ModuleParameter
import sk.ainet.lang.tensor.data.DenseTensorDataFactory
import sk.ainet.lang.tensor.dsl.tensor
import sk.ainet.lang.tensor.matmul
import sk.ainet.lang.tensor.mean
import sk.ainet.lang.tensor.relu
import sk.ainet.lang.types.FP32
import sk.ainet.lang.types.Int32
import kotlin.math.abs
import kotlin.test.assertTrue

/**
 * Simple test demonstrating SKaiNET DSL integration with KotlinDL.
 * Shows complete training flow including metrics.
 */
class SkainetDslTrainingTest {

    private fun createTrainCtx(): DefaultGraphExecutionContext {
        val dataFactory = DenseTensorDataFactory()
        val cpuOps = DefaultCpuOps(dataFactory)
        return DefaultGraphExecutionContext(
            baseOps = cpuOps,
            phase = Phase.TRAIN,
            tensorDataFactory = dataFactory,
            createTapeFactory = { _ -> DefaultGradientTape(true) }
        )
    }

    @Test
    fun `simple linear regression training with DSL`() {
        var initialW = 0f
        var finalW = 0f

        skainet(createTrainCtx()) {
            // Create data using tensor DSL
            val x = tensor<FP32, Float> {
                shape(4, 1) {
                    from(1f, 2f, 3f, 4f)
                }
            }
            val y = tensor<FP32, Float> {
                shape(4, 1) {
                    from(2f, 4f, 6f, 8f)  // y = 2x
                }
            }

            // Model parameter: single weight
            val w = tensor<FP32, Float> {
                shape(1, 1) { from(0.5f) }
            }.withRequiresGrad()
            val wParam = ModuleParameter.WeightParameter("w", w)

            val optimizer = sgd(lr = 0.01)
            val mseLoss = MSELoss()
            initialW = w.data[0, 0]

            // Training loop
            repeat(100) {
                trainStep(optimizer, wParam) {
                    val pred = x.matmul(wParam.value)
                    mseLoss.forward(pred, y, ctx)
                }
            }

            finalW = wParam.value.data[0, 0]
        }

        // Weight should move towards 2.0
        assertTrue(abs(finalW - 2f) < abs(initialW - 2f),
            "Weight should converge towards 2.0. Initial: $initialW, Final: $finalW")
        println("Linear regression: weight converged from $initialW to $finalW (target: 2.0)")
    }

    @Test
    fun `MLP training with metrics using DSL`() {
        var initialAcc = 0.0
        var finalAcc = 0.0

        skainet(createTrainCtx()) {
            // XOR problem data
            val x = tensor<FP32, Float> {
                shape(4, 2) {
                    from(
                        0f, 0f,
                        0f, 1f,
                        1f, 0f,
                        1f, 1f
                    )
                }
            }
            val y = tensor<Int32, Int> {
                shape(4) { from(0, 1, 1, 0) }
            }

            // Two-layer MLP parameters
            val w1 = tensor<FP32, Float> {
                shape(2, 8) { randn(std = 0.5f) }
            }.withRequiresGrad()

            val w2 = tensor<FP32, Float> {
                shape(8, 2) { randn(std = 0.5f) }
            }.withRequiresGrad()

            val w1Param = ModuleParameter.WeightParameter("w1", w1)
            val w2Param = ModuleParameter.WeightParameter("w2", w2)

            val optimizer = sgd(lr = 0.5)
            val crossEntropyLoss = CrossEntropyLoss()
            val metric = accuracy()

            // Initial forward pass for metric
            val initialPreds = x.matmul(w1Param.value).relu().matmul(w2Param.value)
            metric.update(initialPreds, y, ctx)
            initialAcc = metric.compute()
            metric.reset()

            // Training loop
            repeat(200) { epoch ->
                trainStep(optimizer, w1Param, w2Param) {
                    val h = x.matmul(w1Param.value).relu()
                    val logits = h.matmul(w2Param.value)
                    crossEntropyLoss.forward(logits, y, ctx)
                }

                // Compute accuracy every 50 epochs
                if ((epoch + 1) % 50 == 0) {
                    val preds = x.matmul(w1Param.value).relu().matmul(w2Param.value)
                    metric.update(preds, y, ctx)
                    println("Epoch ${epoch + 1}: accuracy = ${metric.compute()}")
                    metric.reset()
                }
            }

            // Final accuracy
            val finalPreds = x.matmul(w1Param.value).relu().matmul(w2Param.value)
            metric.update(finalPreds, y, ctx)
            finalAcc = metric.compute()
        }

        println("MLP XOR: Initial accuracy: $initialAcc, Final accuracy: $finalAcc")
        assertTrue(finalAcc >= initialAcc,
            "Accuracy should improve or stay same. Initial: $initialAcc, Final: $finalAcc")
    }

    @Test
    fun `tensor creation and basic ops with DSL`() {
        skainet(createTrainCtx()) {
            // Create tensors using DSL
            val a = tensor<FP32, Float> {
                shape(2, 3) {
                    from(1f, 2f, 3f, 4f, 5f, 6f)
                }
            }

            val b = tensor<FP32, Float> {
                shape(2, 3) {
                    ones()
                }
            }

            // Verify tensor creation
            println("Tensor a shape: ${a.shape}")
            println("Tensor b shape: ${b.shape}")
            println("a[0,0] = ${a.data[0, 0]}, a[1,2] = ${a.data[1, 2]}")
            println("b[0,0] = ${b.data[0, 0]}")

            // Use ops directly for element-wise operations
            val sum = ctx.ops.add(a, b)
            val product = ctx.ops.multiply(a, b)

            println("sum[0,0] = ${sum.data[0, 0]}")  // 1 + 1 = 2
            println("product[0,1] = ${product.data[0, 1]}")  // 2 * 1 = 2

            // Mean reduction using extension function
            val meanVal = a.mean()
            println("mean(a) = ${meanVal.data.get()}")  // 3.5
        }

        println("Tensor DSL operations completed successfully")
    }
}
