package com.sksamuel.kotest.specs.freespec

import io.kotest.core.Description
import io.kotest.SpecClass
import io.kotest.shouldBe
import io.kotest.specs.FreeSpec

@Suppress("OverridingDeprecatedMember", "DEPRECATION")
class FreeSpecTest : FreeSpec() {

  private var count = 0

  override fun afterSpec(description: Description, spec: SpecClass) {
    count shouldBe 3
  }

  init {

    "context a" - {
      "b1" - {
        "c" {
          count += 1
        }
      }
      "b2" - {
        "d" {
          count += 2
        }
      }
    }


    "params" - {
      "support config".config(invocations = 5) {
      }
    }
  }
}
