/*
 * Copyright 2020-2024 JetBrains s.r.o. and Kotlin Deep Learning project contributors. All Rights Reserved.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

package org.jetbrains.kotlinx.dl.api.core.loss

import sk.ainet.lang.nn.loss.CrossEntropyLoss as SkainetCrossEntropyLoss
import sk.ainet.lang.nn.loss.MSELoss as SkainetMSELoss

/**
 * Mean Squared Error loss function.
 *
 * Computes the mean of squares of errors between labels and predictions:
 * `loss = mean(square(y_true - y_pred))`
 *
 * Example:
 * ```kotlin
 * val loss = MeanSquaredError()
 * val lossValue = loss(predictions, targets)
 * ```
 */
public typealias MeanSquaredError = SkainetMSELoss

/**
 * Alias for [MeanSquaredError].
 */
public typealias MSE = SkainetMSELoss

/**
 * Cross-entropy loss function for classification tasks.
 *
 * Computes the cross-entropy loss between the labels and predictions.
 * Use this crossentropy loss function when there are two or more label classes.
 *
 * Example:
 * ```kotlin
 * val loss = CrossEntropyLoss()
 * val lossValue = loss(predictions, targets)
 * ```
 */
public typealias CrossEntropyLoss = SkainetCrossEntropyLoss

/**
 * Alias for [CrossEntropyLoss].
 */
public typealias SoftmaxCrossEntropy = SkainetCrossEntropyLoss

/**
 * Creates a Mean Squared Error loss function.
 *
 * @return A new MSE loss function instance
 */
public fun mse(): MeanSquaredError = MeanSquaredError()

/**
 * Creates a Cross-Entropy loss function.
 *
 * @return A new cross-entropy loss function instance
 */
public fun crossEntropy(): CrossEntropyLoss = CrossEntropyLoss()
