package io.kotest.engine.config

import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.DuplicateTestNameMode
import io.kotest.fp.Option
import io.kotest.fp.toOption
import io.kotest.mpp.sysprop

internal fun isolationMode(): Option<IsolationMode> =
   sysprop(KotestEngineProperties.isolationMode).toOption().map { IsolationMode.valueOf(it) }

internal fun assertionMode(): Option<AssertionMode> =
   sysprop(KotestEngineProperties.assertionMode).toOption().map { AssertionMode.valueOf(it) }

internal fun parallelism(): Option<Int> =
   sysprop(KotestEngineProperties.parallelism).toOption().map { it.toInt() }

internal fun timeout(): Option<Long> = sysprop(KotestEngineProperties.timeout).toOption().map { it.toLong() }

internal fun invocationTimeout(): Option<Long> =
   sysprop(KotestEngineProperties.invocationTimeout).toOption().map { it.toLong() }

internal fun allowMultilineTestName(): Option<Boolean> =
   sysprop(KotestEngineProperties.allowMultilineTestName).toOption().map { it.uppercase() == "TRUE" }

internal fun concurrentSpecs(): Option<Int> =
   sysprop(KotestEngineProperties.concurrentSpecs).toOption().map { it.toInt() }

internal fun concurrentTests(): Option<Int> =
   sysprop(KotestEngineProperties.concurrentTests).toOption().map { it.toInt() }

internal fun globalAssertSoftly(): Option<Boolean> =
   sysprop(KotestEngineProperties.globalAssertSoftly).toOption().map { it.uppercase() == "TRUE" }

internal fun testNameAppendTags(): Option<Boolean> =
   sysprop(KotestEngineProperties.testNameAppendTags).toOption().map { it.uppercase() == "TRUE" }

internal fun duplicateTestNameMode(): Option<DuplicateTestNameMode> =
   sysprop(KotestEngineProperties.testNameAppendTags).toOption().map { DuplicateTestNameMode.valueOf(it) }

internal fun projectTimeout(): Option<Long> =
   sysprop(KotestEngineProperties.projectTimeout).toOption().map { it.toLong() }

/**
 * Returns a [DetectedProjectConfig] which is built from system properties where supported.
 */
internal fun loadConfigFromSystemProperties(): DetectedProjectConfig {
   return DetectedProjectConfig(
      isolationMode = isolationMode(),
      assertionMode = assertionMode(),
      parallelism = parallelism(),
      concurrentTests = concurrentTests(),
      concurrentSpecs = concurrentSpecs(),
      timeout = timeout(),
      invocationTimeout = invocationTimeout(),
      testNameRemoveWhitespace = allowMultilineTestName(),
      globalAssertSoftly = globalAssertSoftly(),
      testNameAppendTags = testNameAppendTags(),
      duplicateTestNameMode = duplicateTestNameMode(),
      projectTimeout = projectTimeout(),
   )
}
