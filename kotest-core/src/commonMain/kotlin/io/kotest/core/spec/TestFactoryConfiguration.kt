package io.kotest.core.spec

import io.kotest.core.Description
import io.kotest.SpecClass
import io.kotest.core.TestCase
import io.kotest.core.TestResult
import io.kotest.core.*
import io.kotest.core.specs.AbstractSpec
import io.kotest.core.specs.AutoCloseable
import io.kotest.core.specs.JsTest
import io.kotest.core.specs.generateTests
import io.kotest.extensions.SpecLevelExtension
import io.kotest.extensions.TestCaseExtension
import io.kotest.extensions.TestListener

typealias BeforeTest = (TestCase) -> Unit
typealias AfterTest = (TestCase, TestResult) -> Unit
typealias BeforeAll = () -> Unit
typealias AfterAll = () -> Unit

/**
 * A [TestFactory] is a generator of tests along with optional configuration and
 * callbacks related to those tests. A test factory can be added to a [Spec] and the
 * tests generated by the factory will be included in that Spec.
 */
data class TestFactory(
   val tests: List<DynamicTest>,
   val tags: Set<Tag>,
   val assertionMode: AssertionMode?,
   val listeners: List<TestListener>,
   val extensions: List<SpecLevelExtension>,
   val includes: List<TestFactory>
)

data class DynamicTest(
   val name: String,
   val test: suspend TestContext.() -> Unit,
   val config: TestCaseConfig,
   val type: TestType,
   val source: SourceRef
)

/**
 * A [Spec] is the unit of execution in Kotest. It contains one or more
 * [TestCase]s which are executed sequentially. All tests in a spec must
 * pass for the spec itself to be considered passing.
 *
 * Tests can either be root level, or nested inside other tests, depending
 * on the style of spec in use.
 *
 * Specs also contain [TestListener]s and [SpecLevelExtension]s which are used
 * to hook into the test lifecycle and interface with the test engine.
 *
 * A spec can define an [IsolationMode] used to control the instantiation of
 * classes for test cases in that spec.
 *
 * A spec can define the [TestCaseOrder] which controls the ordering of the
 * execution of root level tests in that spec.
 */
data class Spec(
   val rootTests: List<TestCase>,
   val listeners: List<TestListener>,
   val extensions: List<SpecLevelExtension>,
   val isolationMode: IsolationMode?,
   val testCaseOrder: TestCaseOrder?
)

/**
 * Generates a [TestCase] for each [DynamicTest] in this factory.
 * Tags and assertion mode are applied at this time.
 * Any included factories are recursively called and their generated
 * tests included in the returned list.
 *
 * @param description the parent description for the spec of the generated tests.
 * @param spec the spec that contains the generated tests
 */
fun TestFactory.generate(description: Description, spec: SpecClass): List<TestCase> {
   return tests.map { dyn ->
      TestCase(
         description = description.append(dyn.name),
         spec = spec,
         test = dyn.test,
         type = dyn.type,
         source = dyn.source,
         config = dyn.config.copy(tags = dyn.config.tags + this.tags),
         factory = this,
         assertionMode = this.assertionMode
      )
   } + includes.flatMap { it.generate(description, spec) }
}

/**
 * Builds an immutable [TestFactory] from this configuration.
 */
fun TestFactoryConfiguration.build(): TestFactory {

   val factory = TestFactory(
      tests = this.tests,
      tags = this.tags,
      listeners = this.listeners,
      extensions = this.extensions,
      assertionMode = this.assertionMode,
      includes = this.includes
   )

   val callbacks = object : TestListener {
      override fun beforeTest(testCase: TestCase) {
         if (testCase.factory == factory) {
            this@build.beforeTests.forEach { it(testCase) }
         }
      }

      override fun afterTest(testCase: TestCase, result: TestResult) {
         if (testCase.factory == factory) {
            this@build.afterTests.forEach { it(testCase, result) }
         }
      }

      override fun afterSpec(spec: SpecClass) {
         this@build.afterAlls.forEach { it() }
      }

      override fun beforeSpec(spec: SpecClass) {
         this@build.beforeAlls.forEach { it() }
      }
   }

   return factory.copy(listeners = listeners + callbacks)
}

/**
 * Builds an immutable [Spec] from this configuration.
 */
fun SpecConfiguration.build(): Spec {
   return Spec(
      rootTests = this.rootTestCases,
      listeners = this.listeners,
      extensions = this.extensions,
      isolationMode = this.isolationMode,
      testCaseOrder = this.testCaseOrder
   )
}

/**
 * The parent of all configuration DSL objects and contains configuration methods
 * common to both [SpecConfiguration] and [TestFactoryConfiguration] implementations.
 */
abstract class TestConfiguration {

   /**
    * Config applied to each test case if not overridden per test case.
    */
   var defaultTestCaseConfig: TestCaseConfig = TestCaseConfig()

   /**
    * Sets an assertion mode which is applied to every test.
    */
   internal var assertionMode: AssertionMode? = null

   /**
    * Contains the [Tag]s that will be applied to every test.
    */
   internal var tags: Set<Tag> = emptySet()

   /**
    * Contains the [TestFactory] instances that have been included with this config.
    */
   internal var includes = emptyList<TestFactory>()

   // test lifecycle callbacks
   internal var beforeTests = emptyList<BeforeTest>()
   internal var afterTests = emptyList<AfterTest>()
   internal var beforeAlls = emptyList<BeforeAll>()
   internal var afterAlls = emptyList<AfterAll>()

   // test listeners
   internal var listeners = emptyList<TestListener>()
   internal var extensions = emptyList<SpecLevelExtension>()

   /**
    * Registers a new before-test callback to be executed before every [TestCase] generated by
    * this [TestFactoryConfiguration]. The callback will only be executed for tests generated by this factory
    * and not other tests in a [Spec].
    */
   fun beforeTest(f: BeforeTest) {
      beforeTests = beforeTests + f
   }

   /**
    * Registers a new after-test callback to be executed after every [TestCase].
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.  The callback will only be executed
    * for tests generated by this factory and not other tests in a [Spec].
    */
   fun afterTest(f: AfterTest) {
      afterTests = afterTests + f
   }

   fun beforeAll(f: BeforeAll) {
      beforeAlls = beforeAlls + f
   }

   fun afterAll(f: AfterAll) {
      afterAlls = afterAlls + f
   }

   /**
    * Adds [Tag]s to this factory, which will be applied to each test case generated by
    * this [TestFactoryConfiguration]. When this factory is included in a [Spec], only the tests generated
    * from this factory will have these tags applied.
    */
   fun tags(vararg tags: Tag) {
      this.tags = this.tags + tags.toSet()
   }

   fun listeners(vararg listener: TestListener) {
      this.listeners = this.listeners + listener.toList()
   }

   fun extensions(vararg extensions: SpecLevelExtension) {
      this.extensions = this.extensions + extensions.toList()
   }

   /**
    * Include the tests from the given [TestFactory] in this configuration.
    */
   fun include(factory: TestFactory) {
      includes = includes + factory
   }

   /**
    * Register an [AutoCloseable] so that it's close methods is automatically invoked
    * when the tests are completed.
    */
   fun <T : AutoCloseable> autoClose(closeable: T): T {
      afterAll { closeable.close() }
      return closeable
   }
}

/**
 * A [TestFactoryConfiguration] provides a DSL to allow for easy creation of a
 * [TestFactory] when this class is the receiver of a lambda parameter.
 *
 * This class shouldn't be used directly, but as the base for a particular
 * layout style, eg [FunSpecTestFactoryConfiguration].
 */
abstract class TestFactoryConfiguration : TestConfiguration() {

   /**
    * Contains the [DynamicTest]s that have been added to this configuration.
    */
   internal var tests = emptyList<DynamicTest>()

   /**
    * Adds a new [DynamicTest] to this factory. When this factory is included into a [Spec]
    * these tests will be added to the spec as [TestCase]s.
    */
   protected fun addDynamicTest(
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) {
      require(tests.none { it.name == name }) { "Cannot add test with duplicate name $name" }
      this.tests = this.tests + DynamicTest(name, test, config, type, sourceRef())
   }
}

class FakeSpec : AbstractSpec()

abstract class SpecConfiguration : TestConfiguration() {

   /**
    * Contains the root [TestCase]s used in this spec.
    */
   internal var rootTestCases = emptyList<TestCase>()

   /**
    * Sets the [IsolationMode] used by the test engine when running tests in this spec.
    * If left null, then the project default is applied.
    */
   internal var isolationMode: IsolationMode? = null

   /**
    * Sets the [TestCaseOrder] to control the order of execution of root level tests in this spec.
    * If left null, then the project default is applied.
    */
   internal var testCaseOrder: TestCaseOrder? = null

   /**
    * This is a dummy method, intercepted by the kotlin.js framework adapter to generate tests.
    */
   @JsTest
   fun kotestGenerateTests() {
      generateTests(rootTestCases.toList())
   }

   private fun createTestCase(
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) = TestCase(
      Description.fromSpecClass(FakeSpec::class).append(name),
      FakeSpec(),
      test,
      sourceRef(),
      type,
      config,
      null,
      null
   )

   /**
    * Adds a new root-level [TestCase] to this [Spec].
    */
   protected fun addRootTestCase(
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) {
      require(rootTestCases.none { it.name == name }) { "Cannot add test with duplicate name $name" }
      //require(acceptingTopLevelRegistration) { "Cannot add nested test here. Please see documentation on testing styles for how to layout nested tests correctly" }
      rootTestCases = rootTestCases + createTestCase(name, test, config, type)
   }
}


