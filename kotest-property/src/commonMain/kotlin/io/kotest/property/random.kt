package io.kotest.property

import kotlin.random.Random

data class RandomSource(val random: Random, val seed: Long) {
   companion object {

      fun seeded(seed: Long): RandomSource = RandomSource(Random(seed), seed)

      // due to concurrency issues with native, this must not be val
      fun default(): RandomSource {
         val seed = Random.Default.nextLong()
         return RandomSource(Random(seed), seed)
      }
   }
}

fun Long.random(): RandomSource = when (this) {
   0L -> RandomSource(Random(0), 0)
   else -> RandomSource(Random(this), this)
}
