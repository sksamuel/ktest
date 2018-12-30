package io.kotlintest.assertions.arrow

import arrow.Kind
import arrow.typeclasses.ApplicativeError
import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.properties.RANDOM

internal fun <A> matcher(
  passed: Boolean,
  msg: String,
  negatedFailureMsg: String = msg
): Matcher<A> =
  object : Matcher<A> {
    override fun test(value: A): Result = Result(passed, msg, negatedFailureMsg)
  }

/**
 * Polymorphic chooser that distributes generation of arbitrary higher kinded values
 * where [F] provides extensions for the [ApplicativeError] interface.
 * The chooser dispatches returns an error or value in the context of [F]
 */
fun <F, E, A> ApplicativeError<F, E>.choose(fe: () -> E, fa: () -> A): Kind<F, A> =
  if (RANDOM.nextBoolean()) raiseError(fe()) else just(fa())