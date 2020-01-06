package io.kotest.extensions

import io.kotest.AbstractProjectConfig
import io.kotest.SpecClass
import io.kotest.core.TestCase

/**
 * Reusable spec extension to be registered project wide
 * using [AbstractProjectConfig.extensions] or on a
 * per-spec basis by overriding `extensions()` in a [SpecClass] class.
 */
interface SpecExtension : ProjectLevelExtension, SpecLevelExtension {

  /**
   * Intercepts execution of a [SpecClass].
   *
   * Implementations must invoke the process callback if they
   * wish this spec to be executed. If they want to skip
   * the tests in this spec they can elect not to invoke
   * the callback.
   *
   * Once the process function returns, the execution of this
   * [SpecClass] and all it's nested [TestCase]s are guaranteed
   * to have been completed.
   *
   * @param process callback function required to continue spec processing
   */
  suspend fun intercept(spec: SpecClass, process: suspend () -> Unit)
}
