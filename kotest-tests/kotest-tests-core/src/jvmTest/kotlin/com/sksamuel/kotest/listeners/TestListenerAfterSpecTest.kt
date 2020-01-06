package com.sksamuel.kotest.listeners

import io.kotest.core.IsolationMode
import io.kotest.SpecClass
import io.kotest.assertions.fail
import io.kotest.specs.FunSpec
import java.util.concurrent.atomic.AtomicInteger

class TestListenerAfterSpecTest : FunSpec() {

  companion object {
    private val counter = AtomicInteger(4)
  }

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

  // should be invoked once per isolated test
  override fun afterSpec(spec: SpecClass) {
    if (counter.decrementAndGet() < 0)
      fail("Error, after spec called too many times")
  }

  init {
    test("a") { }
    test("b") { }
    test("c") { }
    test("d") { }
  }
}
