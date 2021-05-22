package io.kotest.engine

import io.kotest.core.Tags
import io.kotest.core.config.configuration
import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.filter.SpecFilter
import io.kotest.core.filter.TestFilter
import io.kotest.core.spec.Spec
import io.kotest.core.spec.afterProject
import io.kotest.core.spec.beforeProject
import io.kotest.engine.config.ConfigManager
import io.kotest.engine.extensions.DumpConfigExtension
import io.kotest.engine.extensions.EmptyTestSuiteExtension
import io.kotest.engine.extensions.EngineExtension
import io.kotest.engine.extensions.KotestPropertiesExtension
import io.kotest.engine.extensions.SpecifiedTagsTagExtension
import io.kotest.engine.extensions.TestDslStateExtensions
import io.kotest.engine.launchers.specLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.script.ScriptExecutor
import io.kotest.engine.spec.SpecExecutor
import io.kotest.engine.spec.sort
import io.kotest.fp.Try
import io.kotest.fp.getOrElse
import io.kotest.mpp.log
import kotlin.reflect.KClass
import kotlin.script.templates.standard.ScriptTemplateWithArgs

data class KotestEngineConfig(
   val testFilters: List<TestFilter>,
   val specFilters: List<SpecFilter>,
   val listener: TestEngineListener,
   val explicitTags: Tags?,
   val dumpConfig: Boolean,
)

data class TestSuite(
   val classes: List<KClass<out Spec>>,
   val scripts: List<KClass<out ScriptTemplateWithArgs>>
)

data class EngineResult(val errors: List<Throwable>)

class KotestEngine(private val config: KotestEngineConfig) {

   init {

      ConfigManager.init()

      // if the engine was invoked with explicit tags, we register those via a tag extension
      config.explicitTags?.let { configuration.registerExtension(SpecifiedTagsTagExtension(it)) }

      // if the engine was invoked with explicit filters, those are registered here
      configuration.registerFilters(config.testFilters)
   }

   /**
    * Starts execution of the given [TestSuite], intercepting calls via [EngineExtension]s.
    */
   suspend fun execute(suite: TestSuite): EngineResult {

      val innerExecute: suspend (TestSuite, TestEngineListener) -> EngineResult =
         { ts, tel -> executeTestSuite(ts, tel) }

      val extensions = listOfNotNull(
         KotestPropertiesExtension,
         TestDslStateExtensions,
         if (config.dumpConfig) DumpConfigExtension(configuration) else null,
         if (configuration.failOnEmptyTestSuite) EmptyTestSuiteExtension else null,
      )

      val execute = extensions.foldRight(innerExecute) { extension, next ->
         { ts, tel -> extension.intercept(ts, tel, next) }
      }

      val result = execute(suite, config.listener)
      notifyResult(result)
      return result
   }

   private suspend fun executeTestSuite(suite: TestSuite, listener: TestEngineListener): EngineResult {

      val projectExtensions = configuration.extensions().filterIsInstance<ProjectExtension>().reversed()

      val beforeErrors = notifyListenerEngineStarted(suite, listener)
         .flatMap { configuration.listeners().beforeProject() }
         .fold({ listOf(it) }, { it })

      // if we have errors in the before project listeners, we'll not even execute tests, but
      // instead immediately exit. Any errors in after project are added to the original error.
      val allErrors = beforeErrors + configuration.listeners().afterProject().getOrElse { emptyList() }
      if (allErrors.isNotEmpty())
         return EngineResult(allErrors)

      val submissionError = submitAll(suite, listener).errorOrNull()

      // after project listeners are executed even if the submission fails and the errors are added together
      val afterErrors = configuration.listeners().afterProject().getOrElse { emptyList() }
      return EngineResult(listOfNotNull(submissionError) + afterErrors)
   }

   private tailrec suspend fun runProjectExtensions(extensions: List<ProjectExtension>, f: suspend () -> Unit) {
      if (extensions.isEmpty())
         f()
      else {
         val head = extensions.first()
         val tail = extensions.drop(1)

         runProjectExtensions(tail) {
            head.aroundProject(f)
         }
      }
   }

   fun cleanup() {
      configuration.deregisterFilters(config.testFilters)
   }

   private fun notifyListenerEngineStarted(suite: TestSuite, listener: TestEngineListener) =
      Try { listener.engineStarted(suite.classes) }

   /**
    * Submit the test suite to be executed. Returns a success if everything completed normally,
    * or returns an failure if an unexpected (not test failure) error occured.
    */
   private suspend fun submitAll(suite: TestSuite, listener: TestEngineListener): Try<Unit> = Try {
      log { "KotestEngine: Beginning test plan [specs=${suite.classes.size}, scripts=${suite.scripts.size}, parallelism=${configuration.parallelism}}]" }

      // scripts always run sequentially
      log { "KotestEngine: Launching ${suite.scripts.size} scripts" }
      if (suite.scripts.isNotEmpty()) {
         suite.scripts.forEach { scriptKClass ->
            log { scriptKClass.java.methods.toList().toString() }
            ScriptExecutor(listener)
               .execute(scriptKClass)
         }
         log { "KotestEngine: Script execution completed" }
      }

      // spec classes are ordered using an instance of SpecExecutionOrder
      log { "KotestEngine: Sorting specs by ${configuration.specExecutionOrder}" }
      val ordered = suite.classes.sort(configuration.specExecutionOrder)

      val executor = SpecExecutor(listener)
      log { "KotestEngine: Will use spec executor $executor" }

      val launcher = specLauncher()
      log { "KotestEngine: Will use spec launcher $launcher" }

      launcher.launch(executor, ordered)
   }

   private fun notifyResult(result: EngineResult) {
      result.errors.forEach {
         log(it) { "KotestEngine: Error during test engine run" }
         it.printStackTrace()
      }
      // notify only the original listener of the final engine result
      config.listener.engineFinished(result.errors)
      // explicitly exit because we spin up test threads that the user may have put into deadlock
      // exitProcess(if (errors.isEmpty()) 0 else -1)
   }
}
