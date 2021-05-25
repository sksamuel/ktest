package com.sksamuel.kotest

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.ProjectListener

class MyConfig : AbstractProjectConfig() {
   override fun listeners() = listOf(TestProjectListener, TestBeforeProjectListener)
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
