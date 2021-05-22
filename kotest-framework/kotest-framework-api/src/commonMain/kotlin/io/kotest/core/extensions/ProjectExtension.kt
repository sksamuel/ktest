package io.kotest.core.extensions

/**
 * Extension point that allows intercepting execution of projects.
 */
interface ProjectExtension : Extension {
   /**
    * Implementations must invoke the [project] callback if they wish
    * the project to be executed, otherwise not calling [project] will skip the project.
    */
   suspend fun aroundProject(project: suspend () -> Unit)
}
