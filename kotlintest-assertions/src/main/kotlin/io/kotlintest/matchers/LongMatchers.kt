package io.kotlintest.matchers

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotBe

fun between(a: Long, b: Long): Matcher<Long> = object : Matcher<Long> {
  override fun test(value: Long) = Result(value in a..b, "$value is between ($a, $b)", "$value is not between ($a, $b)")
}

fun lt(x: Long) = beLessThan(x)
fun beLessThan(x: Long) = object : Matcher<Long> {
  override fun test(value: Long) = Result(value < x, "$value should be < $x", "$value should not be < $x")
}

fun lte(x: Long) = beLessThanOrEqualTo(x)
fun beLessThanOrEqualTo(x: Long) = object : Matcher<Long> {
  override fun test(value: Long) = Result(value <= x, "$value should be <= $x", "$value should not be <= $x")
}

fun gt(x: Long) = beGreaterThan(x)
fun beGreaterThan(x: Long) = object : Matcher<Long> {
  override fun test(value: Long) = Result(value > x, "$value should be > $x", "$value should not be > $x")
}

fun gte(x: Long) = beGreaterThanOrEqualTo(x)
fun beGreaterThanOrEqualTo(x: Long) = object : Matcher<Long> {
  override fun test(value: Long) = Result(value >= x, "$value should be >= $x", "$value should not be >= $x")
}

infix fun Long.shouldBeInRange(range: LongRange) = this should beInRange(range)
infix fun Long.shouldNotBeInRange(range: LongRange) = this shouldNot beInRange(range)
fun beInRange(range: LongRange) = object : Matcher<Long> {
  override fun test(value: Long): Result =
      Result(
          value in range,
          "$value should be in range $range",
          "$value should not be in range $range"
      )
}

infix fun Long.shouldBeExactly(x: Long) = this shouldBe exactly(x)
infix fun Long.shouldNotBeExactly(x: Long) = this shouldNotBe exactly(x)
fun exactly(x: Long) = object : Matcher<Long> {
  override fun test(value: Long) = Result(
      value == x,
      "$value should be equal to $x",
      "$value should not be equal to $x"
  )
}