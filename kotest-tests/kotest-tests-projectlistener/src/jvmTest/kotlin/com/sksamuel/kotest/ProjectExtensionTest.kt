package com.sksamuel.kotest

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly

class ProjectExtensionTest : FunSpec({
   test("TestProjectExtension and TestProjectExtension2 should have run by now and in order") {
      listExtensionEvents shouldContainExactly listOf("hello", "there")
   }
})
