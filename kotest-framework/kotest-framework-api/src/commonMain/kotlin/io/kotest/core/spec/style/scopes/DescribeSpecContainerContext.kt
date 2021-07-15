package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.KotestDsl
import io.kotest.core.spec.resolvedDefaultConfig
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createNestedTest
import io.kotest.core.test.createTestName
import kotlin.coroutines.CoroutineContext

@Deprecated("This interface has been renamed to DescribeSpecContainerContext. This alias will be removed in 4.8")
typealias DescribeScope = DescribeSpecContainerContext

/**
 * A scope that allows tests to be registered using the syntax:
 *
 * describe("some test")
 *
 * or
 *
 * xdescribe("some disabled test")
 *
 * and
 *
 * it("some test")
 * it("some test").config(...)
 * xit("some test")
 * xit("some test").config(...)
 */
@KotestDsl
class DescribeSpecContainerContext(
   val testContext: TestContext,
) : AbstractContainerContext(testContext) {

   override suspend fun registerTestCase(nested: NestedTest) = testContext.registerTestCase(nested)

   override suspend fun addTest(name: String, type: TestType, test: suspend TestContext.() -> Unit) {
      when (type) {
         TestType.Container -> describe(name, test)
         TestType.Test -> it(name, test)
      }
   }

   suspend fun context(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      val testName = createTestName("Context: ", name, false)
      containerTest(testName, false, test)
   }

   @ExperimentalKotest
   fun context(name: String) =
      ContainerContextConfigBuilder(createTestName(name), this, false) { DescribeSpecContainerContext(it) }

   suspend fun xcontext(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      val testName = createTestName("Context: ", name, false)
      containerTest(testName, true, test)
   }

   @ExperimentalKotest
   fun xcontext(name: String) =
      ContainerContextConfigBuilder(createTestName("Context: ", name, false), this, true) { DescribeSpecContainerContext(it) }

   suspend fun describe(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      val testName = createTestName("Describe: ", name, false)
      containerTest(testName, false, test)
   }

   @ExperimentalKotest
   fun describe(name: String) =
      ContainerContextConfigBuilder(createTestName("Describe: ", name, false), this, false) { DescribeSpecContainerContext(it) }

   suspend fun xdescribe(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      val testName = createTestName("Describe: ", name, false)
      containerTest(testName, true, test)
   }

   @ExperimentalKotest
   fun xdescribe(name: String) =
      ContainerContextConfigBuilder(createTestName("Describe: ", name, false), this, true) { DescribeSpecContainerContext(it) }

   private suspend fun containerTest(
      testName: DescriptionName.TestName,
      xdisabled: Boolean,
      test: suspend DescribeSpecContainerContext.() -> Unit,
   ) {
      registerTestCase(
         createNestedTest(
            name = testName,
            xdisabled = xdisabled,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Container,
            descriptor = null,
            factoryId = testCase.factoryId
         ) { DescribeSpecContainerContext(this).test() }
      )
   }

   suspend fun it(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.description.appendTest(name))
      return TestWithConfigBuilder(
         createTestName("It: ", name, false),
         testContext,
         testCase.spec.resolvedDefaultConfig(),
         xdisabled = false,
      )
   }

   suspend fun xit(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.description.appendTest(name))
      return TestWithConfigBuilder(
         createTestName("It: ", name, false),
         testContext,
         testCase.spec.resolvedDefaultConfig(),
         xdisabled = true,
      )
   }

   suspend fun it(name: String, test: suspend TestContext.() -> Unit) =
      registerTestCase(
         createNestedTest(
            name = createTestName(name),
            xdisabled = false,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Test,
            descriptor = null,
            factoryId = testCase.factoryId
         ) { DescribeSpecContainerContext(this).test() }
      )

   suspend fun xit(name: String, test: suspend TestContext.() -> Unit) =
      registerTestCase(
         createNestedTest(
            name = createTestName(name),
            xdisabled = true,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Test,
            descriptor = null,
            factoryId = testCase.factoryId
         ) { DescribeSpecContainerContext(this).test() }
      )
}
