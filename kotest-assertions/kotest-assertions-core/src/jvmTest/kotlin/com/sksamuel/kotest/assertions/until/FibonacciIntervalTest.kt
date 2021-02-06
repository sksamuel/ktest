package com.sksamuel.kotest.assertions.until

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.until.fibonacci
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.seconds

class FibonacciIntervalTest : FunSpec() {

   init {
      test("fib correctness") {
         fibonacci(0) shouldBe 0
         fibonacci(1) shouldBe 1
         fibonacci(2) shouldBe 1
         fibonacci(3) shouldBe 2
         fibonacci(4) shouldBe 3
         fibonacci(5) shouldBe 5
         fibonacci(6) shouldBe 8
         fibonacci(7) shouldBe 13
      }

      test("fib cap correctness") {
         val cap = 60.seconds
         val unbounded = 1.seconds.fibonacci()
         val bounded = 1.seconds.fibonacci(cap = cap)

         assertSoftly {
            for (i in 1..20) {
               val u = unbounded.next(i)
               val b = bounded.next(i)
               if (u < cap) {
                  withClue("durations under the cap should be unchanged") {
                     b shouldBe u
                  }
               } else {
                  withClue("durations over the cap should be clamped") {
                     b shouldBe cap
                  }
               }
            }
         }
      }
   }

}
