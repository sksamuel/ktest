package io.kotest.plugin.pitest

import io.kotest.SpecClass
import org.pitest.testapi.TestUnit
import org.pitest.testapi.TestUnitFinder
import kotlin.reflect.KClass

class KotestUnitFinder : TestUnitFinder {

  override fun findTestUnits(clazz: Class<*>): MutableList<TestUnit> {
    return when {
      SpecClass::class.java.isAssignableFrom(clazz) -> mutableListOf(KotestUnit(clazz.kotlin as KClass<out SpecClass>))
      else -> mutableListOf()
    }
  }
}
