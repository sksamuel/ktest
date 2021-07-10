package io.kotest.core.internal

@Deprecated("Renamed to KotestEngineProperties. Will be removed in 4.8")
val KotestEngineSystemProperties = KotestEngineProperties

object KotestEngineProperties {

   @Deprecated("Use the val from the spring extension module (io.kotest.extensions:kotest-extensions-spring")
   const val springIgnoreWarning = "kotest.listener.spring.ignore.warning"

   const val gradle5 = "kotest.gradle5.compatibility"

   const val scriptsEnabled = "kotest.framework.scripts.enabled"

   const val dumpConfig = "kotest.framework.dump.config"

   /**
    * Sets the tag expression that determines included/excluded tags.
    */
   const val tagExpression = "kotest.tags"

   const val excludeTags = "kotest.tags.exclude"

   const val includeTags = "kotest.tags.include"

   const val propertiesFilename = "kotest.properties.filename"

   /**
    * If set to true, then source ref's will not be created for test cases.
    * This may reduce functionality (for example if using the intellij plugin).
    */
   const val disableSourceRef = "kotest.framework.sourceref.disable"

   /**
    * If set to true, disables the use of '!' as a prefix to disable tests.
    */
   const val disableBangPrefix = "kotest.bang.disable"

   /**
    * The default [IsolationMode] for specs.
    */
   const val isolationMode = "kotest.framework.isolation.mode"

   /**
    * The default [AssertionMode] for specs.
    */
   const val assertionMode = "kotest.framework.assertion.mode"

   /**
    * The default parallelism for specs.
    */
   const val parallelism = "kotest.framework.parallelism"

   const val timeout = "kotest.framework.timeout"

   const val projectTimeout = "kotest.framework.projectTimeout"

   const val invocationTimeout = "kotest.framework.invocation.timeout"

   const val concurrentSpecs = "kotest.framework.spec.concurrent"

   const val concurrentTests = "kotest.framework.test.concurrent"

   /**
    * Disable scanning the classpath for configuration classes by setting this property to true
    */
   const val disableConfigurationClassPathScanning = "kotest.framework.classpath.scanning.config.disable"

   /**
    * Disable scanning the classpath for listeners with @AutoScan by setting this property to true
    */
   const val disableAutoScanClassPathScanning = "kotest.framework.classpath.scanning.autoscan.disable"

   const val allowMultilineTestName = "kotest.framework.testname.multiline"

   /**
    *  If set -> filter testCases by this severity level and higher, else running all
    */
   const val testSeverity = "kotest.framework.test.severity"

   /**
    * Enable assert softly globally.
    * */
   const val globalAssertSoftly = "kotest.framework.assertion.globalassertsoftly"

   /**
    * Appends all tags associated with a test case to its display name.
    * */
   const val testNameAppendTags = "kotest.framework.testname.append.tags"

   const val duplicateTestNameMode = "kotest.framework.testname.duplicate.mode"

   const val disableJarDiscovery = "kotest.framework.discovery.jar.scan.disable"
}
