/*
 * Copyright 2020-2024 JetBrains s.r.o. and Kotlin Deep Learning project contributors. All Rights Reserved.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

@file:Suppress("unused")

package org.jetbrains.kotlinx.dl.api.core.metric

import sk.ainet.lang.nn.metrics.Metric as SkainetMetric
import sk.ainet.lang.nn.metrics.accuracy as skainetAccuracy
import sk.ainet.lang.nn.metrics.binaryAccuracy as skainetBinaryAccuracy

/**
 * Type alias for SKaiNET's Metric interface.
 */
public typealias Metric = SkainetMetric

/**
 * Creates an accuracy metric for multi-class classification.
 *
 * Calculates how often predictions match labels.
 *
 * Example:
 * ```kotlin
 * deepLearning {
 *     val metric = accuracy()
 *     metric.update(predictions, targets, ctx)
 *     println("Accuracy: ${metric.compute()}")
 * }
 * ```
 *
 * @return A new accuracy metric instance
 */
public fun accuracy(): Metric = skainetAccuracy()

/**
 * Creates a binary accuracy metric.
 *
 * Calculates how often predictions match labels for binary classification.
 *
 * @return A new binary accuracy metric instance
 */
public fun binaryAccuracy(): Metric = skainetBinaryAccuracy()
