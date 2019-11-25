package io.kotest.property.shrinker

import io.kotest.property.PropertyInput
import kotlin.math.abs

object LongShrinker : Shrinker<Long> {
   override fun shrink(value: Long): List<PropertyInput<Long>> =
      when (value) {
         0L -> emptyList()
         1L, -1L -> listOf(PropertyInput(0L))
         else -> {
            val a = listOf(0, 1, -1, abs(value), value / 3, value / 2, value * 2 / 3)
            val b = (1..5).map { value - it }.reversed().filter { it > 0 }
            (a + b).distinct()
               .filterNot { it == value }
               .map { PropertyInput(it, this) }
         }
      }
}
