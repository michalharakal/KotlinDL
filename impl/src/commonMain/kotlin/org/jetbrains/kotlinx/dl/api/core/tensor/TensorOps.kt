/*
 * Copyright 2020-2024 JetBrains s.r.o. and Kotlin Deep Learning project contributors. All Rights Reserved.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

@file:Suppress("unused")

package org.jetbrains.kotlinx.dl.api.core.tensor

// Re-export SKaiNET tensor types for convenience
import sk.ainet.lang.types.FP32 as SkainetFP32
import sk.ainet.lang.types.FP64 as SkainetFP64
import sk.ainet.lang.types.Int32 as SkainetInt32
import sk.ainet.lang.types.Int64 as SkainetInt64

/**
 * 32-bit floating point data type for tensors.
 */
public typealias FloatType = SkainetFP32

/**
 * 64-bit floating point data type for tensors.
 */
public typealias DoubleType = SkainetFP64

/**
 * 32-bit integer data type for tensors.
 */
public typealias IntType = SkainetInt32

/**
 * 64-bit integer data type for tensors.
 */
public typealias LongType = SkainetInt64

// Note: Tensor operations like matmul, mean, relu, etc. are used directly
// from sk.ainet.lang.tensor package via extension functions.
// Users can import them with KotlinDL-style aliases:
//
// import org.jetbrains.kotlinx.dl.api.core.tensor.FloatType
// import sk.ainet.lang.tensor.matmul
// import sk.ainet.lang.tensor.mean
// import sk.ainet.lang.tensor.relu
