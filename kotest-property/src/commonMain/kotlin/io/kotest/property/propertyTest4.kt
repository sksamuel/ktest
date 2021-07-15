@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.default
import io.kotest.property.internal.proptest

suspend fun <A, B, C, D> checkAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext =
   proptest(computeDefaultIteration(genA, genB, genC, genD), genA, genB, genC, genD, PropTestConfig(), property)

suspend fun <A, B, C, D> checkAll(
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext = checkAll(computeDefaultIteration(genA, genB, genC, genD), config, genA, genB, genC, genD, property)

suspend fun <A, B, C, D> checkAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext = proptest(iterations, genA, genB, genC, genD, PropTestConfig(), property)

suspend fun <A, B, C, D> checkAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext = proptest(iterations, genA, genB, genC, genD, config, property)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any> checkAll(
   noinline property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext = proptest(
   PropertyTesting.defaultIterationCount,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any> checkAll(
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext = proptest(
   PropertyTesting.defaultIterationCount,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   config,
   property
)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any> checkAll(
   iterations: Int,
   noinline property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext = proptest(
   iterations,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any> checkAll(
   iterations: Int,
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext = proptest(
   iterations,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   config,
   property
)

suspend fun <A, B, C, D> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
): PropertyContext =
   forAll(computeDefaultIteration(genA, genB, genC, genD), PropTestConfig(), genA, genB, genC, genD, property)

suspend fun <A, B, C, D> forAll(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
): PropertyContext = forAll(computeDefaultIteration(genA, genB, genC, genD), config, genA, genB, genC, genD, property)

suspend fun <A, B, C, D> forAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
): PropertyContext = forAll(iterations, PropTestConfig(), genA, genB, genC, genD, property)

suspend fun <A, B, C, D> forAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = proptest(iterations, genA, genB, genC, genD, config) { A, B, C, D -> property(A, B, C, D) shouldBe true }

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any> forAll(
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
): PropertyContext = forAll(PropertyTesting.defaultIterationCount, PropTestConfig(), property)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any> forAll(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
) = forAll(PropertyTesting.defaultIterationCount, config, property)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any> forAll(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
) = forAll(iterations, PropTestConfig(), property)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any> forAll(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
) = proptest(
   iterations,
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   config
) { A, B, C, D -> property(A, B, C, D) shouldBe true }

suspend fun <A, B, C, D> forNone(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = forNone(computeDefaultIteration(genA, genB, genC, genD), PropTestConfig(), genA, genB, genC, genD, property)

suspend fun <A, B, C, D> forNone(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = forNone(computeDefaultIteration(genA, genB, genC, genD), config, genA, genB, genC, genD, property)

suspend fun <A, B, C, D> forNone(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = forNone(iterations, PropTestConfig(), genA, genB, genC, genD, property)

suspend fun <A, B, C, D> forNone(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = proptest(iterations, genA, genB, genC, genD, config) { A, B, C, D ->
   property(A, B, C, D) shouldBe false
}

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any> forNone(
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
): PropertyContext = forNone(PropertyTesting.defaultIterationCount, PropTestConfig(), property)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any> forNone(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
): PropertyContext = forNone(PropertyTesting.defaultIterationCount, config, property)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any> forNone(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
) = forNone(iterations, PropTestConfig(), property)

suspend inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any> forNone(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
) = proptest(
   iterations,
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   config
) { A, B, C, D -> property(A, B, C, D) shouldBe false }
