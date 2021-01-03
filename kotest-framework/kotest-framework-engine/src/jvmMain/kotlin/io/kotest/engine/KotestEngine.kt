package io.kotest.engine

import io.kotest.core.Tags
import io.kotest.core.config.configuration
import io.kotest.core.filter.TestFilter
import io.kotest.core.internal.isIsolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.afterProject
import io.kotest.core.spec.beforeProject
import io.kotest.engine.config.ConfigManager
import io.kotest.engine.config.dumpProjectConfig
import io.kotest.engine.extensions.SpecifiedTagsTagExtension
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.script.ScriptSpec
import io.kotest.engine.dispatchers.CoroutineDispatcherFactory
import io.kotest.engine.dispatchers.coroutineDispatcherFactory
import io.kotest.engine.extensions.SpecLauncherExtension
import io.kotest.engine.launchers.ConcurrentSpecLauncher
import io.kotest.engine.launchers.SequentialSpecLauncher
import io.kotest.engine.launchers.SpecLauncher
import io.kotest.engine.script.ScriptExecutor
import io.kotest.engine.spec.SpecExecutor
import io.kotest.engine.spec.sort
import io.kotest.fp.Try
import io.kotest.fp.firstOrNone
import io.kotest.fp.getOrElse
import io.kotest.mpp.log
import kotlin.reflect.KClass
import kotlin.script.templates.standard.ScriptTemplateWithArgs

data class KotestEngineConfig(
   val filters: List<TestFilter>,
   val listener: TestEngineListener,
   val tags: Tags?,
   val dumpConfig: Boolean,
)

data class TestPlan(val classes: List<KClass<out Spec>>, val scripts: List<KClass<out ScriptTemplateWithArgs>>)

class KotestEngine(private val config: KotestEngineConfig) {

   init {

      ConfigManager.init()

      // if the engine was invoked with explicit tags, we register those via a tag extension
      config.tags?.let { configuration.registerExtension(SpecifiedTagsTagExtension(it)) }

      // if the engine was invoked with explicit filters, those are registered here
      configuration.registerFilters(config.filters)
   }

   /**
    * Starts execution of the given test plan.
    */
   suspend fun execute(plan: TestPlan) {

      if (config.dumpConfig) {
         dumpConfig()
      }

      notifyListenerEngineStarted(plan)
         .flatMap { configuration.listeners().beforeProject() }
         .fold(
            { error ->
               // any exception here is swallowed, as we already have an exception to report
               configuration.listeners().afterProject().fold(
                  { end(listOf(error, it)) },
                  {
                     end(it + error)
                  }
               )
               return
            },
            { errors ->
               if (errors.isNotEmpty()) {
                  configuration.listeners().afterProject().fold(
                     { end(errors + listOf(it)) },
                     { end(errors + it) }
                  )
                  return
               }


            }
         )

      Try { submitAll(plan) }
         .fold(
            { error ->
               log("KotestEngine: Error during submit all", error)
               configuration.listeners().afterProject().fold(
                  { end(listOf(error, it)) },
                  { end(it + error) }
               )
            },
            {
               // any exception here is used to notify the listener
               configuration.listeners().afterProject().fold(
                  { end(listOf(it)) },
                  { end(it) }
               )

            }
         )
   }

   fun cleanup() {
      configuration.deregisterFilters(config.filters)
   }

   fun dumpConfig() {
      // outputs the engine settings to the console
      configuration.dumpProjectConfig()
   }

   private fun notifyListenerEngineStarted(plan: TestPlan) = Try { config.listener.engineStarted(plan.classes) }

   private suspend fun submitAll(plan: TestPlan) = Try {
      log("KotestEngine: Beginning test plan [specs=${plan.classes.size}, scripts=${plan.scripts.size}, parallelism=${configuration.parallelism}, concurrentSpecs=${configuration.concurrentSpecs}, testConcurrentDispatch=${configuration.concurrentTests}]")

      // scripts always run sequentially
      log("KotestEngine: Launching ${plan.scripts.size} scripts")
      if (plan.scripts.isNotEmpty()) {
         config.listener.specStarted(ScriptSpec::class)
         plan.scripts.forEach { scriptKClass ->
            log(scriptKClass.java.methods.toList().toString())
            ScriptExecutor(config.listener)
               .execute(scriptKClass)
               .onFailure { config.listener.specFinished(ScriptSpec::class, it, emptyMap()) }
         }
         config.listener.specFinished(ScriptSpec::class, null, emptyMap())
         log("KotestEngine: Script execution completed")
      }

      // spec classes are ordered using an instance of SpecExecutionOrder
      val ordered = plan.classes.sort(configuration.specExecutionOrder)
      val executor = SpecExecutor(config.listener)

      val launcher = launcher()
      log("KotestEngine: Will use spec launcher $launcher")

      // if we are launching specs concurrently, then we partition the specs into those which
      // can run concurrently (default) and those which cannot (see @Isolated)
      val (consecutive, concurrent) = ordered.partition { it.isIsolate() }
      launcher.launch(executor, consecutive)
      launcher.launch(executor, concurrent)
   }

   /**
    * Returns a [SpecLauncher] to be used for launching specs.
    *
    * Will use a [SpecLauncherExtension] if provided otherwise will default to the
    * launcher provided by [defaultSpecLauncher].
    */
   private fun launcher(): SpecLauncher {
      return configuration.extensions().filterIsInstance<SpecLauncherExtension>()
         .firstOrNone()
         .map { it.launcher() }
         .getOrElse { defaultSpecLauncher() }
   }

   /**
    * The default [SpecLauncher] to use.
    *
    * Will return  either [SequentialSpecLauncher] or [ConcurrentSpecLauncher]
    * depending on the value of [configuration.concurrentSpecs].
    *
    * Will use a [CoroutineDispatcherFactory] provided by [coroutineDispatcherFactory].
    */
   private fun defaultSpecLauncher(): SpecLauncher {
      val factory = coroutineDispatcherFactory()
      return when {
         // explicitly enabled concurrent specs
         configuration.concurrentSpecs ?: 1 > 1 ->
            ConcurrentSpecLauncher(configuration.concurrentSpecs ?: 1, factory)
         // implicitly enabled concurrent specs
         configuration.concurrentSpecs == null && configuration.parallelism > 1 ->
            ConcurrentSpecLauncher(configuration.parallelism, factory)
         else -> SequentialSpecLauncher(factory)
      }
   }

   private fun end(errors: List<Throwable>) {
      errors.forEach {
         log("KotestEngine: Error during test engine run", it)
         it.printStackTrace()
      }
      config.listener.engineFinished(errors)
      // explicitly exit because we spin up test threads that the user may have put into deadlock
      // exitProcess(if (errors.isEmpty()) 0 else -1)
   }
}
