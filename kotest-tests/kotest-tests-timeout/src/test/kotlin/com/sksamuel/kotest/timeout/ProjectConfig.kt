package com.sksamuel.kotest.timeout

import io.kotest.core.config.AbstractProjectConfig
import kotlin.time.Duration
import io.kotest.core.spec.SpecExecutionOrder

object ProjectConfig : AbstractProjectConfig() {
   override val specExecutionOrder = SpecExecutionOrder.Annotated
   override val timeout = Duration.milliseconds(1000)

   /**
    * Uncomment [projectTimeout] to see it in action for this test project
    * Pretty difficult to validate without making [io.kotest.core.config.configuration] a default argument to
    * [io.kotest.engine.KotestEngine], but then we're just modifying production code with the sole purpose of testing...
    */
//   override val projectTimeout = ProjectTimeoutMillis(2_000L) // TODO: uncomment this to see the projectTimeout in action
}
