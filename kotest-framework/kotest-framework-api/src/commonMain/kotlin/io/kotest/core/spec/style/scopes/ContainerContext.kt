package io.kotest.core.spec.style.scopes

import io.kotest.core.Tuple2
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.*
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import kotlin.coroutines.CoroutineContext

@Deprecated("Use ContainerContext. This alias will be removed in 4.8")
typealias ContainerScope = ContainerContext

/**
 * Extends a [TestContext] with methods used by test containers.
 */
interface ContainerContext : TestContext {

   /**
    * Registers a test on this scope, wrapping the test in the most appropriate scope for the
    * given test type.
    *
    * For example, invoking this method on a [DescribeSpec] with a test type of Container, will
    * add a `describe` block, and invoking with a test type of Test will add an `it` block.
    */
   suspend fun addTest(
      name: String,
      type: TestType,
      test: suspend TestContext.() -> Unit,
   )

   private fun addListener(listener: TestListener) {
      testCase.spec.listener(listener)
   }

   /**
    * Registers a [BeforeTest] function that executes before every test in this context.
    * Only affects tests registered after a call to this function.
    */
   fun beforeTest(f: BeforeTest) {
      val thisTestCase = this.testCase
      addListener(object : TestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            if (thisTestCase.description.isAncestorOf(testCase.description)) f(testCase)
         }
      })
   }

   /**
    * Registers an [AfterTest] function that executes after every test in this context.
    * Only affects tests registered after a call to this function.
    */
   fun afterTest(f: AfterTest) {
      val thisTestCase = this.testCase
      addListener(object : TestListener {
         override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            if (thisTestCase.description.isAncestorOf(testCase.description)) f(Tuple2(testCase, result))
         }
      })
   }

   /**
    * Registers a [BeforeContainer] function that executes before every test with
    * type [TestType.Container] in this context.
    *
    * Only affects test conatiners registered after a call to this function.
    */
   fun beforeContainer(f: BeforeContainer) {
      val thisTestCase = this.testCase
      addListener(object : TestListener {
         override suspend fun beforeContainer(testCase: TestCase) {
            if (thisTestCase.description.isAncestorOf(testCase.description)) {
               f(testCase)
            }
         }
      })
   }

   /**
    * Registers an [AfterContainer] function that executes after every test with
    * type [TestType.Container] in this context.
    *
    * Only affects test conatiners registered after a call to this function.
    */
   fun afterContainer(f: AfterContainer) {
      val thisTestCase = this.testCase
      addListener(object : TestListener {
         override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
            if (thisTestCase.description.isAncestorOf(testCase.description)) {
               f(Tuple2(testCase, result))
            }
         }
      })
   }

   /**
    * Registers a [BeforeEach] function that executes before every test with type [TestType.Test] in this scope.
    */
   fun beforeEach(f: BeforeEach) {
      val thisTestCase = this.testCase
      addListener(object : TestListener {
         override suspend fun beforeEach(testCase: TestCase) {
            if (thisTestCase.description.isAncestorOf(testCase.description)) {
               f(testCase)
            }
         }
      })
   }

   /**
    * Registers an [AfterEach] function that executes after every test with type [TestType.Test] in this scope.
    */
   fun afterEach(f: AfterEach) {
      val thisTestCase = this.testCase
      addListener(object : TestListener {
         override suspend fun afterEach(testCase: TestCase, result: TestResult) {
            if (thisTestCase.description.isAncestorOf(testCase.description)) {
               f(Tuple2(testCase, result))
            }
         }
      })
   }

   /**
    * Registers a [BeforeAny] function that executes before every test with any [TestType] in this scope.
    */
   fun beforeAny(f: BeforeAny) {
      val thisTestCase = this.testCase
      addListener(object : TestListener {
         override suspend fun beforeAny(testCase: TestCase) {
            if (thisTestCase.description.isAncestorOf(testCase.description)) f(testCase)
         }
      })
   }

   /**
    * Registers an [AfterAny] function that executes after every test with any [TestType] in this scope.
    */
   fun afterAny(f: AfterAny) {
      val thisTestCase = this.testCase
      addListener(object : TestListener {
         override suspend fun afterAny(testCase: TestCase, result: TestResult) {
            if (thisTestCase.description.isAncestorOf(testCase.description)) f(Tuple2(testCase, result))
         }
      })
   }
}

abstract class AbstractContainerContext(testContext: TestContext) : ContainerContext {
   override val testCase: TestCase = testContext.testCase
   override val coroutineContext: CoroutineContext = testContext.coroutineContext
}
