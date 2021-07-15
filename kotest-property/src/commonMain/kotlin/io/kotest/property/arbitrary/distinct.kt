package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample

fun <A> Arb<A>.distinct(attempts: Int = 100) = object : Arb<A>() {

   private val seen = mutableSetOf<A>()

   override fun edgecase(rs: RandomSource): A? = this@distinct.edgecase(rs)

   override fun sample(rs: RandomSource): Sample<A> {
      var iterations = 0
      return generateSequence {
         if (iterations++ < attempts) this@distinct.sample(rs) else null
      }.filter { seen.add(it.value) }.first()
   }

}
