package io.kotest.property.lifecycle

import io.kotest.core.spec.Spec
import kotlinx.coroutines.withContext

fun Spec.beforeProperty(f: suspend () -> Unit) {
   aroundTest { (testCase, execute) ->
      withContext(io.kotest.property.BeforePropertyContextElement(f)) {
         execute(testCase)
      }
   }
}

fun Spec.afterProperty(f: suspend () -> Unit) {
   aroundTest { (testCase, execute) ->
      withContext(io.kotest.property.AfterPropertyContextElement(f)) {
         execute(testCase)
      }
   }
}
