package io.kotest.core.config

import kotlin.time.Duration

sealed class ProjectTimeout {
   fun toLongMilliseconds(): Long = when(this) {
      is ProjectTimeoutDuration -> projectTimeout.toLongMilliseconds()
      is ProjectTimeoutMillis -> projectTimeoutMillis
   }
}
data class ProjectTimeoutDuration(val projectTimeout: Duration) : ProjectTimeout()
data class ProjectTimeoutMillis(val projectTimeoutMillis: Long) : ProjectTimeout()
