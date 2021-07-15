package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.KotestDsl
import io.kotest.core.spec.resolvedDefaultConfig
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createNestedTest
import io.kotest.core.test.createTestName

@Deprecated("This interface has been renamed to BehaviorSpecGivenContainerContext. This alias will be removed in 4.8")
typealias GivenScope = BehaviorSpecGivenContainerContext

/**
 * A context that allows tests to be registered using the syntax:
 *
 * when("some test")
 * when("some test").config(...)
 * xwhen("some disabled test")
 * xwhen("some disabled test").config(...)
 *
 * and
 *
 * then("some test")
 * then("some test").config(...)
 * xthen("some disabled test").config(...)
 * xthen("some disabled test").config(...)
 *
 */
@Suppress("FunctionName")
@KotestDsl
class BehaviorSpecGivenContainerContext(
   val testContext: TestContext,
) : AbstractContainerContext(testContext) {

   override suspend fun registerTestCase(nested: NestedTest) = testContext.registerTestCase(nested)

   override suspend fun addTest(name: String, type: TestType, test: suspend TestContext.() -> Unit) {
      when (type) {
         TestType.Container -> `when`(name, test)
         TestType.Test -> then(name, test)
      }
   }

   suspend fun And(name: String, test: suspend BehaviorSpecGivenContainerContext.() -> Unit) =
      addAnd(name, test, xdisabled = false)

   suspend fun and(name: String, test: suspend BehaviorSpecGivenContainerContext.() -> Unit) =
      addAnd(name, test, xdisabled = false)

   suspend fun xand(name: String, test: suspend BehaviorSpecGivenContainerContext.() -> Unit) =
      addAnd(name, test, xdisabled = true)

   suspend fun xAnd(name: String, test: suspend BehaviorSpecGivenContainerContext.() -> Unit) =
      addAnd(name, test, xdisabled = true)

   private suspend fun addAnd(
      name: String,
      test: suspend BehaviorSpecGivenContainerContext.() -> Unit,
      xdisabled: Boolean
   ) {
      registerTestCase(
         createNestedTest(
            name = createTestName("And: ", name, true),
            xdisabled = xdisabled,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Container,
            descriptor = null,
            factoryId = testCase.factoryId,
            test = { BehaviorSpecGivenContainerContext(this).test() }
         )
      )
   }

   suspend fun When(name: String, test: suspend BehaviorSpecWhenContainerContext.() -> Unit) = addWhen(name, test, xdisabled = false)
   suspend fun `when`(name: String, test: suspend BehaviorSpecWhenContainerContext.() -> Unit) = addWhen(name, test, xdisabled = false)
   suspend fun xwhen(name: String, test: suspend BehaviorSpecWhenContainerContext.() -> Unit) = addWhen(name, test, xdisabled = true)
   suspend fun xWhen(name: String, test: suspend BehaviorSpecWhenContainerContext.() -> Unit) = addWhen(name, test, xdisabled = true)

   private suspend fun addWhen(name: String, test: suspend BehaviorSpecWhenContainerContext.() -> Unit, xdisabled: Boolean) {
      registerTestCase(
         createNestedTest(
            name = createTestName("When: ", name, true),
            xdisabled = xdisabled,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Container,
            descriptor = null,
            factoryId = null,
            test = { BehaviorSpecWhenContainerContext(this).test() }
         )
      )
   }

   fun Then(name: String) = TestWithConfigBuilder(
      createTestName("Then: ", name, true),
      testContext,
      testCase.spec.resolvedDefaultConfig(),
      xdisabled = false
   )

   fun then(name: String) = TestWithConfigBuilder(
      createTestName("Then: ", name, true),
      testContext,
      testCase.spec.resolvedDefaultConfig(),
      xdisabled = false
   )

   fun xthen(name: String) = TestWithConfigBuilder(
      createTestName("Then: ", name, true),
      testContext,
      testCase.spec.resolvedDefaultConfig(),
      xdisabled = true
   )

   fun xThen(name: String) = TestWithConfigBuilder(
      createTestName("Then: ", name, true),
      testContext,
      testCase.spec.resolvedDefaultConfig(),
      xdisabled = true
   )

   suspend fun Then(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = false)
   suspend fun then(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = false)
   suspend fun xthen(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = true)
   suspend fun xThen(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = true)

   private suspend fun addThen(name: String, test: suspend TestContext.() -> Unit, xdisabled: Boolean) {
      registerTestCase(
         createNestedTest(
            name = createTestName("Then: ", name, true),
            xdisabled = xdisabled,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Test,
            descriptor = null,
            factoryId = testCase.factoryId,
            test = { BehaviorSpecWhenContainerContext(this).test() }
         )
      )
   }
}
