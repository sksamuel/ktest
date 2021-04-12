package io.kotest.core.spec.style

import io.kotest.core.test.TestType
import io.kotest.matchers.shouldBe

class FunSpecTestTypeTest : FunSpec() {
   init {
      context("context") {
         this.testCase.type shouldBe TestType.Container
         context("context 2") {
            this.testCase.type shouldBe TestType.Container
            test("test") {
               this.testCase.type shouldBe TestType.Test
            }
         }
         test("test") {
            this.testCase.type shouldBe TestType.Test
         }
      }
   }
}