package io.kotest.matchers.collections.inspectors

import io.kotest.matchers.should

//internal typealias MatcherBlock<T> = (T) -> Unit
fun interface MatcherBlock<T> {
   operator fun invoke(element: T): Unit
}

fun <T> Array<T>.shouldContain(times: Int, block: MatcherBlock<T>) = asList() should containExactly(times, block)
fun <T> Iterable<T>?.shouldContain(times: Int, block: MatcherBlock<T>) = this should containExactly(times, block)


infix fun <T> Array<T>.shouldAll(block: MatcherBlock<T>) = asList() should all(block)
infix fun <T> Array<T>.shouldNotContain(block: MatcherBlock<T>) = asList() should containExactly(0, block)
infix fun <T> Array<T>.shouldContainSome(block: MatcherBlock<T>) = asList() should containSome(block)
infix fun <T> Array<T>.shouldContainOne(block: MatcherBlock<T>) = asList() should containExactly(1, block)

infix fun <T> Iterable<T>?.shouldAll(block: MatcherBlock<T>) = this should all(block)
infix fun <T> Iterable<T>?.shouldNotContain(block: MatcherBlock<T>) = this should containExactly(0, block)
infix fun <T> Iterable<T>?.shouldContainSome(block: MatcherBlock<T>) = this should containSome(block)
infix fun <T> Iterable<T>?.shouldContainOne(block: MatcherBlock<T>) = this should containExactly(1, block)


fun <T> Array<T>.shouldContainAtLeast(times: Int, block: MatcherBlock<T>) = asList() should containAtLeast(times, block)
infix fun <T> Array<T>.shouldContainAtLeastOne(block: MatcherBlock<T>) = asList() should containAtLeast(1, block)

fun <T> Iterable<T>?.shouldContainAtLeast(times: Int, block: MatcherBlock<T>) = this should containAtLeast(times, block)
infix fun <T> Iterable<T>?.shouldContainAtLeastOne(block: MatcherBlock<T>) = this should containAtLeast(1, block)


fun <T> Array<T>.shouldContainAtMost(times: Int, block: MatcherBlock<T>) = asList() should containAtMost(times, block)
infix fun <T> Array<T>.shouldContainAtMostOne(block: MatcherBlock<T>) = asList() should containAtMost(1, block)

fun <T> Iterable<T>?.shouldContainAtMost(times: Int, block: MatcherBlock<T>) = this should containAtMost(times, block)
infix fun <T> Iterable<T>?.shouldContainAtMostOne(block: MatcherBlock<T>) = this should containAtMost(1, block)
