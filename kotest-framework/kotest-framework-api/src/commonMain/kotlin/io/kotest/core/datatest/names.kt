package io.kotest.core.datatest

import io.kotest.mpp.bestName
import io.kotest.mpp.isStable

internal class Identifiers {

   private var names = mutableListOf<String>()

   /**
    * Each test name must be unique. We can use the toString if we determine the instance is stable.
    *
    * An instance is considered stable if it is a data class where each parameter is either a data class itself,
    * or one of the [primitiveTypes].
    *
    * Note: If the user has overridden toString() and the returned value is not stable, tests may not appear.
    */
   fun stableIdentifier(t: Any): String {
      val name = if (isStable(t::class)) {
         t.toString()
      } else {
         t::class.bestName()
      }
      val count = names.count { it == name }
      names.add(name)
      return if (count == 0) name else "$name ($count)"
   }
}
