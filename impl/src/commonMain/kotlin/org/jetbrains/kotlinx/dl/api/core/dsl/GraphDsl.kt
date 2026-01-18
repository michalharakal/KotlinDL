/*
 * Copyright 2020-2024 JetBrains s.r.o. and Kotlin Deep Learning project contributors. All Rights Reserved.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

@file:Suppress("unused")

package org.jetbrains.kotlinx.dl.api.core.dsl

import sk.ainet.context.Phase
import sk.ainet.exec.tensor.ops.DefaultCpuOps
import sk.ainet.lang.graph.DefaultGradientTape
import sk.ainet.lang.graph.DefaultGraphExecutionContext
import sk.ainet.lang.graph.dsl.SkainetScope
import sk.ainet.lang.graph.dsl.skainet
import sk.ainet.lang.tensor.data.DenseTensorDataFactory

/**
 * Type alias for SKaiNET's SkainetScope - the main DSL scope for tensor computations.
 */
public typealias DeepLearningScope = SkainetScope

/**
 * Type alias for SKaiNET's graph execution context.
 */
public typealias GraphContext = DefaultGraphExecutionContext

/**
 * Type alias for SKaiNET's gradient tape for automatic differentiation.
 */
public typealias GradientTape = DefaultGradientTape

/**
 * Creates a training context for model training.
 *
 * @return A configured graph execution context for training
 */
public fun createTrainingContext(): GraphContext {
    val dataFactory = DenseTensorDataFactory()
    val cpuOps = DefaultCpuOps(dataFactory)
    return DefaultGraphExecutionContext(
        baseOps = cpuOps,
        phase = Phase.TRAIN,
        tensorDataFactory = dataFactory,
        createTapeFactory = { _ -> DefaultGradientTape(true) }
    )
}

/**
 * Creates an inference context for model evaluation/prediction.
 *
 * @return A configured graph execution context for inference
 */
public fun createInferenceContext(): GraphContext {
    val dataFactory = DenseTensorDataFactory()
    val cpuOps = DefaultCpuOps(dataFactory)
    return DefaultGraphExecutionContext(
        baseOps = cpuOps,
        phase = Phase.EVAL,
        tensorDataFactory = dataFactory,
        createTapeFactory = { _ -> DefaultGradientTape(false) }
    )
}

/**
 * Creates a deep learning computation scope with a training context.
 *
 * This is the main entry point for KotlinDL's tensor computation DSL.
 * All tensor operations, model definitions, and training loops should be
 * defined within this scope.
 *
 * Example:
 * ```kotlin
 * import org.jetbrains.kotlinx.dl.api.core.dsl.deepLearning
 * import org.jetbrains.kotlinx.dl.api.core.tensor.FloatType
 * import sk.ainet.lang.tensor.dsl.tensor
 *
 * deepLearning {
 *     val x = tensor<FloatType, Float> {
 *         shape(3) { from(1f, 2f, 3f) }
 *     }
 *     println(x.data.toArray().contentToString())
 * }
 * ```
 *
 * @param block The DSL block containing tensor computations
 */
public fun deepLearning(block: DeepLearningScope.() -> Unit): Unit =
    skainet(createTrainingContext(), block)

/**
 * Alternative short name for [deepLearning].
 *
 * @param block The DSL block containing tensor computations
 */
public fun dl(block: DeepLearningScope.() -> Unit): Unit =
    skainet(createTrainingContext(), block)

/**
 * Creates a deep learning computation scope with a custom context.
 *
 * @param ctx The execution context to use
 * @param block The DSL block containing tensor computations
 */
public fun deepLearning(ctx: GraphContext, block: DeepLearningScope.() -> Unit): Unit =
    skainet(ctx, block)

/**
 * Alternative short name for [deepLearning] with custom context.
 *
 * @param ctx The execution context to use
 * @param block The DSL block containing tensor computations
 */
public fun dl(ctx: GraphContext, block: DeepLearningScope.() -> Unit): Unit =
    skainet(ctx, block)
