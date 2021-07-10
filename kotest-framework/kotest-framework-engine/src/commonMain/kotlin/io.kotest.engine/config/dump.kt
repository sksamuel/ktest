package io.kotest.engine.config

import io.kotest.core.config.Configuration
import io.kotest.core.internal.tags.activeTags
import io.kotest.mpp.bestName

fun Configuration.createConfigSummary(): String {

   val sb = StringBuilder()

   sb.buildOutput("Parallelization factor", parallelism.toString())
   sb.buildOutput("Concurrent specs", concurrentSpecs.toString())
   sb.buildOutput("Global concurrent tests", concurrentTests.toString())
   sb.buildOutput("Dispatcher affinity", dispatcherAffinity.toString())
   sb.buildOutput("Default test timeout", timeout.toString() + "ms")
   sb.buildOutput("Default test order", testCaseOrder.name)
   sb.buildOutput("Overall project timeout", projectTimeout.toString() + "ms") // TODO: make duration when kotlin.time stabilizes
   sb.buildOutput("Default isolation mode", isolationMode.name)
   sb.buildOutput("Global soft assertions", globalAssertSoftly.toString())
   sb.buildOutput("Write spec failure file", writeSpecFailureFile.toString())
   if (writeSpecFailureFile) {
      sb.buildOutput("Spec failure file path",
         specFailureFilePath.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
   }
   sb.buildOutput("Fail on ignored tests", failOnIgnoredTests.toString())
   sb.buildOutput("Spec execution order", specExecutionOrder::class.simpleName)

   if (includeTestScopeAffixes != null)
      sb.buildOutput("Include test scope affixes", includeTestScopeAffixes.toString())

   sb.buildOutput("Remove test name whitespace", removeTestNameWhitespace.toString())
   sb.buildOutput("Append tags to test names", testNameAppendTags.toString())

   if (extensions().isNotEmpty()) {
      sb.buildOutput("Extensions")
      extensions().map(::mapClassName).forEach {
         sb.buildOutput(it, indentation = 1)
      }
   }

   if (listeners().isNotEmpty()) {
      sb.buildOutput("Listeners")
      listeners().map(::mapClassName).forEach {
         sb.buildOutput(it, indentation = 1)
      }
   }

   activeTags().expression?.let { sb.buildOutput("Tags", it) }
   return sb.toString()
}

fun Configuration.dumpProjectConfig() {
   println("~~~ Kotest Configuration ~~~")
   println(createConfigSummary())
}

private fun StringBuilder.buildOutput(key: String, value: String? = null, indentation: Int = 0) {
   if (indentation == 0) {
      append("-> ")
   } else {
      for (i in 0 until indentation) {
         append("  ")
      }
      append("- ")
   }
   append(key)
   value?.let { append(": $it") }
   append("\n")
}

private fun mapClassName(any: Any) = any::class.bestName()
