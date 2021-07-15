package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.checkAll

class DefaultArbTest : FunSpec() {
   init {
      test("provide defaults for primitive types") {
         // this will error if a default cannot be found
         checkAll<String, Int, Boolean, Short, Long, Double, Float> { _, _, _, _, _, _, _ ->
         }
      }

      test("provide defaults for data class") {
         data class Wobble(val a: String)
         // this will error if a default cannot be found
         checkAll<Wobble> {}
      }

      test("provide defaults for data class with nested data classes") {
         data class Wobble(val a: String)
         data class Dobble(val w: Wobble)
         // this will error if a default cannot be found
         checkAll<Dobble> {}
      }
   }
}
