package org.jetbrains.kotlinx.dl.impl.skainet

import org.junit.jupiter.api.Test
// KotlinDL wrapper imports
import org.jetbrains.kotlinx.dl.api.core.dsl.createTrainingContext
import org.jetbrains.kotlinx.dl.api.core.dsl.DeepLearningScope
import org.jetbrains.kotlinx.dl.api.core.dsl.GraphContext
import org.jetbrains.kotlinx.dl.api.core.loss.MeanSquaredError
import org.jetbrains.kotlinx.dl.api.core.loss.CrossEntropyLoss
import org.jetbrains.kotlinx.dl.api.core.metric.accuracy
import org.jetbrains.kotlinx.dl.api.core.tensor.FloatType
import org.jetbrains.kotlinx.dl.api.core.tensor.IntType
// SKaiNET imports for tensor DSL and operations
import sk.ainet.lang.graph.dsl.skainet
import sk.ainet.lang.nn.optim.sgd
import sk.ainet.lang.nn.topology.ModuleParameter
import sk.ainet.lang.tensor.dsl.tensor
import sk.ainet.lang.tensor.matmul
import sk.ainet.lang.tensor.mean
import sk.ainet.lang.tensor.relu
import kotlin.math.abs
import kotlin.test.assertTrue

/**
 * Test demonstrating SKaiNET DSL integration with KotlinDL wrappers.
 * Shows complete training flow including metrics using KotlinDL-style imports.
 */
class SkainetDslTrainingTest {

    @Test
    fun `simple linear regression training with DSL`() {
        var initialW = 0f
        var finalW = 0f

        skainet(createTrainingContext()) {
            // Create data using tensor DSL with KotlinDL type alias
            val x = tensor<FloatType, Float> {
                shape(4, 1) {
                    from(1f, 2f, 3f, 4f)
                }
            }
            val y = tensor<FloatType, Float> {
                shape(4, 1) {
                    from(2f, 4f, 6f, 8f)  // y = 2x
                }
            }

            // Model parameter: single weight
            val w = tensor<FloatType, Float> {
                shape(1, 1) { from(0.5f) }
            }.withRequiresGrad()
            val wParam = ModuleParameter.WeightParameter("w", w)

            val optimizer = sgd(lr = 0.01)
            val mseLoss = MeanSquaredError()  // Using KotlinDL alias
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

        skainet(createTrainingContext()) {
            // XOR problem data with KotlinDL type aliases
            val x = tensor<FloatType, Float> {
                shape(4, 2) {
                    from(
                        0f, 0f,
                        0f, 1f,
                        1f, 0f,
                        1f, 1f
                    )
                }
            }
            val y = tensor<IntType, Int> {
                shape(4) { from(0, 1, 1, 0) }
            }

            // Two-layer MLP parameters
            val w1 = tensor<FloatType, Float> {
                shape(2, 8) { randn(std = 0.5f) }
            }.withRequiresGrad()

            val w2 = tensor<FloatType, Float> {
                shape(8, 2) { randn(std = 0.5f) }
            }.withRequiresGrad()

            val w1Param = ModuleParameter.WeightParameter("w1", w1)
            val w2Param = ModuleParameter.WeightParameter("w2", w2)

            val optimizer = sgd(lr = 0.5)
            val crossEntropyLoss = CrossEntropyLoss()  // Using KotlinDL alias
            val metric = accuracy()  // Using KotlinDL wrapper

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
        skainet(createTrainingContext()) {
            // Create tensors using DSL with KotlinDL type alias
            val a = tensor<FloatType, Float> {
                shape(2, 3) {
                    from(1f, 2f, 3f, 4f, 5f, 6f)
                }
            }

            val b = tensor<FloatType, Float> {
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
