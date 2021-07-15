plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")
   id("com.adarshr.test-logger")
}

repositories {
   mavenCentral()
}

kotlin {

   targets {
      jvm {
         compilations.all {
            kotlinOptions {
               jvmTarget = "1.8"
            }
         }
      }
      js(BOTH) {
         browser()
         nodejs()
      }
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            compileOnly(kotlin("stdlib"))
            api(project(Projects.Property))
            api(project(Projects.Api))
            implementation(Libs.Coroutines.coreCommon)
            implementation(project(Projects.Common))
         }
      }

      val commonTest by getting {
         dependencies {
            implementation(project(Projects.Engine))
            implementation(project(Projects.AssertionsCore))
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(project(Projects.JunitRunner))
         }
      }

      all {
         languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
         languageSettings.useExperimentalAnnotation("kotlin.experimental.ExperimentalTypeInference")
      }
   }
}

tasks.named<Test>("jvmTest") {
   useJUnitPlatform()
   filter {
      isFailOnNoMatchingTests = false
   }
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(
         org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
         org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
      )
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
   }
}

apply(from = "../../publish-mpp.gradle.kts")
