package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.KotestDsl
import io.kotest.core.spec.resolvedDefaultConfig
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createNestedTest
import io.kotest.core.test.createTestName
import kotlin.coroutines.CoroutineContext

@Deprecated("renamed to FeatureSpecContainerContext")
typealias FeatureScope = FeatureSpecContainerContext


/**
 * A scope that allows tests to be registered using the syntax:
 *
 * feature("some context")
 * xfeature("some disabled context")
 *
 * and
 *
 * scenario("some test")
 * scenario("some test").config(...)
 * xscenario("some test")
 * xscenario("some test").config(...)
 *
 */
@KotestDsl
class FeatureSpecContainerContext(
   val testContext: TestContext,
) : AbstractContainerContext(testContext) {

   override suspend fun registerTestCase(nested: NestedTest) = testContext.registerTestCase(nested)

   override suspend fun addTest(name: String, type: TestType, test: suspend TestContext.() -> Unit) {
      when (type) {
         TestType.Container -> feature(name, test)
         TestType.Test -> scenario(name, test)
      }
   }

   suspend fun feature(name: String, test: suspend FeatureSpecContainerContext.() -> Unit) {
      registerTestCase(
         createNestedTest(
            name = createTestName("Feature: ", name, false),
            xdisabled = false,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Container,
            descriptor = null,
            factoryId = testCase.factoryId
         ) { FeatureSpecContainerContext(this).test() }
      )
   }

   suspend fun xfeature(name: String, test: suspend FeatureSpecContainerContext.() -> Unit) {
      registerTestCase(
         createNestedTest(
            name = createTestName("Feature: ", name, false),
            xdisabled = true,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Test,
            descriptor = null,
            factoryId = testCase.factoryId
         ) { FeatureSpecContainerContext(this).test() }
      )
   }

   suspend fun scenario(name: String, test: suspend TestContext.() -> Unit) {
      registerTestCase(
         createNestedTest(
            name = createTestName("Scenario: ", name, false),
            xdisabled = false,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Test,
            descriptor = null,
            factoryId = null,
            test = test
         )
      )
   }

   suspend fun xscenario(name: String, test: suspend TestContext.() -> Unit) {
      registerTestCase(
         createNestedTest(
            name = createTestName("Scenario: ", name, false),
            xdisabled = true,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Container,
            descriptor = null,
            factoryId = null,
            test = test
         )
      )
   }

   suspend fun scenario(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.description.appendTest(name))
      return TestWithConfigBuilder(
         name = createTestName("Scenario: ", name, false),
         context = testContext,
         defaultTestConfig = testCase.spec.resolvedDefaultConfig(),
         xdisabled = false,
      )
   }

   suspend fun xscenario(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.description.appendTest(name))
      return TestWithConfigBuilder(
         name = createTestName("Scenario: ", name, false),
         context = testContext,
         defaultTestConfig = testCase.spec.resolvedDefaultConfig(),
         xdisabled = true,
      )
   }
}
