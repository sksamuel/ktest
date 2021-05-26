package com.sksamuel.kotest.timeout

import io.kotest.core.annotation.Ignored
import io.kotest.core.config.configuration
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.delay

@Isolate
class ProjectTimeoutTest : FunSpec({
   val listener = object : TestEngineListener {
      override fun engineFinished(t: List<Throwable>) {

      }
   }

   var original: Long = Long.MAX_VALUE

   beforeSpec {
      original = configuration.projectTimeout
      configuration.projectTimeout = 2_000L
   }

   afterSpec {
      configuration.projectTimeout = original
   }

   test("a project times out when the sum duration of its tests exceeds the specified project timeout") {
      val result = KotestEngineLauncher()
         .withListener(listener)
         .withSpec(SpecWithTestsUnderTimeout::class)
         .launch()
      result.errors.forAtLeastOne { it.message.shouldContain("TimeoutCancellationException: Timed out waiting for 2000 ms") }
   }
})

@Ignored
internal class SpecWithTestsUnderTimeout : FunSpec({
   test("1: a test under the test level timeout") {
      delay(500)
   }

   test("2: a test under the invocation timeout") { delay(500) }

   test("3: a test under the invocation timeout") { delay(500) }

   test("4: a test under the invocation timeout") { delay(500) }

   test("5: a test under the invocation timeout") { delay(500) }
})
