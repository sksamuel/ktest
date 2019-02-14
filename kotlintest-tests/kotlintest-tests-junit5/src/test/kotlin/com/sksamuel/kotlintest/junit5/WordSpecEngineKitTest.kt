package com.sksamuel.kotlintest.junit5

import io.kotlintest.specs.FunSpec
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.testkit.engine.EngineTestKit

class WordSpecEngineKitTest : FunSpec({

  test("verify container stats") {
    EngineTestKit
        .engine("kotlintest")
        .selectors(selectClass(WordSpecTestCase::class.java))
        .execute()
        .containers()
        .assertStatistics { it.started(12).succeeded(8) }
  }

  test("verify test stats") {
    EngineTestKit
        .engine("kotlintest")
        .selectors(selectClass(WordSpecTestCase::class.java))
        .execute()
        .tests()
        .assertStatistics { it.skipped(2).started(6).succeeded(3).aborted(0).failed(3).finished(6) }
  }

})