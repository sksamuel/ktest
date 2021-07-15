package com.sksamuel.kotest

import io.kotest.assertions.withClue
import io.kotest.core.config.configuration
import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

@Isolate
class ProjectExtensionExceptionTest : FunSpec({
   val listExtensionEvents = mutableListOf<String>()

   val extensions = listOf(
      object : ProjectExtension {
         val name = "hello q"
         override suspend fun aroundProject(callback: suspend () -> Throwable?): Throwable? {
            listExtensionEvents.add(name)
            return callback()
         }
      },
      object : ProjectExtension {
         val name = "mon capitaine!"
         override suspend fun aroundProject(callback: suspend () -> Throwable?): Throwable? {
            callback(); return ProjectExtensionThrowable(name)
         }
      },
   )

   beforeSpec {
      configuration.registerExtensions(extensions)
   }

   afterSpec {
      configuration.deregisterExtensions(extensions)
   }

   test("the test engine should execute project extensions") {
      val errors = mutableListOf<Throwable>()
      val listener = object : TestEngineListener {
         override fun engineFinished(t: List<Throwable>) {
            errors.addAll(t)
         }
      }

      withClue("The state tracking whether the throwing or regular project extensions runs should be reset") {
         listExtensionEvents.clear()
         listExtensionEvents.shouldBeEmpty()
         errors.shouldBeEmpty()
      }

      KotestEngineLauncher().withListener(listener).withSpec(PassingProjectTest::class).launch()

      withClue("""the test engine should report errors from project extension interceptors
         and the order the interceptors are executed is not guaranteed
      """.trimIndent()) {
         listExtensionEvents + errors.map { it.message } shouldContainExactlyInAnyOrder listOf("hello q", "mon capitaine!")
      }
   }
})

private class ProjectExtensionThrowable(override val message: String) : Throwable()

private class PassingProjectTest : FunSpec({ test("this is a passing test") { 1 shouldBe 1 } })
