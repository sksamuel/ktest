package com.sksamuel.kotest.specs

import io.kotest.core.config.Project
import io.kotest.core.config.TestNameCaseOptions
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestName
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class TestNameTest : FunSpec() {

   init {
      val prefix = "Prefix: "

      test("test name case conversion only changes first letter of prefix and name") {
         Project.testNameCase(TestNameCaseOptions.LOWERCASE)
         TestName(null, "Test URL").displayName() shouldBe "test URL"
         TestName("Pref OK", "Test URL").displayName() shouldBe "pref OKtest URL"

         Project.testNameCase(TestNameCaseOptions.SENTENCE)
         TestName(null, "test URL").displayName() shouldBe "Test URL"
         TestName("pref OK", "Test URL").displayName() shouldBe "Pref OKtest URL"

         Project.testNameCase(TestNameCaseOptions.AS_IS)
      }

      test("prefix should be placed before name when not null") {
         TestName(null, "test").displayName() shouldBe "test"
         TestName("pref", "test").displayName() shouldBe "preftest"

         Project.testNameCase(TestNameCaseOptions.LOWERCASE)
         TestName(null, "Test").displayName() shouldBe "test"
         TestName(null, "test").displayName() shouldBe "test"
         TestName("Pref", "Test").displayName() shouldBe "preftest"
         TestName("Pref", "test").displayName() shouldBe "preftest"
         TestName("pref", "Test").displayName() shouldBe "preftest"
         TestName("pref", "test").displayName() shouldBe "preftest"

         Project.testNameCase(TestNameCaseOptions.SENTENCE)
         TestName(null, "Test").displayName() shouldBe "Test"
         TestName(null, "test").displayName() shouldBe "Test"
         TestName("Pref", "Test").displayName() shouldBe "Preftest"
         TestName("Pref", "test").displayName() shouldBe "Preftest"
         TestName("pref", "Test").displayName() shouldBe "Preftest"
         TestName("pref", "test").displayName() shouldBe "Preftest"

         Project.testNameCase(TestNameCaseOptions.AS_IS)
      }

      test("Display Name should place bang before name") {
         val name = "!banged"
         TestName(null, name).bang.shouldBeTrue()
         TestName(null, name).displayName() shouldBe "!banged"

         Project.testNameCase(TestNameCaseOptions.LOWERCASE)
         listOf("!banged", "!Banged").forEach {
            TestName(null, it).bang.shouldBeTrue()
            TestName(null, it).displayName() shouldBe "!banged"
         }

         Project.testNameCase(TestNameCaseOptions.SENTENCE)
         listOf("!banged", "!Banged").forEach {
            TestName(null, it).bang.shouldBeTrue()
            TestName(null, it).displayName() shouldBe "!Banged"
         }

         Project.testNameCase(TestNameCaseOptions.AS_IS)
      }

      test("Display Name should place bang before prefix and name") {
         val name = "!banged"
         TestName(prefix, name).bang.shouldBeTrue()
         TestName(prefix, name).displayName() shouldBe "!Prefix: banged"

         Project.testNameCase(TestNameCaseOptions.LOWERCASE)
         listOf("!banged", "!Banged").forEach {
            TestName(prefix, it).bang.shouldBeTrue()
            TestName(prefix, it).displayName() shouldBe "!prefix: banged"
         }

         Project.testNameCase(TestNameCaseOptions.SENTENCE)
         listOf("!banged", "!Banged").forEach {
            TestName(prefix, it).bang.shouldBeTrue()
            TestName(prefix, it).displayName() shouldBe "!Prefix: banged"
         }

         Project.testNameCase(TestNameCaseOptions.AS_IS)
      }

      test("Display Name should place focus before name") {
         val name = "f:Focused"
         TestName(null, name).focus.shouldBeTrue()
         TestName(null, name).displayName() shouldBe "f:Focused"

         Project.testNameCase(TestNameCaseOptions.LOWERCASE)
         listOf("f:Focused", "f:focused").forEach {
            TestName(null, it).focus.shouldBeTrue()
            TestName(null, it).displayName() shouldBe "f:focused"
         }

         Project.testNameCase(TestNameCaseOptions.SENTENCE)
         listOf("f:Focused", "f:focused").forEach {
            TestName(null, it).focus.shouldBeTrue()
            TestName(null, it).displayName() shouldBe "f:Focused"
         }

         Project.testNameCase(TestNameCaseOptions.AS_IS)
      }

      test("Display Name should place focus before prefix and name") {
         val name = "f:Focused"
         TestName(prefix, name).focus.shouldBeTrue()
         TestName(prefix, name).displayName() shouldBe "f:Prefix: Focused"

         Project.testNameCase(TestNameCaseOptions.LOWERCASE)
         listOf("f:Focused", "f:focused").forEach {
            TestName(prefix, it).focus.shouldBeTrue()
            TestName(prefix, it).displayName() shouldBe "f:prefix: focused"
         }

         Project.testNameCase(TestNameCaseOptions.SENTENCE)
         listOf("f:Focused", "f:focused").forEach {
            TestName(prefix, it).focus.shouldBeTrue()
            TestName(prefix, it).displayName() shouldBe "f:Prefix: focused"
         }

         Project.testNameCase(TestNameCaseOptions.AS_IS)
      }

      test("Should bring bang to the start of the test if there's a focus after it") {
         val name = "!f: BangFocus"
         TestName(prefix, name).displayName() shouldBe "!Prefix: f: BangFocus"

         Project.testNameCase(TestNameCaseOptions.LOWERCASE)
         TestName(prefix, name).displayName() shouldBe "!prefix: f: BangFocus"

         Project.testNameCase(TestNameCaseOptions.SENTENCE)
         TestName(prefix, name).displayName() shouldBe "!Prefix: f: BangFocus"

         Project.testNameCase(TestNameCaseOptions.AS_IS)
      }
   }
}
