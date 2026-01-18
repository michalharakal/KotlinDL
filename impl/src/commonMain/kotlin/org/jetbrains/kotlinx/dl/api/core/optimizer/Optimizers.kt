/*
 * Copyright 2020-2024 JetBrains s.r.o. and Kotlin Deep Learning project contributors. All Rights Reserved.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

@file:Suppress("unused")

package org.jetbrains.kotlinx.dl.api.core.optimizer

import sk.ainet.lang.nn.optim.Optimizer as SkainetOptimizer

/**
 * Type alias for SKaiNET's Optimizer interface.
 *
 * Optimizers are used to update model parameters during training
 * based on computed gradients.
 */
public typealias Optimizer = SkainetOptimizer

/**
 * Optimizers for neural network training.
 *
 * This package provides access to optimization algorithms.
 * Use the optimizer functions from SKaiNET directly within a SkainetScope:
 *
 * Available optimizers (import from sk.ainet.lang.nn.optim):
 * - `sgd(lr)` - Stochastic Gradient Descent
 *
 * Example usage:
 * ```kotlin
 * import org.jetbrains.kotlinx.dl.api.core.dsl.deepLearning
 * import org.jetbrains.kotlinx.dl.api.core.tensor.FloatType
 * import sk.ainet.lang.nn.optim.sgd
 * import sk.ainet.lang.nn.topology.ModuleParameter
 * import sk.ainet.lang.tensor.dsl.tensor
 *
 * deepLearning {
 *     val w = tensor<FloatType, Float> {
 *         shape(2, 1) { from(0.1f, 0.2f) }
 *     }.withRequiresGrad()
 *     val wParam = ModuleParameter.WeightParameter("w", w)
 *
 *     val optimizer = sgd(lr = 0.01)
 *
 *     repeat(100) {
 *         trainStep(optimizer, wParam) {
 *             // Forward pass and loss computation
 *         }
 *     }
 * }
 * ```
 */

// Optimizer functions are available as scope functions in SKaiNET.
// Users should import them directly:
//
// import sk.ainet.lang.nn.optim.sgd
// import sk.ainet.lang.nn.optim.Optimizer
