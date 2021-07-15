package io.kotest.property.internal

import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.assertions.show.show
import io.kotest.mpp.stacktraces
import io.kotest.property.AfterPropertyContextElement
import io.kotest.property.BeforePropertyContextElement
import kotlin.coroutines.coroutineContext

/**
 * Performs a property test for a single set of values, tracking the min success and max failure rates.
 * Will perform shrinking and throw when the property test is deemed to have failed.
 *
 * If registered, will invoke beforeProperty and afterProperty lifecycle methods.
 */
internal suspend fun test(
   context: PropertyContext,
   config: PropTestConfig,
   shrinkfn: suspend () -> List<ShrinkResult<Any?>>,
   inputs: List<Any?>,
   seed: Long,
   fn: suspend () -> Any
) {
   try {
      coroutineContext[BeforePropertyContextElement]?.before?.invoke()
      fn()
      context.markSuccess()
      coroutineContext[AfterPropertyContextElement]?.after?.invoke()
   } catch (e: AssertionError) { // we track assertion errors and try to shrink them
      context.markFailure()
      handleException(context, shrinkfn, inputs, seed, e, config)
   } catch (e: Exception) {
      context.markFailure()
      when (e::class.simpleName) {
         "AssertionError",
         "AssertionFailedError",
         "ComparisonFailure" -> handleException(context, shrinkfn, inputs, seed, e, config)
         // any other non assertion error exception is an immediate fail without shrink
         else -> throwPropertyTestAssertionError(e, context.attempts(), seed)
      }
   }
}

internal suspend fun handleException(
   context: PropertyContext,
   shrinkfn: suspend () -> List<ShrinkResult<Any?>>,
   inputs: List<Any?>,
   seed: Long,
   e: Throwable,
   config: PropTestConfig
) {
   if (config.maxFailure == 0) {

      println("Property test failed for inputs\n")
      inputs.withIndex().forEach { (index, value) ->
         println("$index) ${value.show().value}")
      }
      println()

      val cause = stacktraces.root(e)
      when (val stack = stacktraces.throwableLocation(cause, 4)) {
         null -> println("Caused by $e")
         else -> {
            println("Caused by $e at")
            stack.forEach { println("\t$it") }
         }
      }
      println()
      throwPropertyTestAssertionError(shrinkfn(), e, context.attempts(), seed)
   } else if (context.failures() > config.maxFailure) {
      var error = "Property failed ${context.failures()} times (maxFailure rate was ${config.maxFailure})\n"
      error += "Last error was caused by args:\n"
      inputs.withIndex().forEach { (index, value) ->
         error += "  $index) ${value.show().value}\n"
      }
      throwPropertyTestAssertionError(shrinkfn(), AssertionError(error), context.attempts(), seed)
   }
}
