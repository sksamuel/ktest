package com.sksamuel.kotest

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.listeners.ProjectListener

class MyConfig : AbstractProjectConfig() {
   override fun listeners() = listOf(TestProjectListener, TestBeforeProjectListener)
   override fun extensions() = listOf(TestProjectExtension, TestProjectExtension2)
}

internal val listExtensionEvents = mutableListOf<String>()

object TestProjectExtension : ProjectExtension {
   override suspend fun aroundProject(project: suspend () -> Unit) {
      listExtensionEvents.add("hello")
      project()
   }
}

object TestProjectExtension2 : ProjectExtension {
   override suspend fun aroundProject(project: suspend () -> Unit) {
      listExtensionEvents.add("there")
      project()
   }
}

object TestProjectListener : ProjectListener {

   var beforeAll = 0
   var afterAll = 0

   override suspend fun beforeProject() {
      beforeAll++
   }

   override suspend fun afterProject() {
      afterAll++
   }
}

object TestBeforeProjectListener : ProjectListener {

   var beforeAll = 0
   var afterAll = 0

   override suspend fun beforeProject() {
      beforeAll++
   }

   override suspend fun afterProject() {
      afterAll++
   }
}
