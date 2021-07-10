@file:Suppress("MemberVisibilityCanBePrivate")

package io.kotest.core.config

import io.kotest.common.ExperimentalKotest
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.filter.Filter
import io.kotest.core.listeners.Listener
import io.kotest.core.listeners.SpecInstantiationListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.DuplicateTestNameMode
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestNameCase
import kotlinx.coroutines.CoroutineDispatcher

/**
 * The global configuration singleton.
 */
val configuration = Configuration()

/**
 * This class defines project wide settings that are used when executing tests.
 *
 * Some settings here are fallback values. That is, a setting specified in a Spec or Test
 * will override the value here.
 */
class Configuration {

   companion object {
      @ExperimentalKotest
      const val Sequential = 1

      @ExperimentalKotest
      const val MaxConcurrency = Int.MAX_VALUE
   }

   private val listeners = mutableListOf<Listener>()
   private val filters = mutableListOf<Filter>()
   private val extensions = mutableListOf<Extension>()

   /**
    * If enabled, then all failing spec names will be written to a "failure file".
    * This file can then be used by [SpecExecutionOrder.FailureFirst].
    *
    * Defaults to [Defaults.writeSpecFailureFile]
    */
   var writeSpecFailureFile: Boolean = Defaults.writeSpecFailureFile

   /**
    * The path to write the failed spec list to, if enabled.
    */
   var specFailureFilePath: String = Defaults.specFailureFilePath

   /**
    * If true, then all test cases are implicitly wrapped in an assertSoftly call.
    *
    * Defaults to [Defaults.globalAssertSoftly].
    */
   var globalAssertSoftly: Boolean = Defaults.globalAssertSoftly

   /**
    * The casing of the tests' names can be adjusted using different strategies. It affects tests'
    * prefixes (I.e.: Given, When, Then) and tests' titles.
    *
    * This setting's options are defined in [TestNameCase].
    *
    * Defaults to [Defaults.defaultTestNameCase]
    */
   var testNameCase: TestNameCase = Defaults.defaultTestNameCase

   /**
    * If true, then the test execution will fail if any test is set to ignore.
    * If false, then ignored tests are outputted as normal.
    *
    * Defaults to [Defaults.failOnIgnoredTests].
    */
   var failOnIgnoredTests: Boolean = Defaults.failOnIgnoredTests

   /**
    * Returns the default assertion mode.
    */
   var assertionMode: AssertionMode = Defaults.assertionMode

   /**
    * The parallelism factor determines how many threads are used to execute specs and tests.
    *
    * By default a single threaded [CoroutineDispatcher] is used for all tests.
    *
    * Increasing this value to k > 1, means that k dispatchers are created, allowing different
    * specs to execute on different dispatchers (each backed by a separate thread).
    *
    * By default, all tests inside a single spec are executed using the same dispatcher to ensure
    * that callbacks all operate on the same thread. In other words, a spec is sticky with regards
    * to the execution thread. To change that, specify [dispatcherAffinity] globally or per spec.
    *
    * An alternative way to set this value is via system property kotest.framework.parallelism
    * which will always (if defined) take priority over the value here.
    *
    * Note: For backwards compatibility, setting this value to k will implicitly set
    * [Configuration.concurrentSpecs] to the k unless that value has also been set.
    *
    * Defaults to [Defaults.parallelism].
    */
   var parallelism: Int = Defaults.parallelism

   /**
    * By default, all tests inside a single spec are executed using the same dispatcher to ensure
    * that callbacks all operate on the same thread. In other words, a spec is sticky with regards
    * to the execution thread. To change this, set this value to false.
    *
    * When this value is false, the framework is free to assign different dispatchers to different
    * root tests (nested tests always run in the same thread as their parent test).
    *
    * Note: Setting this value alone will not increase the number of threads used. For that,
    * see [Configuration.parallelism].
    *
    * Defaults to [Defaults.dispatcherAffinity].
    */
   @ExperimentalKotest
   var dispatcherAffinity: Boolean = Defaults.dispatcherAffinity

   /**
    * Each spec is launched into its own coroutine. By default, the test engine waits for that
    * coroutine to finish before launching the next spec. By setting [Configuration.concurrentSpecs]
    * to a value higher than 1, multiple specs will be launched at the same time.
    *
    * For example, setting this value to 5 will result in 5 specs running concurrently. Once
    * one of those specs completes, another will be launched (if any are remaining), and so on.
    *
    * Setting this value to [Configuration.MaxConcurrency] will result in all specs being launched together.
    *
    * The maximum number of coroutines that can be launched at any time is given
    * by [Configuration.concurrentSpecs] * [Configuration.concurrentTests].
    *
    * Tests inside each spec will continue to be launched sequentially. To change that
    * see [Configuration.concurrentTests].
    *
    * Note: This value does not change the number of threads used by the test engine. If a test uses a
    * blocking method, then that thread cannot be utilized by another coroutine while the thread is
    * blocked. See [Configuration.parallelism].
    *
    * Note: This setting can be > 1 and specs can still choose to "opt out" by using the
    * [Isolate] annotation. That annotation ensures that a spec never runs concurrently
    * with any other regardless of the setting here.
    */
   @ExperimentalKotest
   var concurrentSpecs: Int? = null

   /**
    * Each root test is launched into its own coroutine. By default, the test engine waits
    * for that coroutine to finish before launching the next test of the same spec. By setting
    * [Configuration.concurrentTests] to a value higher than 1, multiple tests will be launched
    * at the same time.
    *
    * For example, setting this value to 5 will result in at most 5 tests running concurrently per spec.
    * Once one of those tests completes, another will be launched (if there are further tests in the spec),
    * and so on.
    *
    * Setting this value to [Configuration.MaxConcurrency] will result in all tests of a spec being
    * launched together when the spec is instantiated
    *
    * Setting this value will result in tests inside a spec executing concurrently, but will not change
    * how many specs are launched concurrently. For that, see [Configuration.concurrentSpecs].
    *
    * The maximum number of coroutines that can be launched at any time is given
    * by [Configuration.concurrentSpecs] * [Configuration.concurrentTests].
    *
    * Note: This value does not change the number of threads used by the test engine. If a test uses a
    * blocking method, then that thread cannot be utilized by another coroutine while the thread is
    * blocked. See [Configuration.parallelism].
    */
   @ExperimentalKotest
   var concurrentTests: Int = Defaults.concurrentTests

   /**
    * Returns the timeout for the execution of a test case.
    * Note: This timeout includes the time required to executed nested tests.
    *
    * This value is used if a timeout is not specified in the test case itself.
    *
    * Defaults to [Defaults.defaultTimeoutInMillis].
    */
   var timeout: Long = Defaults.defaultTimeoutInMillis

   /**
    * Returns the timeout for any single invocation of a test.
    *
    * This value is used if a timeout is not specified in the test case itself.
    *
    * Defaults to [Defaults.defaultInvocationTimeoutInMillis].
    */
   var invocationTimeout: Long = Defaults.defaultInvocationTimeoutInMillis

   /**
    * A timeout that is applied to the overall project if not null,
    * if the sum duration of all the tests exceeds this the suite will fail.
    * TODO: make this a [kotlin.time.Duration] when that API stabilizes
    */
   var projectTimeout: Long = Long.MAX_VALUE

   /**
    * Returns the default [TestCaseConfig] to be assigned to tests when not specified either in
    * the spec, test factory, or test case itself.
    *
    * If this is null, then defaults to [Defaults.testCaseConfig]
    */
   var defaultTestConfig: TestCaseConfig = Defaults.testCaseConfig

   /**
    * If set to true, then will cause the test suite to fail if there were no executed tests.
    */
   var failOnEmptyTestSuite: Boolean = Defaults.failOnEmptyTestSuite

   /**
    * Some specs have DSLs that include prefix or suffix words in the test name.
    *
    * If this method returns true, then test names include those prefix and suffix names
    * in reports and the IDE.
    *
    * For example, when using ExpectSpec like this:
    *
    * expect("this test 1") {
    *   feature("this test 2") {
    *   }
    * }
    *
    * If prefixes are enabled, the output would be:
    *
    * Expect: this test 1
    *   Feature: this test 2
    *
    * And if disabled, the output would be:
    *
    * this test 1
    *    test this 2
    *
    * Defaults to null, which is to use the default.
    */
   var includeTestScopeAffixes: Boolean? = Defaults.defaultIncludeTestScopeAffixes

   /**
    * Controls the default [IsolationMode] that each spec will execute in.
    *
    * This value is used if a value is not specified in the spec itself.
    *
    * Default to [Defaults.isolationMode]
    */
   var isolationMode: IsolationMode = Defaults.isolationMode

   /**
    * Controls the ordering of root test cases in each spec.
    *
    * Valid options are the enum values of [TestCaseOrder].
    *
    * This value is used if a value is not specified in the spec itself.
    *
    * Defaults to [Defaults.testCaseOrder]
    */
   var testCaseOrder: TestCaseOrder = Defaults.testCaseOrder

   /**
    * Returns the sort order to use when executing specs.
    *
    * Defaults to [Defaults.specExecutionOrder]
    */
   var specExecutionOrder: SpecExecutionOrder = Defaults.specExecutionOrder

   var removeTestNameWhitespace: Boolean = false

   var testNameAppendTags: Boolean = false

   /**
    * Controls what to do when a duplicated test name is discovered.
    * See possible settings in [DuplicateTestNameMode].
    *
    * Defaults to [Defaults.duplicateTestNameMode]
    */
   var duplicateTestNameMode: DuplicateTestNameMode = Defaults.duplicateTestNameMode

   /**
    * Returns all globally registered [Listener]s.
    */
   fun listeners() = listeners.toList()

   /**
    * Returns all globally registered [Extension]s.
    */
   fun extensions() = extensions.toList()

   /**
    * Returns all globally registered [Filter]s.
    */
   fun filters() = filters.toList()

   fun registerFilters(vararg filters: Filter) = filters.forEach { registerFilter(it) }
   fun registerFilters(filters: Collection<Filter>) = filters.forEach { registerFilter(it) }
   fun deregisterFilters(filters: Collection<Filter>) = filters.forEach { deregisterFilter(it) }

   fun registerFilter(filter: Filter) {
      filters.add(filter)
   }

   fun deregisterFilter(filter: Filter) {
      filters.remove(filter)
   }

   fun registerExtensions(vararg extensions: Extension) = extensions.forEach { registerExtension(it) }
   fun registerExtensions(extensions: List<Extension>) = extensions.forEach { registerExtension(it) }
   fun deregisterExtensions(extensions: List<Extension>) = extensions.forEach { deregisterExtension(it) }

   fun registerExtension(extension: Extension) {
      extensions.add(extension)
   }

   fun deregisterExtension(extension: Extension) {
      extensions.remove(extension)
   }

   fun registerListeners(vararg listeners: Listener) = listeners.forEach { registerListener(it) }
   fun registerListeners(listeners: List<Listener>) = listeners.forEach { registerListener(it) }
   fun deregisterListeners(listeners: List<Listener>) = listeners.forEach { deregisterListener(it) }

   fun registerListener(listener: Listener) {
      listeners.add(listener)
   }

   fun deregisterListener(listener: Listener) {
      listeners.remove(listener)
   }

   fun removeListeners() {
      listeners.clear()
   }

   fun removeExtensions() {
      extensions.clear()
   }

   fun removeFilters() {
      filters.clear()
   }
}

fun Configuration.testListeners(): List<TestListener> = listeners().filterIsInstance<TestListener>()
fun Configuration.testCaseExtensions(): List<TestCaseExtension> = listeners().filterIsInstance<TestCaseExtension>()
fun Configuration.specInstantiationListeners(): List<SpecInstantiationListener> =
   listeners().filterIsInstance<SpecInstantiationListener>()

