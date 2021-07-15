package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.KotestDsl
import io.kotest.core.spec.resolvedDefaultConfig
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createNestedTest
import io.kotest.core.test.createTestName
import kotlin.coroutines.CoroutineContext

@Deprecated("This interface has been renamed to ShouldSpecContainerContext. This alias will be removed in 4.8")
typealias ShouldSpecContextScope = ShouldSpecContainerContext

/**
 * A scope that allows tests to be registered using the syntax:
 *
 * context("some context")
 * should("some test")
 * should("some test").config(...)
 *
 */
@KotestDsl
class ShouldSpecContainerContext(
   val testContext: TestContext,
) : AbstractContainerContext(testContext) {

   override suspend fun registerTestCase(nested: NestedTest) = testContext.registerTestCase(nested)

   override suspend fun addTest(name: String, type: TestType, test: suspend TestContext.() -> Unit) {
      when (type) {
         TestType.Container -> context(name, test)
         TestType.Test -> should(name, test)
      }
   }

   /**
    * Adds a nested context scope to this scope.
    */
   suspend fun context(name: String, test: suspend ShouldSpecContainerContext.() -> Unit) {
      registerTestCase(
         createNestedTest(
            name = createTestName(name),
            xdisabled = false,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Container,
            descriptor = null,
            factoryId = testCase.factoryId,
            test = { ShouldSpecContainerContext(this).test() }
         )
      )
   }

   @ExperimentalKotest
   fun context(name: String) = ContainerContextConfigBuilder(
      createTestName(name),
      testContext,
      false
   ) { ShouldSpecContainerContext(it) }

   /**
    * Adds a disabled nested context scope to this scope.
    */
   suspend fun xcontext(name: String, test: suspend ShouldSpecContainerContext.() -> Unit) {
      registerTestCase(
         createNestedTest(
            name = createTestName(name),
            xdisabled = true,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Container,
            descriptor = null,
            factoryId = testCase.factoryId,
            test = { ShouldSpecContainerContext(this).test() }
         )
      )
   }

   @ExperimentalKotest
   fun xcontext(name: String) = ContainerContextConfigBuilder(
      createTestName(name),
      testContext,
      true
   ) { ShouldSpecContainerContext(it) }

   suspend fun should(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.description.appendTest(name))
      return TestWithConfigBuilder(
         createTestName("should ", name, false),
         testContext,
         testCase.spec.resolvedDefaultConfig(),
         xdisabled = false,
      )
   }

   suspend fun xshould(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.description.appendTest(name))
      return TestWithConfigBuilder(
         createTestName("should ", name, false),
         testContext,
         testCase.spec.resolvedDefaultConfig(),
         xdisabled = true,
      )
   }

   suspend fun should(name: String, test: suspend TestContext.() -> Unit) =
      registerTestCase(
         createNestedTest(
            name = createTestName("should ", name, true),
            xdisabled = false,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Test,
            descriptor = null,
            factoryId = testCase.factoryId,
            test = { ShouldSpecContainerContext(this).test() }
         )
      )

   suspend fun xshould(name: String, test: suspend TestContext.() -> Unit) =
      registerTestCase(
         createNestedTest(
            name = createTestName("should ", name, true),
            xdisabled = true,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Test,
            descriptor = null,
            factoryId = testCase.factoryId,
            test = { ShouldSpecContainerContext(this).test() }
         )
      )
}
