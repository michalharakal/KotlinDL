/*
 * Copyright 2020-2024 JetBrains s.r.o. and Kotlin Deep Learning project contributors. All Rights Reserved.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

@file:Suppress("unused")

package org.jetbrains.kotlinx.dl.api.core.activation

/**
 * Activation functions for neural networks.
 *
 * This package provides access to common activation functions.
 * Activation functions are applied element-wise to tensors and introduce
 * non-linearity into neural network models.
 *
 * Available activations (import from sk.ainet.lang.tensor):
 * - `relu` - Rectified Linear Unit: max(0, x)
 * - `sigmoid` - Sigmoid: 1 / (1 + exp(-x))
 * - `tanh` - Hyperbolic tangent
 * - `softmax` - Softmax for classification
 *
 * Example usage:
 * ```kotlin
 * import org.jetbrains.kotlinx.dl.api.core.dsl.deepLearning
 * import org.jetbrains.kotlinx.dl.api.core.tensor.FloatType
 * import sk.ainet.lang.tensor.relu
 * import sk.ainet.lang.tensor.dsl.tensor
 *
 * deepLearning {
 *     val x = tensor<FloatType, Float> {
 *         shape(2, 3) { from(-1f, 0f, 1f, -2f, 2f, 3f) }
 *     }
 *     val activated = x.relu()  // Applies ReLU: [-1,0,1,-2,2,3] -> [0,0,1,0,2,3]
 * }
 * ```
 */

// Activation functions are extension functions on Tensor in SKaiNET.
// Users should import them directly from sk.ainet.lang.tensor:
//
// import sk.ainet.lang.tensor.relu
// import sk.ainet.lang.tensor.sigmoid
// import sk.ainet.lang.tensor.tanh
// import sk.ainet.lang.tensor.softmax
