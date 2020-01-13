package io.kotest.matchers

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.KotlinCompilation.Result
import com.tschuchort.compiletesting.SourceFile
import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.should
import io.kotest.shouldNot
import java.io.File

private fun compileCodeSnippet(codeSnippet: String): Result {
   val kotlinCompilation = KotlinCompilation()
      .apply {
         sources = listOf(SourceFile.kotlin("KClass.kt", codeSnippet))
         inheritClassPath = true
      }
   val compilationResult = kotlinCompilation.compile()
   kotlinCompilation.workingDir.deleteRecursively()

   return compilationResult
}

private val compiles = object : Matcher<String> {
   override fun test(value: String): MatcherResult {
      val compilationResult = compileCodeSnippet(value)
      return MatcherResult(
         compilationResult.exitCode == ExitCode.OK,
         { "Expected code to compile, but it failed to compile" },
         { "Expected code to fail to compile, but it compile" }
      )
   }
}

/**
 * Assert that given codeSnippet[String] compiles successfully.
 * It includes the classpath of the calling process,
 * so that dependencies available for calling process will be available for code snippet snippet as well.
 * @see [String.shouldNotCompiles]
 * */
fun String.shouldCompiles() = this should compiles

/**
 * Assert that given codeSnippet[String] does not compiles successfully.
 * It includes the classpath of the calling process,
 * so that dependencies available for calling process will be available for code snippet snippet as well.
 * @see [String.shouldCompiles]
 * */
fun String.shouldNotCompiles() = this shouldNot compiles

/**
 * Assert that given file[File] compiles successfully.
 * It includes the classpath of the calling process,
 * so that dependencies available for calling process will be available for code snippet snippet as well.
 * @see [File.shouldNotCompiles]
 * */
fun File.shouldCompiles() = readText() should compiles

/**
 * Assert that given file[File] does not compiles successfully.
 * It includes the classpath of the calling process,
 * so that dependencies available for calling process will be available for code snippet snippet as well.
 * @see [File.shouldNotCompiles]
 * */
fun File.shouldNotCompiles() = readText() shouldNot compiles
