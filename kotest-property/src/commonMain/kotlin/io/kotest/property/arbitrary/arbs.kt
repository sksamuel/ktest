package io.kotest.property.arbitrary

import io.kotest.property.*

/**
 * Returns a sequence of size [count] from values generated from this arb.
 * Edgecases will be ignored.
 */
fun <A> Arb<A>.take(count: Int, rs: RandomSource = RandomSource.Default): Sequence<A> =
   samples(rs).map { it.value }.take(count)

/**
 * Returns a single value generated from this arb ignoring edgecases.
 * Alias for next.
 */
fun <A> Arb<A>.single(rs: RandomSource = RandomSource.Default): A = this.samples(rs).map { it.value }.first()

/**
 * Returns a single value generated from this arb ignoring edgecases.
 * Alias for single.
 */
fun <A> Arb<A>.next(rs: RandomSource = RandomSource.Default): A = single(rs)

/**
 * Returns a new [Arb] which takes its elements from the receiver and filters them using the supplied
 * predicate. This gen will continue to request elements from the underlying gen until one satisfies
 * the predicate.
 */
fun <A> Arb<A>.filter(predicate: (A) -> Boolean) = object : Arb<A>() {
   override fun edgecases(): List<A> = this@filter.edgecases().filter(predicate)
   override fun values(rs: RandomSource): Sequence<Sample<A>> = this@filter.values(rs).filter { predicate(it.value) }
   override fun sample(rs: RandomSource): Sample<A> = samples(rs).filter { predicate(it.value) }.first()
}

/**
 * @return a new [Arb] by filtering this arbs output by the negated function [f]
 */
fun <A> Arb<A>.filterNot(f: (A) -> Boolean): Arb<A> = filter { !f(it) }

/**
 * Returns a new [Arb] which takes its elements from the receiver and maps them using the supplied function.
 */
fun <A, B> Arb<A>.map(f: (A) -> B): Arb<B> = object : Arb<B>() {
   override fun edgecases(): List<B> = this@map.edgecases().map(f)

   override fun values(rs: RandomSource): Sequence<Sample<B>> {
      return this@map.values(rs).map { Sample(f(it.value), it.shrinks.map(f)) }
   }

   override fun sample(rs: RandomSource): Sample<B> = Sample(f(this@map.sample(rs).value))
}

/**
 * Returns a new [Arb] which takes its elements from the receiver and maps them using the supplied function.
 */
fun <A, B> Arb<A>.flatMap(f: (A) -> Arb<B>): Arb<B> = object : Arb<B>() {
   override fun edgecases(): List<B> = this@flatMap.edgecases().flatMap { f(it).edgecases() }
   override fun values(rs: RandomSource): Sequence<Sample<B>> =
      this@flatMap.samples(rs).map { sample ->
         Sample(f(sample.value).next(rs))
      }

   override fun sample(rs: RandomSource): Sample<B> = f(this@flatMap.sample(rs).value).sample(rs)
}

/**
 * Returns a new [Arb] which ensures samples are drawn from uniformly distributed distinct elements from
 * the original distribution.
 *
 * The number of minimum sample size drawn from the original distribution is determined by [minSampleSize].
 * Since resampling can be very expensive, the randomly sampled elements from the original distribution are
 * cached for reuse unless [cacheSamples] is set to false.
 */
@Deprecated("distinct will be removed in 4.5.")
fun <A> Arb<A>.distinct(minSampleSize: Int = 1000, cacheSamples: Boolean = true): Arb<A> =
   distinctBy(minSampleSize, cacheSamples) { it }

/**
 * Returns a new [Arb] which ensures samples are drawn from uniformly distributed distinct elements from
 * the original distribution based on the provided [selector] function.
 *
 * The number of minimum sample size drawn from the original distribution is determined by [minSampleSize].
 * Since resampling can be very expensive, the randomly sampled elements from the original distribution are
 * cached for reuse unless [cacheSamples] is set to false.
 */
@Deprecated("distinctBy will be removed in 4.5.")
fun <A, B> Arb<A>.distinctBy(
   minSampleSize: Int = 1000,
   cacheSamples: Boolean = true,
   selector: (A) -> B
): Arb<A> = object : Arb<A>() {
   override fun edgecases(): List<A> = this@distinctBy.edgecases().distinctBy(selector)
   override fun sample(rs: RandomSource): Sample<A> {
      val distinctSamples = if (cacheSamples) sampledElements else sampleFromPopulation(rs)
      return Sample(distinctSamples.random(rs.random))
   }

   override fun values(rs: RandomSource): Sequence<Sample<A>> = generateSequence { sample(rs) }

   private fun sampleFromPopulation(rs: RandomSource): List<A> =
      this@distinctBy.generate(rs).map { it.value }.take(minSampleSize).toList().distinctBy(selector)

   private val sampledElements: List<A> by lazy {
      // The seed is provided such that the cached random sampling
      // from the original population is repeatable across tests.
      // This approach is unfortunately less than ideal.
      sampleFromPopulation(RandomSource.seeded(1234L))
   }
}

/**
 * Returns a new [Arb] which will merge the values from this Arb and the values of
 * the supplied gen together, taking one from each in turn.
 *
 * In other words, if genA provides 1,2,3 and genB provides 7,8,9 then the merged
 * gen would output 1,7,2,8,3,9.
 *
 * The supplied gen must be a subtype of the type of this gen.
 *
 * @param other the arg to merge with this one
 * @return the merged arg.
 */
fun <A, B : A> Arb<A>.merge(other: Gen<B>): Arb<A> = object : Arb<A>() {
   override fun edgecases(): List<A> = when (other) {
      is Arb -> this@merge.edgecases().zip(other.edgecases()).flatMap { listOf(it.first, it.second) }
      is Exhaustive -> this@merge.edgecases()
   }

   override fun values(rs: RandomSource): Sequence<Sample<A>> {
      val aIterator = this@merge.samples(rs).iterator()
      val bIterator = when (other) {
         is Arb -> other.samples(rs).iterator()
         is Exhaustive -> other.toArb().samples(rs).iterator()
      }
      return generateSequence {
         if (rs.random.nextBoolean()) aIterator.next() else bIterator.next()
      }
   }

   override fun sample(rs: RandomSource): Sample<A> =
      if (rs.random.nextBoolean()) {
         this@merge.sample(rs)
      } else {
         when (other) {
            is Arb -> other.sample(rs)
            is Exhaustive -> other.toArb().sample(rs)
         }
      }
}

/**
 * returns a new [Arb] with the supplied edgecases
 */
fun <A> Arb<A>.withEdgecases(edgecases: List<A>): Arb<A> = arbitrary(edgecases) { this.next(it) }

/**
 * returns a new [Arb] with the supplied edgecases
 */
fun <A> Arb<A>.withEdgecases(vararg edgecases: A): Arb<A> = withEdgecases(edgecases.toList())

/**
 * returns a new [Arb] with a new edgecases after applying the function on the initial edgecases
 */
fun <A> Arb<A>.modifyEdgecases(f: (List<A>) -> List<A>): Arb<A> = arbitrary(f(this.edgecases())) { this.next(it) }
