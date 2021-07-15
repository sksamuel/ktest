@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.default
import io.kotest.property.internal.proptest

suspend fun <A, B, C, D, E, F> checkAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit
): PropertyContext = proptest(computeDefaultIteration(genA, genB, genC, genD, genE, genF), genA, genB, genC, genD, genE, genF, PropTestConfig(), property)

suspend fun <A, B, C, D, E, F> checkAll(
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit
): PropertyContext = checkAll(computeDefaultIteration(genA, genB, genC, genD, genE, genF), config, genA, genB, genC, genD, genE, genF, property)

suspend fun <A, B, C, D, E, F> checkAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit
): PropertyContext = proptest(iterations, genA, genB, genC, genD, genE, genF, PropTestConfig(), property)

suspend fun <A, B, C, D, E, F> checkAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit
): PropertyContext = proptest(iterations, genA, genB, genC, genD, genE, genF, config, property)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any> checkAll(
   noinline property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit
) = proptest(
   PropertyTesting.defaultIterationCount,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any> checkAll(
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit
) = proptest(
   PropertyTesting.defaultIterationCount,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   config,
   property
)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any> checkAll(
   iterations: Int,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit
) = proptest(
   iterations,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any> checkAll(
   iterations: Int,
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit
) = proptest(
   iterations,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   config,
   property
)

suspend fun <A, B, C, D, E, F> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forAll(computeDefaultIteration(genA, genB, genC, genD, genE, genF), PropTestConfig(), genA, genB, genC, genD, genE, genF, property)

suspend fun <A, B, C, D, E, F> forAll(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forAll(computeDefaultIteration(genA, genB, genC, genD, genE, genF), config, genA, genB, genC, genD, genE, genF, property)

suspend fun <A, B, C, D, E, F> forAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forAll(iterations, PropTestConfig(), genA, genB, genC, genD, genE, genF, property)

suspend fun <A, B, C, D, E, F> forAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Boolean
) = proptest(iterations, genA, genB, genC, genD, genE, genF, config) { a, b, c, d, e, f -> property(a, b, c, d, e, f) shouldBe true }

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any> forAll(
   crossinline property: PropertyContext.(A, B, C, D, E, F) -> Boolean
): PropertyContext = forAll(PropertyTesting.defaultIterationCount, PropTestConfig(), property)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any> forAll(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forAll(PropertyTesting.defaultIterationCount, config, property)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any> forAll(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forAll(iterations, PropTestConfig(), property)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any> forAll(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E, F) -> Boolean
) = proptest(
   iterations,
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   Arb.default<E>(),
   Arb.default<F>(),
   config
) { a, b, c, d, e, f -> property(a, b, c, d, e, f) shouldBe true }

suspend fun <A, B, C, D, E, F> forNone(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forNone(computeDefaultIteration(genA, genB, genC, genD, genE, genF), PropTestConfig(), genA, genB, genC, genD, genE, genF, property)

suspend fun <A, B, C, D, E, F> forNone(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forNone(computeDefaultIteration(genA, genB, genC, genD, genE, genF), config, genA, genB, genC, genD, genE, genF, property)

suspend fun <A, B, C, D, E, F> forNone(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forNone(iterations, PropTestConfig(), genA, genB, genC, genD, genE, genF, property)

suspend fun <A, B, C, D, E, F> forNone(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Boolean
) = proptest(iterations, genA, genB, genC, genD, genE, genF, config) {
   a, b, c, d, e, f ->
   property(a, b, c, d, e, f) shouldBe false
}

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any> forNone(
   crossinline property: PropertyContext.(A, B, C, D, E, F) -> Boolean
): PropertyContext = forNone(PropertyTesting.defaultIterationCount, PropTestConfig(), property)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any> forNone(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E, F) -> Boolean
): PropertyContext = forNone(PropertyTesting.defaultIterationCount, config, property)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any> forNone(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forNone(iterations, PropTestConfig(), property)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any> forNone(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E, F) -> Boolean
) = proptest(
   iterations,
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   Arb.default<E>(),
   Arb.default<F>(),
   config
) { a, b, c, d, e, f -> property(a, b, c, d, e, f) shouldBe false }
