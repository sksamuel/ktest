package com.sksamuel.kotest.engine.spec.config

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.seconds

/**
 * A test that just ensures the syntax for test configs does not break between releases.
 * The actual functionality of things like tags and timeouts is tested elsewhere.
 */
class DescribeSpecConfigSyntaxTest : DescribeSpec() {
   init {

      val counter = AtomicInteger(0)

      afterSpec {
         counter.get() shouldBe 20
      }

      describe("a describe with timeout").config(timeout = 2.seconds) {
         counter.incrementAndGet()
         it("an inner test") {
            counter.incrementAndGet()
         }
      }

      describe("a describe with tags").config(tags = setOf(Tag1)) {
         counter.incrementAndGet()
         it("an inner test") {
            counter.incrementAndGet()
         }
      }

      describe("a describe with multiple tags").config(tags = setOf(Tag1, Tag2)) {
         counter.incrementAndGet()
         it("an inner test") {}
      }

      describe("a describe disabled by an enabled flag").config(enabled = false) {
         error("boom")
         it("an inner test") { error("boom") }
      }

      describe("a describe disabled by an enabled function").config(enabledIf = { System.currentTimeMillis() == 0L }) {
         error("boom")
         it("an inner test") { error("boom") }
      }

      context("a context") {
         counter.incrementAndGet()
         describe("a describe with timeout").config(timeout = 2.seconds) {
            counter.incrementAndGet()
            it("an inner test") {
               counter.incrementAndGet()
            }
         }

         describe("a describe with tags").config(tags = setOf(Tag1)) {
            counter.incrementAndGet()
            it("an inner test") {
               counter.incrementAndGet()
            }
         }

         describe("a describe with multiple tags").config(tags = setOf(Tag1, Tag2)) {
            counter.incrementAndGet()
            it("an inner test") {
               counter.incrementAndGet()
            }
         }

         describe("a describe disabled by an enabled flag").config(enabled = false) {
            error("boom")
            it("an inner test") { error("boom") }
         }

         describe("a describe disabled by an enabled function").config(enabledIf = { System.currentTimeMillis() == 0L }) {
            error("boom")
            it("an inner test") { error("boom") }
         }
      }

      context("a context with timeout").config(timeout = 2.seconds) {
         counter.incrementAndGet()
         describe("a describe") {
            counter.incrementAndGet()
            it("an inner test") {
               counter.incrementAndGet()
            }
         }
      }

      context("a context with tags").config(tags = setOf(Tag1)) {
         counter.incrementAndGet()
         describe("a describe") {
            counter.incrementAndGet()
            it("an inner test") {
               counter.incrementAndGet()
            }
         }
      }

      context("a context with multiple tags").config(tags = setOf(Tag1, Tag2)) {
         counter.incrementAndGet()
         describe("a describe") {
            counter.incrementAndGet()
            it("an inner test") {
               counter.incrementAndGet()
            }
         }
      }

      context("a context disabled by an enabled flag").config(enabled = false) {
         counter.incrementAndGet()
         describe("a describe") {
            counter.incrementAndGet()
            it("an inner test") {
               counter.incrementAndGet()
            }
         }
      }

      context("a context disabled by an enabled function").config(enabledIf = { System.currentTimeMillis() == 0L }) {
         counter.incrementAndGet()
         describe("a describe") {
            counter.incrementAndGet()
            it("an inner test") {
               counter.incrementAndGet()
            }
         }
      }
   }
}
