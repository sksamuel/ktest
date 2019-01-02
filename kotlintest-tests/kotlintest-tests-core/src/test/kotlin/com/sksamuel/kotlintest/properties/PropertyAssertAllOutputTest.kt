package com.sksamuel.kotlintest.properties

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.properties.Gen
import io.kotlintest.properties.PropertyTesting
import io.kotlintest.properties.assertAll
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.FunSpec
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class PropertyAssertAllOutputTest : FunSpec() {

  override fun beforeSpec(description: Description, spec: Spec) {
    PropertyTesting.shouldLogGeneratedValues = true
  }

  init {

    fun captureStdout(thunk: () -> Unit): String {
      val previous = System.out
      val out = ByteArrayOutputStream()
      System.setOut(PrintStream(out))
      thunk()
      System.setOut(previous)
      return String(out.toByteArray())
    }

    test("property test logging for fn 1") {

      captureStdout {

        val gen = object : Gen<String> {
          override fun random(): Sequence<String> = listOf("a", "b", "c", "d", "e").asSequence()
          override fun constants(): Iterable<String> = listOf("x", "y")
        }

        assertAll(10, gen) {
          it shouldNotBe null
        }

      } shouldBe "Property test completed; values = [x, y, a, b, c, d, e]\n"
    }

    test("property test logging for fn 2") {

      captureStdout {

        val gen = object : Gen<String> {
          override fun random(): Sequence<String> = listOf("a", "b", "c", "d", "e").asSequence()
          override fun constants(): Iterable<String> = listOf("x", "y")
        }

        val gen2 = object : Gen<Int> {
          override fun random(): Sequence<Int> = listOf(1, 2, 3, 4, 5).asSequence()
          override fun constants(): Iterable<Int> = listOf(-1, -2)
        }

        assertAll(10, gen, gen2) { a, b ->
          a shouldNotBe null
          b shouldNotBe null
        }

      } shouldBe "Property test completed; values = [(x, -1), (x, -2), (y, -1), (y, -2), (a, 1), (b, 2), (c, 3), (d, 4), (e, 5)]\n"
    }

    test("property test logging for fn 3") {

      captureStdout {

        val gen = object : Gen<String> {
          override fun random(): Sequence<String> = listOf("a", "b", "c", "d", "e").asSequence()
          override fun constants(): Iterable<String> = listOf("x", "y")
        }

        val gen2 = object : Gen<Int> {
          override fun random(): Sequence<Int> = listOf(1, 2, 3, 4, 5).asSequence()
          override fun constants(): Iterable<Int> = listOf(-1, -2)
        }

        assertAll(10, gen, gen2, gen) { a, b, c ->
          a shouldNotBe null
          b shouldNotBe null
          c shouldNotBe null
        }

      } shouldBe "Property test completed; values = [(x, -1, x), (x, -1, y), (x, -2, x), (x, -2, y), (y, -1, x), (y, -1, y), (y, -2, x), (y, -2, y), (a, 1, a), (b, 2, b)]\n"
    }

    test("property test logging for fn 4") {

      captureStdout {

        val gen = object : Gen<String> {
          override fun random(): Sequence<String> = listOf("a", "b", "c", "d", "e").asSequence()
          override fun constants(): Iterable<String> = listOf("x", "y")
        }

        val gen2 = object : Gen<Int> {
          override fun random(): Sequence<Int> = listOf(1, 2, 3, 4, 5).asSequence()
          override fun constants(): Iterable<Int> = listOf(-1, -2)
        }

        assertAll(10, gen, gen2, gen, gen2) { a, b, c, d ->
          a shouldNotBe null
          b shouldNotBe null
          c shouldNotBe null
          d shouldNotBe null
        }

      } shouldBe "Property test completed; values = [(x, -1, x, -1), (x, -1, x, -2), (x, -1, y, -1), (x, -1, y, -2), (x, -2, x, -1), (x, -2, x, -2), (x, -2, y, -1), (x, -2, y, -2), (y, -1, x, -1), (y, -1, x, -2), (y, -1, y, -1), (y, -1, y, -2), (y, -2, x, -1), (y, -2, x, -2), (y, -2, y, -1), (y, -2, y, -2)]\n"
    }

    test("property test logging for fn 5") {

      captureStdout {

        val gen = object : Gen<String> {
          override fun random(): Sequence<String> = listOf("a", "b", "c", "d", "e").asSequence()
          override fun constants(): Iterable<String> = listOf("x", "y")
        }

        val gen2 = object : Gen<Int> {
          override fun random(): Sequence<Int> = listOf(1, 2, 3, 4, 5).asSequence()
          override fun constants(): Iterable<Int> = listOf(-1, -2)
        }

        assertAll(10, gen, gen2, gen, gen2, gen) { a, b, c, d, e ->
          a shouldNotBe null
          b shouldNotBe null
          c shouldNotBe null
          d shouldNotBe null
          e shouldNotBe null
        }

      } shouldBe "Property test completed; values = [(x, -1, x, -1, x), (x, -1, x, -1, y), (x, -1, x, -2, x), (x, -1, x, -2, y), (x, -1, y, -1, x), (x, -1, y, -1, y), (x, -1, y, -2, x), (x, -1, y, -2, y), (x, -2, x, -1, x), (x, -2, x, -1, y), (x, -2, x, -2, x), (x, -2, x, -2, y), (x, -2, y, -1, x), (x, -2, y, -1, y), (x, -2, y, -2, x), (x, -2, y, -2, y), (y, -1, x, -1, x), (y, -1, x, -1, y), (y, -1, x, -2, x), (y, -1, x, -2, y), (y, -1, y, -1, x), (y, -1, y, -1, y), (y, -1, y, -2, x), (y, -1, y, -2, y), (y, -2, x, -1, x), (y, -2, x, -1, y), (y, -2, x, -2, x), (y, -2, x, -2, y), (y, -2, y, -1, x), (y, -2, y, -1, y), (y, -2, y, -2, x), (y, -2, y, -2, y)]\n"
    }

    test("property test logging for fn 6") {

      captureStdout {

        val gen = object : Gen<String> {
          override fun random(): Sequence<String> = listOf("a", "b", "c", "d", "e").asSequence()
          override fun constants(): Iterable<String> = listOf("x", "y")
        }

        val gen2 = object : Gen<Int> {
          override fun random(): Sequence<Int> = listOf(1, 2, 3, 4, 5).asSequence()
          override fun constants(): Iterable<Int> = listOf(-1, -2)
        }

        assertAll(10, gen, gen2, gen, gen2, gen, gen2) { a, b, c, d, e, f ->
          a shouldNotBe null
          b shouldNotBe null
          c shouldNotBe null
          d shouldNotBe null
          e shouldNotBe null
          f shouldNotBe null
        }

      } shouldBe "Property test completed; values = [(x, -1, x, -1, x, -1), (x, -1, x, -1, x, -2), (x, -1, x, -1, y, -1), (x, -1, x, -1, y, -2), (x, -1, x, -2, x, -1), (x, -1, x, -2, x, -2), (x, -1, x, -2, y, -1), (x, -1, x, -2, y, -2), (x, -1, y, -1, x, -1), (x, -1, y, -1, x, -2), (x, -1, y, -1, y, -1), (x, -1, y, -1, y, -2), (x, -1, y, -2, x, -1), (x, -1, y, -2, x, -2), (x, -1, y, -2, y, -1), (x, -1, y, -2, y, -2), (x, -2, x, -1, x, -1), (x, -2, x, -1, x, -2), (x, -2, x, -1, y, -1), (x, -2, x, -1, y, -2), (x, -2, x, -2, x, -1), (x, -2, x, -2, x, -2), (x, -2, x, -2, y, -1), (x, -2, x, -2, y, -2), (x, -2, y, -1, x, -1), (x, -2, y, -1, x, -2), (x, -2, y, -1, y, -1), (x, -2, y, -1, y, -2), (x, -2, y, -2, x, -1), (x, -2, y, -2, x, -2), (x, -2, y, -2, y, -1), (x, -2, y, -2, y, -2), (y, -1, x, -1, x, -1), (y, -1, x, -1, x, -2), (y, -1, x, -1, y, -1), (y, -1, x, -1, y, -2), (y, -1, x, -2, x, -1), (y, -1, x, -2, x, -2), (y, -1, x, -2, y, -1), (y, -1, x, -2, y, -2), (y, -1, y, -1, x, -1), (y, -1, y, -1, x, -2), (y, -1, y, -1, y, -1), (y, -1, y, -1, y, -2), (y, -1, y, -2, x, -1), (y, -1, y, -2, x, -2), (y, -1, y, -2, y, -1), (y, -1, y, -2, y, -2), (y, -2, x, -1, x, -1), (y, -2, x, -1, x, -2), (y, -2, x, -1, y, -1), (y, -2, x, -1, y, -2), (y, -2, x, -2, x, -1), (y, -2, x, -2, x, -2), (y, -2, x, -2, y, -1), (y, -2, x, -2, y, -2), (y, -2, y, -1, x, -1), (y, -2, y, -1, x, -2), (y, -2, y, -1, y, -1), (y, -2, y, -1, y, -2), (y, -2, y, -2, x, -1), (y, -2, y, -2, x, -2), (y, -2, y, -2, y, -1), (y, -2, y, -2, y, -2)]\n"
    }
  }
}