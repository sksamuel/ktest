package io.kotest.core.internal

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.configuration
import io.kotest.core.extensions.EnabledExtension
import io.kotest.core.extensions.resolvedExtensions
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.internal.tags.activeTags
import io.kotest.core.internal.tags.allTags
import io.kotest.core.internal.tags.isActive
import io.kotest.core.internal.tags.parse
import io.kotest.core.plan.toDescriptor
import io.kotest.core.spec.focusTests
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.isBang
import io.kotest.core.test.isFocused
import io.kotest.mpp.log
import io.kotest.mpp.sysprop

suspend fun TestCase.isActive(): Boolean = this.isEnabled().isEnabled

/**
 * Returns [Enabled.isEnabled] if the given [TestCase] is enabled based on default rules at [isEnabledInternal]
 * or any registered [EnabledExtension]s.
 */
@OptIn(ExperimentalKotest::class)
suspend fun TestCase.isEnabled(): Enabled {
   val descriptor = this.descriptor ?: this.description.toDescriptor(this.source)
   val internal = isEnabledInternal()
   return if (!internal.isEnabled) {
      internal
   } else {
      this.spec.resolvedExtensions()
         .filterIsInstance<EnabledExtension>().map { it.isEnabled(descriptor) }.let { Enabled.fold(it) }
   }
}

/**
 * Returns [Enabled.isEnabled] if the given [TestCase] is enabled by the built in rules.
 *
 * Logic can be customized via [EnabledExtension]s.
 *
 * A test can be enabled or disabled.
 *
 * A test is disabled if:
 *
 * - The `enabledOrReasonIf` function evaluates to [Enabled.disabled] in the [TestCaseConfig] associated with the test.
 * - The `enabled` property is set to false in the [TestCaseConfig] associated with the test.
 * - The `enabledIf` function evaluates to [false] in the [TestCaseConfig] associated with the test.
 * - The name of the test is prefixed with "!" and System.getProperty("kotest.bang.disable") has a null value (ie, not defined)
 * - Excluded tags have been specified and this test has a [Tag] which is one of those excluded
 * - Included tags have been specified and this test either has no tags, or does not have a tag that is one of those included
 * - The test is filtered out via a [TestFilter]
 *
 * Note: tags are defined either through [TestCaseConfig] or in the [Spec] dsl.
 */
suspend fun TestCase.isEnabledInternal(): Enabled {

   // this sys property disables the use of !
   // when it's not set, then we use ! to disable tests
   val bangEnabled = sysprop(KotestEngineProperties.disableBangPrefix) == null
   if (isBang() && bangEnabled) {
      return Enabled.disabled("${description.testPath()} is disabled by bang" ).also { log { it.reason } }
   }

   val enabledOrReasonIf = config.enabledOrReasonIf(this)
   if (!enabledOrReasonIf.isEnabled) {
      return enabledOrReasonIf
   }

   if (!config.enabled) {
      return Enabled.disabled("${description.testPath()} is disabled by enabled property in config")
         .also { log { it.reason } }
   }

   if (!config.enabledIf(this)) {
      return Enabled.disabled("${description.testPath()} is disabled by enabledIf function in config")
         .also { log { it.reason } }
   }

   val enabledInTags = configuration.activeTags().parse().isActive(this.allTags())
   if (!enabledInTags) {
      return Enabled.disabled("${description.testPath()} is disabled by tags").also { log { it.reason } }
   }

   val filters = configuration.filters().filterIsInstance<TestFilter>()
   val includedByFilters = filters.all {
      it.filter(this.description) == TestFilterResult.Include
   }
   if (!includedByFilters) {
      return Enabled.disabled("${description.testPath()} is excluded by test case filters (${filters.size} filters found)")
         .also { log { it.reason } }
   }

   // if the spec has focused tests, and this test is root and *not* focused, then it's not active
   val specHasFocusedTests = spec.focusTests().isNotEmpty()
   if (description.isRootTest() && !isFocused() && specHasFocusedTests) {
      return Enabled.disabled("${description.testPath()} is disabled by another test having focus")
         .also { log { it.reason } }
   }

   // if we have the severityLevel lower then in the sysprop -> we ignore this case
   val severityEnabled = config.severity?.let { TestCaseSeverityLevel.valueOf(it.name).isEnabled() }
      ?: TestCaseSeverityLevel.valueOf("NORMAL").isEnabled()
   if (!severityEnabled) {
      return Enabled.disabled("${description.testPath()} is disabled by severityLevel").also { log { it.reason } }
   }

   return Enabled.enabled
}
