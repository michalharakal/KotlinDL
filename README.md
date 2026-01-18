# KotlinDL: High-level Deep Learning API in Kotlin [![official JetBrains project](http://jb.gg/badges/incubator.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)

[![Kotlin](https://img.shields.io/badge/kotlin-2.1.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Slack channel](https://img.shields.io/badge/chat-slack-green.svg?logo=slack)](https://kotlinlang.slack.com/messages/kotlindl/)

KotlinDL is a high-level Deep Learning API written in Kotlin and inspired by [Keras](https://keras.io).
Under the hood, it uses [SKaiNET](https://github.com/tribit-ai/skainet) as its tensor computation backend.
KotlinDL offers simple APIs for training deep learning models from scratch and leveraging transfer learning for tailoring existing pre-trained models to your tasks.

This project aims to make Deep Learning easier for JVM and Android developers and simplify deploying deep learning models in production environments.

Here's an example of training a simple neural network using the SKaiNET DSL:

```kotlin
import sk.ainet.lang.graph.dsl.skainet
import sk.ainet.lang.nn.loss.MSELoss
import sk.ainet.lang.nn.metrics.accuracy
import sk.ainet.lang.nn.optim.sgd
import sk.ainet.lang.tensor.dsl.tensor

fun main() {
    skainet {
        // Create training data
        val inputs = tensor(floatArrayOf(0f, 0f, 0f, 1f, 1f, 0f, 1f, 1f), shape = intArrayOf(4, 2))
        val targets = tensor(floatArrayOf(0f, 1f, 1f, 0f), shape = intArrayOf(4, 1))

        // Define model parameters
        val weights = tensor(floatArrayOf(0.1f, 0.2f), shape = intArrayOf(2, 1))
            .withRequiresGrad()
        val bias = tensor(floatArrayOf(0.0f), shape = intArrayOf(1))
            .withRequiresGrad()

        val optimizer = sgd(learningRate = 0.1f, parameters = listOf(weights, bias))
        val lossFunction = MSELoss()

        // Training loop
        repeat(100) { epoch ->
            trainStep {
                val predictions = inputs.matmul(weights) + bias
                val loss = lossFunction(predictions, targets)

                if (epoch % 10 == 0) {
                    println("Epoch $epoch, Loss: ${loss.item()}")
                }
            }
            optimizer.step()
        }
    }
}
```

## Table of Contents

- [Library Structure](#library-structure)
- [How to configure KotlinDL in your project](#how-to-configure-kotlindl-in-your-project)
  - [Working with KotlinDL in Android projects](#working-with-kotlindl-in-android-projects)
- [Requirements](#requirements)
- [Documentation](#documentation)
- [Examples and tutorials](#examples-and-tutorials)
- [Logging](#logging)
- [Contributing](#contributing)
- [Reporting issues/Support](#reporting-issuessupport)
- [Code of Conduct](#code-of-conduct)
- [License](#license)

## Library Structure

KotlinDL consists of several modules:
* `kotlin-deeplearning-api` - API interfaces and classes
* `kotlin-deeplearning-impl` - Implementation classes, utilities, and SKaiNET integration
* `kotlin-deeplearning-dataset` - Dataset classes and data loading utilities

All modules support both desktop JVM and Android platforms.

## How to configure KotlinDL in your project

To use KotlinDL in your project, ensure that `mavenCentral()` and `mavenLocal()` (for SKaiNET snapshots) are added to the repositories list:

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        mavenLocal()  // For SKaiNET snapshots
    }
}
```

Then add the necessary dependencies to your `build.gradle.kts` file:

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.jetbrains.kotlinx:kotlin-deeplearning-impl:[KOTLIN-DL-VERSION]")
    implementation("org.jetbrains.kotlinx:kotlin-deeplearning-dataset:[KOTLIN-DL-VERSION]")
}
```

Or using Gradle version catalog (`gradle/libs.versions.toml`):

```toml
[versions]
kotlindl = "[KOTLIN-DL-VERSION]"

[libraries]
kotlindl-impl = { module = "org.jetbrains.kotlinx:kotlin-deeplearning-impl", version.ref = "kotlindl" }
kotlindl-dataset = { module = "org.jetbrains.kotlinx:kotlin-deeplearning-dataset", version.ref = "kotlindl" }
```

```kotlin
// build.gradle.kts
dependencies {
    implementation(libs.kotlindl.impl)
    implementation(libs.kotlindl.dataset)
}
```

The latest KotlinDL version is `0.6.0`.

### Working with KotlinDL in Android projects

KotlinDL supports Android development with SKaiNET as the tensor computation backend.

To use KotlinDL in your Android project, add the following to your `build.gradle.kts`:

```kotlin
// build.gradle.kts
android {
    compileSdk = 34
    defaultConfig {
        minSdk = 26
        targetSdk = 34
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlin-deeplearning-impl:[KOTLIN-DL-VERSION]")
}
```

## Requirements

| KotlinDL Version | Kotlin Version | Minimum Java Version | Android: Compile SDK Version |
|------------------|----------------|----------------------|------------------------------|
| 0.6.0+           | 2.1.0          | 21                   | 34                           |
| 0.5.0-0.5.2      | 1.8.x          | 11                   | 31                           |

## Documentation

* [Change log for KotlinDL](CHANGELOG.md)
* [Full KotlinDL API reference](https://kotlin.github.io/kotlindl/)
* [SKaiNET Documentation](https://github.com/tribit-ai/skainet)

## Examples and tutorials

You do not need prior experience with Deep Learning to use KotlinDL.

We are working on including extensive documentation to help you get started.
At this point, please feel free to check out the following tutorials we have prepared:
- [Quick Start Guide](docs/quick_start_guide.md)
- [Creating your first neural network](docs/create_your_first_nn.md)
- [Transfer learning](docs/transfer_learning.md)

For more inspiration, take a look at the [code examples](examples) in this repository.

## Logging

By default, the API module uses the [kotlin-logging](https://github.com/MicroUtils/kotlin-logging) library to organize the logging process separately from the specific logger implementation.

You could use any widely known JVM logging library with a [Simple Logging Facade for Java (SLF4J)](http://www.slf4j.org/) implementation such as Logback or Log4j/Log4j2.

You will also need to add the following dependencies and configuration file `log4j2.xml` to the `src/resource` folder in your project if you wish to use log4j2:

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.apache.logging.log4j:log4j-api:2.17.2")
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.2")
}
```

```xml
<Configuration status="WARN">
    <Appenders>
        <Console name="STDOUT" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"/>
        </Console>
    </Appenders>

    <Loggers>
        <Root level="debug">
            <AppenderRef ref="STDOUT" level="DEBUG"/>
        </Root>
        <Logger name="io.jhdf" level="off" additivity="true">
            <appender-ref ref="STDOUT" />
        </Logger>
    </Loggers>
</Configuration>
```

If you wish to use Logback, include the following dependency and configuration file `logback.xml` to `src/resource` folder in your project:

```kotlin
// build.gradle.kts
dependencies {
    implementation("ch.qos.logback:logback-classic:1.4.5")
}
```

```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="info">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

## Contributing

Read the [Contributing Guidelines](https://github.com/Kotlin/kotlindl/blob/master/CONTRIBUTING.md).

## Reporting issues/Support

Please use [GitHub issues](https://github.com/Kotlin/kotlindl/issues) for filing feature requests and bug reports.
You are also welcome to join the [#kotlindl channel](https://kotlinlang.slack.com/messages/kotlindl/) in Kotlin Slack.

## Code of Conduct
This project and the corresponding community are governed by the [JetBrains Open Source and Community Code of Conduct](https://confluence.jetbrains.com/display/ALL/JetBrains+Open+Source+and+Community+Code+of+Conduct). Please make sure you read it.

## License
KotlinDL is licensed under the [Apache 2.0 License](LICENSE).
