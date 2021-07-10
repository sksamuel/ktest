package io.kotest.core.test

import io.kotest.common.ExperimentalKotest
import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.TestListener
import kotlin.time.Duration

typealias EnabledIf = suspend (TestCase) -> Boolean
typealias EnabledOrReasonIf = suspend (TestCase) -> Enabled

class Enabled private constructor(val isEnabled: Boolean, reason: String? = null) {
   private val builder = StringBuilder(reason ?: "")
   val reason get() = builder.trim().toString()

   companion object {
      val enabled = Enabled(true)
      val disabled = Enabled(false, null)
      fun disabled(reason: String) = Enabled(false, reason)

      fun fold(es: Iterable<Enabled>): Enabled {
         return es.fold(enabled) { acc, e ->
            Enabled(acc.isEnabled && e.isEnabled, acc.reason).also {
               if (!e.isEnabled) {
                  it.builder.appendLine()
                  it.builder.append(e.reason)
               }
            }
         }
      }
   }
}

data class TestCaseConfig(

   /**
    * If set to false, this test and any nested tests will be disabled.
    */
   val enabled: Boolean = true,
   val invocations: Int = 1,
   val threads: Int = 1,

   /**
    * The timeout for a test case and all it's invocations. For example, if this value was set to 800ms,
    * and invocations was 1 (which is the default and typical value), then that single invocation has
    * the full 800ms to complete. But if invocations was 2 for example, then the 800ms would apply to
    * the total time across both those invocations.
    *
    * To set a timeout per invocation see [invocationTimeout].
    */
   val timeout: Duration? = null,

   /**
    * This timeout applies to individual invocations of a test case. If invocations is 1, then this
    * has the same effect as timeout. To set a timeout across all invocations then see [timeout].
    */
   val invocationTimeout: Duration? = null,

   /**
    * [Tag]s that are applied to this test case, in addition to any tags declared on
    * the containing spec or parent tests.
    */
   val tags: Set<Tag> = emptySet(),
   val listeners: List<TestListener> = emptyList(),
   val extensions: List<TestCaseExtension> = emptyList(),

   /**
    * If this function evaluates to false, then this test and any nested tests will be disabled.
    */
   val enabledIf: EnabledIf = { true },
   val severity: TestCaseSeverityLevel? = null,
   val enabledOrReasonIf: EnabledOrReasonIf = { Enabled.enabled },
) {
   init {
      require(invocations > 0) { "Number of invocations must be greater than 0" }
      require(threads > 0) { "Number of threads must be greater than 0" }
      require(threads <= invocations) { "Number of threads must be <= number of invocations" }
   }
}

val xdisabledMessage = Enabled.disabled("Test was disabled using xdisabled")

/**
 * Returns a copy of this test config with the enabled flag set to false, if [xdisabled] is true.
 */
fun TestCaseConfig.withXDisabled(xdisabled: Boolean) = if (xdisabled) copy(enabledOrReasonIf = { xdisabledMessage }) else this


/**
 * Contains config that is applicable to container tests.
 */
@ExperimentalKotest
data class TestContainerConfig(

   /**
    * If set to false, this test and any nested tests will be disabled.
    */
   val enabled: Boolean = true,

   /**
    * If this function evaluates to false, then this test and any nested tests will be disabled.
    */
   val enabledIf: EnabledIf = { true },

   /**
    * If this function evaluates to false, then this test and any nested tests will be disabled.
    */
   val enabledOrReasonIf: EnabledOrReasonIf = { Enabled.enabled },

   /**
    * A timeout that applies to the total runtime of this test and any nested tests.
    *
    * Nested tests can set their own timeout value which will apply to their segment of the test tree.
    */
   val timeout: Duration? = null,

   /**
    * [Tag]s that are applied to this test case and nested tests.
    */
   val tags: Set<Tag> = emptySet(),
)

@ExperimentalKotest
fun TestCaseConfig.toTestContainerConfig() =
   TestContainerConfig(
      enabled = enabled,
      enabledIf = enabledIf,
      enabledOrReasonIf = enabledOrReasonIf,
      tags = tags,
      timeout = timeout
   )

@ExperimentalKotest
fun TestContainerConfig.toTestCaseConfig() =
   TestCaseConfig(
      enabled = this.enabled,
      enabledIf = this.enabledIf,
      enabledOrReasonIf = this.enabledOrReasonIf,
      tags = this.tags,
      timeout = this.timeout,
      invocationTimeout = null,
      listeners = emptyList(),
      extensions = emptyList()
   )
