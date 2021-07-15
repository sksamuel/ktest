package io.kotest.engine.extensions

import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.TestEngineListener

/**
 * Extension that intercepts calls to the engine.
 *
 * This extension can be used to execute code before or after the engine, change
 * the [TestEngineListener], or adapt the [TestSuite].
 *
 * Extensions of this type are designed to be used by third party addons but with a compelling
 * use case we could change that.
 */
internal interface EngineExtension {
   suspend fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: suspend (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult
}
