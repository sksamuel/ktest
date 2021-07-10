package io.kotest.core.config

import io.kotest.common.ExperimentalKotest
import io.kotest.core.test.TestNameCase
import io.kotest.core.extensions.Extension
import io.kotest.core.filter.Filter
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.listeners.Listener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.test.DuplicateTestNameMode
import io.kotest.core.test.TestCaseConfig
import kotlin.reflect.KClass
import kotlin.time.Duration

/**
 * Project-wide configuration. Extensions returned by an
 * instance of this class will be applied to all [Spec] and [TestCase]s.
 *
 * Create an object that is derived from this class and place it in your classpath.
 * It will be detected at runtime and the options specified here will be used to set
 * global configuration.
 */
abstract class AbstractProjectConfig {

   /**
    * List of project wide [Extension] instances.
    */
   open fun extensions(): List<Extension> = emptyList()

   /**
    * List of project wide [Listener] instances.
    */
   open fun listeners(): List<Listener> = emptyList()

   /**
    * List of project wide [Filter] instances.
    */
   open fun filters(): List<Filter> = emptyList()

   /**
    * Override this function and return an instance of [SpecExecutionOrder] which will
    * be used to sort specs before execution.
    *
    * Implementations are currently:
    *  - [LexicographicSpecSorter]
    *  - [FailureFirstSpecExecutionOrder]
    *  - [RandomSpecExecutionOrder]
    *
    *  Note: This has no effect on non-JVM targets.
    */
   open val specExecutionOrder: SpecExecutionOrder? = null

   /**
    * The [IsolationMode] set here will be applied if the isolation mode in a spec is null.
    */
   open val isolationMode: IsolationMode? = null

   /**
    * A global timeout that is applied to all tests if not null.
    * Tests which define their own timeout will override this.
    */
   open val timeout: Duration? = null

   /**
    * A global invocation timeout that is applied to all tests if not null.
    * Tests which define their own timeout will override this.
    * The value here is in millis
    */
   open val invocationTimeout: Long? = null

   /**
    * A timeout that is applied to the overall project if not null,
    * if the sum duration of all the tests exceeds this the suite will fail.
    */
   open val projectTimeout: ProjectTimeout? = null

   /**
    * The parallelism factor determines how many threads are used to launch tests.
    *
    * The tests inside the same spec are always executed using the same thread, to ensure
    * that callbacks all operate on the same thread. In other words, a spec is sticky
    * with regards to the execution thread.
    *
    * Increasing this value to k > 1, means that k threads are created, allowing different
    * specs to execute on different threads. For n specs, if you set this value to k, then
    * on average, each thread will service n/k specs.
    *
    * The thread choosen for a particular thread can be determined by the ThreadAllocationExtension,
    * which by default chooses in a round robin fashion.
    *
    * An alternative way to enable this is the system property kotest.framework.parallelism
    * which will always (if defined) take priority over the value here.
    *
    * Note: For backwards compatibility, setting this value to > 1 will implicitly set
    * [specConcurrentDispatch] to true unless that value has been explicitly set to false.
    */
   open val parallelism: Int? = null

   /**
    * When set to true, failed specs are written to a file called spec_failures.
    * This file is used on subsequent test runs to run the failed specs first.
    *
    * To enable this feature, set this to true, or set the system property
    * 'kotest.write.specfailures=true'
    */
   open val writeSpecFailureFile: Boolean? = null

   /**
    * Sets the order of top level tests in a spec.
    * The value set here will be used unless overriden in a [Spec].
    * The value in a [Spec] is always taken in preference to the value here.
    * Nested tests will always be executed in discovery order.
    *
    * If this function returns null then the default of Sequential
    * will be used.
    */
   open val testCaseOrder: TestCaseOrder? = null

   /**
    * Override this value and set it to true if you want all tests to behave as if they
    * were operating in an [assertSoftly] block.
    */
   open val globalAssertSoftly: Boolean? = null

   /**
    * Override this value and set it to false if you want to disable autoscanning of extensions
    * and listeners.
    */
   open val autoScanEnabled: Boolean? = null

   /**
    * Override this value with a list of classes for which @autoscan is disabled.
    */
   open val autoScanIgnoredClasses: List<KClass<*>> = emptyList()

   /**
    * Override this value and set it to true if you want the build to be marked as failed
    * if there was one or more tests that were disabled/ignored.
    */
   open val failOnIgnoredTests: Boolean = false

   /**
    * Override this value and set it to true if you want the build to be marked as failed
    * if no tests were executed.
    */
   open val failOnEmptyTestSuite: Boolean? = null

   @ExperimentalKotest
   open val concurrentSpecs: Int? = null

   @ExperimentalKotest
   open val concurrentTests: Int? = null

   /**
    * Override this value to set a global [AssertionMode].
    * If a [Spec] sets an assertion mode, then the spec will override.
    */
   open val assertionMode: AssertionMode? = null

   /**
    * Any [TestCaseConfig] set here is used as the default for tests, unless overriden in a spec,
    * or in a test itself. In other words the order is test -> spec -> project config default -> kotest default
    */
   open val defaultTestCaseConfig: TestCaseConfig? = null

   /**
    * Some specs have DSLs that include "prefix" words in the test name.
    * For example, when using ExpectSpec like this:
    *
    * expect("this test 1") {
    *   feature("this test 2") {
    *   }
    * }
    *
    * Will result in:
    *
    * Expect: this test 1
    *   Feature: this test 2
    *
    * From 4.2, this feature can be disabled by setting this value to false.
    * Then the output of the previous test would be:
    *
    * this test 1
    *   this test 2
    */
   open val includeTestScopePrefixes: Boolean? = null

   /**
    * The casing of the tests' names can be adjusted using different strategies. It affects tests'
    * prefixes (I.e.: Given, When, Then) and tests' titles.
    *
    * This setting's options are defined in [TestNameCase]. Check the previous enum for the
    * available options and examples.
    */
   open val testNameCase: TestNameCase? = null

   open val testNameRemoveWhitespace: Boolean? = null

   open val testNameAppendTags: Boolean? = null

   /**
    * Controls what to do when a duplicated test name is discovered.
    * See possible settings in [DuplicateTestNameMode].
    */
   open val duplicateTestNameMode: DuplicateTestNameMode? = null

   /**
    * Executed before the first test of the project, but after the
    * [ProjectListener.beforeProject] methods.
    */
   open suspend fun beforeProject() {}

   /**
    * Executed before the first test of the project, but after the
    * [ProjectListener.beforeProject] methods.
    */
   @Deprecated(message = "use beforeProject", replaceWith = ReplaceWith("beforeProject"))
   open fun beforeAll() {}

   /**
    * Executed after the last test of the project, but before the
    * [ProjectListener.afterProject] methods.
    */
   open suspend fun afterProject() {}

   /**
    * Executed after the last test of the project, but before the
    * [ProjectListener.afterProject] methods.
    */
   @Deprecated(message = "use afterProject", replaceWith = ReplaceWith("afterProject"))
   open fun afterAll() {}
}
