package com.sksamuel.kotest.tests.json

import io.kotest.assertions.json.shouldEqualSpecifiedJson
import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class EqualIgnoringUnknownTest : FunSpec(
   {
      test("extra field in actual - passes") {
         checkAll(Arb.string(1..10, Codepoint.az())) { string ->
            val a = """ { "a" : "$string", "b": "bar" } """
            val b = """ { "a" : "$string" }"""

            a shouldEqualSpecifiedJson b
         }
      }

      test("expected field has different value - should fail") {
         val a = """ { "a" : "foo", "b" : "bar" } """
         val b = """ { "a" : "foo", "b" : "baz" } """

         a shouldEqualSpecifiedJson a

         shouldFail {
            a shouldEqualSpecifiedJson b
         }.shouldHaveMessage(
            """At 'b' expected 'baz' but was 'bar'

expected:
{
  "a": "foo",
  "b": "baz"
}

actual:
{
  "a": "foo",
  "b": "bar"
}"""
         )
      }

      test("actual missing field") {
         val a = """ { "a" : "foo" } """
         val b = """ { "a" : "foo", "b": "bar" } """

         shouldFail {
            a shouldEqualSpecifiedJson b
         }.shouldHaveMessage(
            """The top level object was missing expected field(s) [b]

expected:
{
  "a": "foo",
  "b": "bar"
}

actual:
{
  "a": "foo"
}"""
         )
      }

      context("Nested object") {
         test("extra field is ok") {
            checkAll(Arb.string(1..10, Codepoint.az())) { string ->
               val a = """ { "wrapper": { "a" : "$string", "b": "bar" } }"""
               val b = """ { "wrapper": { "a" : "$string" } }"""

               a shouldEqualSpecifiedJson b
            }
         }

         test("nested expected value differs") {
            val a = """ { "wrapper": { "a" : "foo", "b": "bar" } }"""
            val b = """ { "wrapper": { "a" : "foo", "b": "baz" } }"""

            a shouldEqualSpecifiedJson a

            shouldFail {
               a shouldEqualSpecifiedJson b
            }.shouldHaveMessage(
               """At 'wrapper.b' expected 'baz' but was 'bar'

expected:
{
  "wrapper": {
    "a": "foo",
    "b": "baz"
  }
}

actual:
{
  "wrapper": {
    "a": "foo",
    "b": "bar"
  }
}"""
            )
         }

         test("actual missing field") {
            val a = """ { "wrapper": { "a" : "foo" } } """
            val b = """ { "wrapper": { "a" : "foo", "b": "bar" } } """

            shouldFail {
               a shouldEqualSpecifiedJson b
            }.shouldHaveMessage(
               """At 'wrapper' object was missing expected field(s) [b]

expected:
{
  "wrapper": {
    "a": "foo",
    "b": "bar"
  }
}

actual:
{
  "wrapper": {
    "a": "foo"
  }
}"""
            )
         }

      }

      context("Arrays") {
         test("extra field is ok") {
            checkAll(Arb.string(1..10, Codepoint.az())) { string ->
               val a = """ { "wrapper": [{ "a" : "$string", "b": "bar" }] }"""
               val b = """ { "wrapper": [{ "a" : "$string" }] }"""

               a shouldEqualSpecifiedJson b
            }
         }

         test("nested expected value differs") {
            val a = """ { "wrapper": [{ "a" : "foo", "b": "bar" }] }"""
            val b = """ { "wrapper": [{ "a" : "foo", "b": "baz" }] }"""

            a shouldEqualSpecifiedJson a

            shouldFail {
               a shouldEqualSpecifiedJson b
            }.shouldHaveMessage(
               """At 'wrapper.[0].b' expected 'baz' but was 'bar'

expected:
{
  "wrapper": [
    {
      "a": "foo",
      "b": "baz"
    }
  ]
}

actual:
{
  "wrapper": [
    {
      "a": "foo",
      "b": "bar"
    }
  ]
}"""
            )
         }

         test("actual missing field") {
            val a = """ { "wrapper": [ { "a" : "foo" } ] } """
            val b = """ { "wrapper": [ { "a" : "foo", "b": "bar" } ] } """

            shouldFail {
               a shouldEqualSpecifiedJson b
            }.shouldHaveMessage(
               """At 'wrapper.[0]' object was missing expected field(s) [b]

expected:
{
  "wrapper": [
    {
      "a": "foo",
      "b": "bar"
    }
  ]
}

actual:
{
  "wrapper": [
    {
      "a": "foo"
    }
  ]
}"""
            )
         }
      }
   }
)
