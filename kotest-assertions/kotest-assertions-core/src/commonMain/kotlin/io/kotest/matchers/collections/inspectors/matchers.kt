package io.kotest.matchers.collections.inspectors

import io.kotest.inspectors.ElementResult
import io.kotest.inspectors.buildAssertionError
import io.kotest.inspectors.runTests
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult


fun <T> all(block: MatcherBlock<T>) = buildCollectionMatcher(
   { items -> with(items.count()) { this..this } },
   block,
   { violations -> "Expected all ${violations.count()} elements to pass, but found ${violations.countFails()} failures" },
   { violations -> "Expected no element to pass, but ${violations.countMatches()} did" },
)


fun <T> containExactly(times: Int = 1, block: MatcherBlock<T>) = buildCollectionMatcher(
   { times..times },
   block,
   { violations -> "${violations.countMatches()} elements passed but expected $times" },
   { violations -> "${violations.countMatches()} elements passed, expected to differ from $times" },
)

fun <T> containSome(block: MatcherBlock<T>) = buildCollectionMatcher(
   { items -> 1 until items.count() },
   block,
   { violations -> "Expected some, but not all, elements to match, but ${if (violations.countMatches() == 0) "none" else "all"} did" },
   { violations -> "Expected all or none of the elements in the collection to match, but ${violations.countMatches()} did" },
)

/** Tests that at least [times] elements of the collection matches the block */
fun <T> containAtMost(times: Int = 1, block: MatcherBlock<T>) = buildCollectionMatcher(
   { 0..times },
   block,
   { violations -> "${violations.countMatches()} elements passed but expected at most $times" },
   { violations -> "${violations.countMatches()} elements passed but expected > $times" },
)

/** Tests that at least [times] elements of the collection matches the block */
fun <T> containAtLeast(times: Int = 1, block: MatcherBlock<T>) = buildCollectionMatcher(
   { times..Int.MAX_VALUE },
   block,
   { violations -> "${violations.countMatches()} elements passed but expected at least $times" },
   { violations -> "${violations.countMatches()} elements passed but expected < $times" },
)

internal fun <T> buildCollectionMatcher(
   validRange: (Iterable<T>) -> IntRange,
   block: MatcherBlock<T>,
   failureMessageFn: (violations: List<ElementResult<T>>) -> String,
   negatedFailureMessageFn: (violations: List<ElementResult<T>>) -> String
) = object : Matcher<Iterable<T>?> {
   override fun test(ts: Iterable<T>?): MatcherResult {
      checkNotNull(ts) // TODO
      val elementResults = runTests(ts.toList()) { element -> block(element) }
      val matchCount = elementResults.count { it.error() == null }

      return MatcherResult(
         matchCount in validRange(ts),
         { buildAssertionError(failureMessageFn(elementResults), elementResults) },
         { buildAssertionError(negatedFailureMessageFn(elementResults), elementResults) },
      )
   }
}

private fun <T> List<ElementResult<T>>.countMatches() = this.count { it.error() == null }
private fun <T> List<ElementResult<T>>.countFails() = this.count { it.error() != null }
