package io.kotest.matchers.collections

import io.kotest.assertions.eq.eq
import io.kotest.assertions.show.Printed
import io.kotest.assertions.show.show
import io.kotest.matchers.*
import kotlin.jvm.JvmName

@JvmName("shouldContainExactly_iterable")
infix fun <T> Iterable<T>?.shouldContainExactly(expected: Iterable<T>) = this?.toList() should containExactly(expected.toList())
@JvmName("shouldContainExactly_array")
infix fun <T> Array<T>?.shouldContainExactly(expected: Array<T>) = this?.asList() should containExactly(*expected)
fun <T> Iterable<T>?.shouldContainExactly(vararg expected: T) = this?.toList() should containExactly(*expected)
fun <T> Array<T>?.shouldContainExactly(vararg expected: T) = this?.asList() should containExactly(*expected)

infix fun <T, C : Collection<T>> C?.shouldContainExactly(expected: C) = this should containExactly(expected)

fun <T> containExactly(vararg expected: T): Matcher<Collection<T>?> = containExactly(expected.asList())

/** Assert that a collection contains exactly the given values and nothing else, in order. */
fun <T, C : Collection<T>> containExactly(expected: C): Matcher<C?> = neverNullMatcher { actual ->

   val passed = eq(actual, expected, strictNumberEq = true) == null

   val failureMessage = {

      val missing = expected.filterNot { actual.contains(it) }
      val extra = actual.filterNot { expected.contains(it) }

      val sb = StringBuilder()
      sb.append("Expecting: ${expected.printed().value} but was: ${actual.printed().value}")
      sb.append("\n")
      if (missing.isNotEmpty()) {
         sb.append("Some elements were missing: ")
         sb.append(missing.printed().value)
         if (extra.isNotEmpty()) {
            sb.append(" and some elements were unexpected: ")
            sb.append(extra.printed().value)
         }
      } else if (extra.isNotEmpty()) {
         sb.append("Some elements were unexpected: ")
         sb.append(extra.printed().value)
      }
      sb.toString()
   }

   MatcherResult(
      passed,
      failureMessage
   ) { "Collection should not be exactly ${expected.printed().value}" }
}

@JvmName("shouldNotContainExactly_iterable")
infix fun <T> Iterable<T>?.shouldNotContainExactly(expected: Iterable<T>) = this?.toList() shouldNot containExactly(expected.toList())

@JvmName("shouldNotContainExactly_array")
infix fun <T> Array<T>?.shouldNotContainExactly(expected: Array<T>) = this?.asList() shouldNot containExactly(*expected)

fun <T> Iterable<T>?.shouldNotContainExactly(vararg expected: T) = this?.toList() shouldNot containExactly(*expected)
fun <T> Array<T>?.shouldNotContainExactly(vararg expected: T) = this?.asList() shouldNot containExactly(*expected)

infix fun <T, C : Collection<T>> C?.shouldNotContainExactly(expected: C) = this shouldNot containExactly(expected)
fun <T> Collection<T>?.shouldNotContainExactly(vararg expected: T) = this shouldNot containExactly(*expected)

fun <T, C : Collection<T>> C.printed(): Printed {
   val expectedPrinted = take(20).joinToString(",\n  ", prefix = "[\n  ", postfix = "\n]") { it.show().value }
   val expectedMore = if (size > 20) " ... (plus ${size - 20} more)" else ""
   return Printed("$expectedPrinted$expectedMore")
}
