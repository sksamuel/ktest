package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.distinct
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.take

class DistinctTest : FunSpec({

   test("with enough entropy distinct should return required count") {
      Arb.int(0..100000).distinct().take(100).toSet().shouldHaveSize(100)
   }

   test("without enough entropy distinct should throw") {
      shouldThrow<NoSuchElementException> {
         Arb.int(1..9).distinct().take(10).toSet().shouldHaveSize(10)
      }
   }

   test("distinct should honour attempts parameter") {
      var count = 0
      val arb = arbitrary {
         count++
         0
      }
      shouldThrow<NoSuchElementException> {
         arb.distinct(43).take(2).toSet().shouldHaveSize(2)
      }
      // +1 for the first 0, then +43 for the failures
      count shouldBe 44
   }

   test("distinct should honour attempts == 1") {
      var count = 0
      val arb = arbitrary {
         count++
         0
      }
      shouldThrow<NoSuchElementException> {
         arb.distinct(1).take(2).toSet().shouldHaveSize(2)
      }
      // +1 for the first 0, then +43 for the failures
      count shouldBe 2
   }
})
