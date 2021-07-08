---
title: Changelog
sidebar_label: Changelog
slug: changelog.html
---

### [Next Release 5.0.0]

#### Breaking Changes

* `Arb.values` has been removed. This was deprecated back in 4.3 in favour of `Arb.sample`. Any custom arbs that override this method should be updated. Any custom arbs that use the `arbitrary` builders are not affected.
* The Engine no longer logs config to the console during start **by default**. To enable, set the system property `kotest.framework.dump.config` to true.
* Removed deprecated `shouldReceiveWithin` and `shouldReceiveNoElementsWithin` channel matchers.
* `equalJson` has an added parameter to support the new `shouldEqualSpecifiedJson` assertion

#### Fixes

* Finalize spec is now properly called in all situations #2272
* String matchers now also work on CharSequence where applicable #2278
* Annotations such as @Ignore and @Isolate now work when composed #2279

#### Improvements

* Unfinished tests now error
* Added `shouldEqualSpecifiedJson` to match a JSON structure on a subset of (specified) keys.

### 4.6.0 May 2021

This is a small release which **adds support for Kotlin 1.5** while remaining compatible with Kotlin 1.4.x

#### Bugfixes.

* All internal logging now uses lazy functions which offers a significant speed up on large test suites.
  Thanks to [Łukasz Wasylkowski](https://github.com/lwasyl) who spent considerable time tracking down this performance issue.
* Fixed false negative results by Inspectors when used inside assertSoftly. #2245

#### Features / Improvement

* Test config can now be specified at the test container level in addition to the leaf level #1370 #2050 #2065
* In data driven tests, added `IsStableType` annotation which when use on a type, kotest will call `toString` method on
  that type for creating test name. See [updated docs](https://kotest.io/docs/framework/data-driven-testing.html) #2248
* In data driven tests, added `WithDataTestName` interface which allow a type to modify the test name generated.
  See [updated docs](https://kotest.io/docs/framework/data-driven-testing.html) #2248
* Reflection methods are cached to avoid slow reflection calls.
* Added experimental versions of `eventually`, `until`, and `continually` that don't use `kotlin.time` internally.
  See [updated docs](https://kotest.io/docs/framework/concurrency/eventually.html) #2149
* Coroutines upgraded to 1.5 which also allows us to release assertions/property tests for watchosX64
* WatchosX64 artifacts released for assertions and property tests.

#### Contributors

* Ashish Kumar Joy
* Jim Schneidereit
* Łukasz Wasylkowski
* sksamuel


### 4.5.0 May 2021

As part of this release, third party extensions were promoted to top level repositories instead of modules inside the main kotest repo.
This allows the extensions to iterate quickly, without needing to wait for a full Kotest release.

From 4.5 onwards, the namespace for all extensions has changed to `io.kotest.extensions` and the versioning reset to 1.0.0.

So, for example, if you used the Spring extension, you would previously have added `io.kotest:kotest-extensions-spring:4.x.y` to your build.
Now you would use `io.kotest.extensions:kotest-extensions-spring:1.x.y`

See the full list of [extension modules](https://kotest.io/docs/extensions/extensions.html).


#### Breaking Changes
* In order to use `ExperimentalKotest` more broadly,
  it was moved from `io.kotest.core.config.ExperimentalKotest` to `io.kotest.common.ExperimentalKotest`. #1950
* In order to ensure the `EventuallyListener` is called in `eventually` when an exception is thrown the `ListenerState` field `result` was changed
  from type `T` to type `T?`. This will allow insight into when the eventually producer function is failing for whatever reason
  instead of appearing as if it is hanging. #2190
* Property tests now randomly cycle between edge cases and samples, rather than iterating all edge cases first. This allows greater number of edge cases to be used and avoids a combinatoral explosion. If you are implementing custom Arb's by extending the Arb class (instead of using the `arbitrary` builders), then you will need to adjust your edge cases method from `fun edgecases(): List<A>` to `fun edgecase(rs: RandomSource): A?`.
* Because of the above property test change, if you are setting a seed in a property test you may need to adjust the value.
* The kotlin stdlib dependencies are now marked as `compileOnly`, meaning the version in your build will be used. Kotest tries to maintain compatibility across multiple versions by not relying on features only available in the latest releases.
* Duplicated test names no longer throw an automatic error, but now mangle the name. So two tests of name 'foo' will appear as 'foo' and '(1) foo'. This enables data driven testing to work properly in javascript. To restore the original behavior, set the configuration value `Configuration.duplicateTestNameMode = Error`.

#### Features / Improvement
* A new data testing module has been added `kotest-framework-datatest` which properly supports runtime nesting of data tests. See [updated docs](https://kotest.io/docs/framework/data-driven-testing.html) #2078
* Added new matcher for DayOfWeek in `kotest-assertion-clock` module. #2124
* Added factory method to simplify creating new matchers. #2122
* Added method in `Exhaustive` to create a new `Exhaustive` which will be a cartesian product of given two `Exhaustive`. #2120
* Added support for writing tests inside an object as well as class. #1970
* Added suspend version of `shouldCompleteWithin`, `shouldCompleteBetween` and `shouldTimeOut`. #2107
* Added [kotest-extensions-wiremock](https://github.com/kotest/kotest-extensions-wiremock) project for managing lifecycle of `WireMockServer` in Kotest test. #2108
* Added [kotest-extensions-kafka](https://github.com/kotest/kotest-extensions-embedded-kafka) project for using embedded kafka in your tests
* Upgrade `klock` dependency to 2.0.6 and added `browser`, `nodejs`, `linuxX64`, `mingwX64`, `macosX64`, `tvos`,
  `iosX64`, `iosArm64` and `iosArm32` platform targets for `kotest-assertions-klock`. #2116
* Run eventually one final time if iterations is one and delay is greater than the interval #2105
* Some improvement around `eventually`.<br/>
  (1) Makes `EventuallyPredicate` a type alias instead of interface for better user experience.<br/>
  (2) Update failure message to inform the user about failure of given `EventuallyPredicate`.<br/>
  (3) Adds an overload of `eventually` which does not accept `EventuallyPredicate` so that it gives a feel of `until` function.
* Added `shouldBeEqualToComparingFields` and `shouldBeEqualToComparingFieldsExcept` matchers which check equality of
  actual and expected by comparing their fields instead of using `equals` method. #2197
* `one` and `any` have been added as alternatives to `assertSoftly`. These are suspending methods that will check that only a single
  assertion succeeded (in the case of `one`) or that at least one assertion succeeded (in the case of `any`). #1950
* New reporter added to generate HTML reports #2011
* `Exhaustive.cartesian` has been added #2119
* `kotest.tags` can now be set via ENV Vars #2098
* Edgecases are now probabilistic based #2112
* TestResult should support a reason why tests were skipped #2172
* Add watchos support back for x86 #2204
* Add overload to `Double.plusOrMinus` that accepts a percentage value instead of an absolute one. `1.0.plusOrMinus(10.percent)`


#### Bugfixes.
* Fixes eventually failing inside assert softly block without retrying the given lambda. #2092
* Fixes any other implementation of `Listener` apart from `ProjectListener` not getting picked by Kotest framework. #2088
* Fixes `EventuallyListener` not being called in `eventually` when the producer function throws an exception. #2190
* Use the classname as the default prefix for temporary files #2140
* Fix for `SystemExitListener` and _picocli_ framework #2156
* Fix for `Arb.choose(arb, arb2, ...)` not generating random values #2176
* Using checkAll, forAll and using take on an Arb cause an InvalidMutabilityException on XorWowRandom for Ios #2198
* Fix issues of passing vararg to another function in containsInOrder #2200
* The StringShrinker ignored min size limit #2213
* Fix for unlimited concurrency in spec execution when using experimental concurrency support #2177
* Synchronize access to Spring test contexts #2166
* Fixed typo in `haveClassAnnontations` matcher. Existing incorrect spelling is deprecated. #2133



#### Deprecations
* Deprecated `instanceOf`, `beInstanceOf`, `beTheSameInstanceAs`, `beOfType` of package `io.kotest.matchers` these will
  be removed permanently in 4.7 release, you can import these same assertion from `io.kotest.matchers.types`
  package.
* Remove deprecated eventually that uses durations for intervals. #2086
* Receivers used in test scopes have been renamed. For example, `DescribeScope` has become `DescribeSpecContainerContext`.
  The previous names exist as typealiases but are deprecated.
  This is only of importance if you implement custom spec types that inherit from the builtin specs or have defined
  extension methods on those scopes.


#### Contributors

* AJ Alt
* Alex Ordóñez
* Andreas Deininger
* Ashish Kumar Joy
* Dale King
* Hirotaka Kawata
* Hugo Martins
* Janek
* Jim Schneidereit
* Leonardo Colman
* Malte Esch
* Mateusz Kwieciński
* Mitchell Yuwono
* Nikita Klimenko
* Niklas Lochschmidt
* Rustam Musin
* Sean Flanigan
* Sebastian Schuberth
* Yoonho Sean Lee
* Zak Henry
* sksamuel
* tbcs


### 4.4.3 March 2021

* Removed verbose debugging statements that were erroneously left in the 4.4.2 release.

### 4.4.2 March 2021

Note: Release 4.4.2 is compiled against Kotlin 1.4.31 and coroutines 1.4.3

* Feature: The Javascript test artifacts are now compiled against the IR compiler in addition to current #2037
* Bugfix: `BeforeProjectListener` was not always being detected
* Bugfix: Using `shouldBe` with throwables would compare using the message only #2094
* Bugfix: Fix `withEnvironment()` to be case-insensitve on Windows #2099
* Bugfix: Fix `IncorrectDereferenceException` when calling assertions on background thread on native #2128
* Bugfix: Fix `Arb.bigdecimal` hanging for some combinations of min and maxvalues #2135
* Improvement: `eventually` sometimes only checks a single time despite short scheduled intervals #2089
* Improvement: Updates error message for `shouldContainKeys` matcher to includes keys which are not present in given map #2106
* Improvement: `haveCauseOfType` shows exception type instead of cause type #2131

### 4.4.1 February 2021

Note: Release 4.4.1 bumps the minimum required version of Kotlin to 1.4.30

* Fixed allure test grouping #1871
* Updated shouldBeEmpty and shouldNotBeEmpty to work for nullable references #2055
* Expose factor in exponential interval in eventually and until #2046
* Added cap to exponential and fibonacci intervals #2053
* Fix test name for data driven test when data class contains enum values #2034
* IntArray not being printed in Assert log #2042
* Fixed invalid json causing streaming error in json assertions #2045
* Generation of larger sets via Arb.set throws an exception #2051
* Avoid creating extra lambdas in blocking forAll #2036

### 4.4.0 February 2021

Note: Release 4.4.0 bumps the minimum required version of Kotlin to 1.4.21

#### Features / Improvements

* Add lazy property test generator #1651
* New map assertions #1697
* Property test framework is now deployed for native targets #1747
* Improve concurrency support for specs / tests #1760
* Variation of clue / asClue to accept lazy #1766
* New Map assertions shouldNotContainAnyKeysOf() shouldNotContainAnyValuesOf() #1769
* Add matchers for Atomic Booleans #1791
* Upgrade to Kotlin 1.4.20 #1800
* Test cases should have their tags appended to the test name if so configured #1804
* Add functionality to use 'it' without surrounding 'describe' #1827
* Added inline version for intanceOf alias #1838
* Add Support for globalAssertSoftly from System Property #1843
* Helper for temporary directory creation #1862
* shouldBeBetween not defined for floats #1927
* Increase arity of checkall / forall property tests to 12 #1929
* Add more configurations to Email Generator #1941
* not null matcher should show the value that was meant to be not null #1942
* Add SpringTestExtension which exposes test context via coroutine context element #1956
* Add active test extension #1959
* Upgrade ktor matchers to use ktor 1.5 #1965
* Allow data driven tests to register in root scope #1967
* Add domain arbitrary #1969
* Upgrade arrow matchers to 0.11.0 #1976
* Add alphanumeric codepoint arb #1989

#### Bugfixes

* Performance improvements for Exceptions on the JVM #1787
* Using a symbol or Japanese etc in the test name will change the behavior #1828
* Wrong behavior when combining assertSoftly and shouldNotBe #1831
* BehaviorSpec Then assertion shows green in Intellij but should show red #1857
* AssertionMode.Error doesn't work on FeatureSpec #1864
* Invalid test usage should throw at runtime #1882
* Output from reporters should be single threaded #1895
* Arb.set with a range hangs the test if the given gen inside the set cannot produce enough values for the range #1931

### 4.3.2 December 2020

#### Features

* Assertions library now released for watchos32 in addition to all other targets
* Allow using `it` for creating test outside of describe block
* Added Arb.lazy and Exhaustive.lazy

#### Bugfix

* A Kotlin 1.4 specific method was added in 4.3.1 and reverted in 4.3.2
* Arb.choose does not currently include edge cases from input arbs #1886
* String shrinking is not being executed #1860
* Arb.stringPattern slows down the test dramatically #1878
* AssertionMode.Error doesn't work on FeatureSpec #1864
* Incomplete edge cases with the double generator #1869
* Unexpected ToleranceMatcher behavior at infinite doubles #1832
* Wrong behavior when combining assertSoftly and shouldNotBe #1831
* Fixed shouldContainJsonKeyValue to work with Long expected value and integer actual value #1790


### 4.3.1 November 2020

#### Features

* Variation of clue / asClue to accept lazy #1766
* Added Tuple2..Tuple22 for use in data testing #1773

#### Improvements

* Stacktrace recovery when an `eventually` block fails #1775
* Performance improvements for Exceptions on the JVM #1787
* Updated discovery to only initialize spec classes #1788

#### Bugfix

* Added stable identifiers when using new data-driven tests with non-data classes #1795
* Wrong TimeoutException messages shown when test exceeds spec-level invocation timeout #1809
* Invocation timeouts should not be applied to containers #1810
* Arb.filter causing stackoverflow #1818
* Arb.shuffle type signature change broken in 4.3.0 #1824

### 4.3.0 October 2020 - [Blog](https://dev.to/kotest/kotest-release-4-3-2768)

#### Features

* New data driven test DSL with data classes #1537 (framework)
* Option to strip whitespace from test names #1545 (framework)
* EnabledIf annotation for specs #1683 (framework)
* Propagate coroutine context to tests #1725 (framework)
* Option to suppress configuration dump #1742 (framework)
* Added severity attribute for TestCase #1746 (framework)
* Added kotest.framework.sourceref.disable option (framework)
* Make Engine dependency free #1748 (framework)
* Multi-line-string comparison for file contents #823 (assertions)
* New assertion: Iterator.shouldHaveNext() #1660 (assertions)
* New assertions: isEmpty / isDefined for java Optional #1661 (assertions)
* Non infix matchers should return `this` for easy chaining #1744 (assertions)
* Add property test module for kotlinx datetime #1679 (prop-testing)
* Add Gen.forNone #1636 (prop-testing)
* Arb should generate a single value #1754 (prop-testing)
* Adds an arbitrary to generate bigdecimal #1705 (prop-testing)
* Add steps and stack trace to allure, with full docs #460 (extensions)
* Added roboelectric extension to main build (extensions)

#### Breaking Changes

* The `kotest-extensions-junit5extensions` module is now called `kotest-extensions-junit5`

### 4.2.6 October 2020

#### Features

* Added per project listener for testcontainers #1731

#### Bugfix

* Fixed regression in shouldBe when using iterables/arrays #1707 #1727
* Fix first failure in `beforeTest` blocks #1736
* Deprecate distinct #1730
* Fixed the empty allure result for tests with the failed afterTest block #1724

### 4.2.5 September 2020

* Bugfix: Fixed performance issue when using 1000s of tests in gradle #1693
* Feature: Added matchers for pair / triple components 1694
* Feature: Added shouldHaveNameWithoutExtension matcher for files and paths #1696
* Improvement: Added koin lifecycle mode #1710


### 4.2.4 September 2020

* Bugfix: Test time does not scale with number of tests #1685
* Bugfix: Added spring listener lifecycle mode #1643
* Bugfix: Fix and remove double negative in empty directory assertions
* Improvement: Duplicated test name exception should include test name #1686
* Improvement: SpringListener to generate meaningful method names #1591


### 4.2.3 September 2020

* Bugfix: Throwables of `Error` in the engine should be reported to test engine listeners
* Bugfix: Switched classgraph to api
* Bugfix: Make Set comparisons use .contains() instead of a linear search #1672
* Bugfix: Change retry default delay to 1 #1670
* Bugfix: removed 1.4 api usage from property tests
* Improvement: Allow retry to call suspend functions #1669
* Improvement: Add matcher alias for Iterator have next #1664
* Improvement: Add java.util.Optional matchers #1662
* Improvement: Expand ktor matchers to the client libraries #1658
* Improvement: Add `forNone` assertion #1654
* Improvement: Arb and Exhaustive should be covariant #1653
* Improvement: Remove the annoying `executionError` extra test in gradle #1655
* Improvement: Added more helpful error message when spec instantiation fails
* Docs: Update Gradle dependencies docs removing unnecessary -jvm suffix #1650
* Docs: MockServer extension documentation #1446

### 4.2.2 August 2020

* Bugfix: Usage of a Kotlin 1.4 only method has been removed
* Bugfix: KotlinReflectionInternalError fixed on java enums #1611
* Bugfix: Errors in a DiscoveryExtension were not propagated to output #1634
* Bugfix: Tags specified via inline tags dsl were not being picked up #1642
* Improvement: Updated output of some collection matchers to format an element per line #1380

### 4.2.1 August 2020

* Feature: The assertion libraries are now also published for watchos, tvos, and ios in addition to the macos, windows, and linux targets previously.

### 4.2.0 August 2020 - [Blog](blog/release_4.2.md)

* Feature: Kotest upgraded to use Kotlin 1.4.0 #1511
* Feature: Allow multiple project configs to be detected and merged #1632
* Feature: Allow case control in test's reports #1458
* Feature: Use expression for tags instead of include/exclude #863
* Feature: Add new scoped callbacks #1584
* Feature: Support order annotation for SpecOrder #1593
* Feature: Spec level overrides for timeout and invocation timeout #1551
* Improvement: Added exhaustive only mode to property tests #1596
* Improvement: Change instance of matchers to use generic contracts #1510
* Improvement: Allow to disable SpringListener on final class warning #1573
* Improvement: Bundle console runner with intellij plugin #1567
* Improvement: Improved error message for map should contain when key present #1587
* Improvement: Allow allure to be customizable #1527
* Improvement: Use the SPDX compliant license identifier "Apache-2.0" in POM files [#1517](https://github.com/kotest/kotest/issues/1517)
* Improvement: Use forAll(1) suspend parameters #1626
* Bugfix: Running all tests in a package doesn't run tests in subpackages #1621
* Bugfix: Can't run a single test method from Gradle 6.5 #1531
* Bugfix: TestFactory listeners not executing on nested tests #1613
* Bugfix: Disabling test execution with x-methods doesn't work with kotest-core-js #1623
* Bugfix: NoSuchFileException when using kotest-extensions-junitxml with Gradle #1581
* Bugfix: Non complete junit report when using FunSpec #999
* Breaking Change: kotest-core module has been replaced with kotest-framework-api and kotest-framework-engine. Tools authors can depend on api only. Engine should be used for JS testing. For JVM testing, continue to use kotest-runner-junit5-jvm.

### 4.1.2 July 2020

* Bugfix: Dkotest.tags.include= only takes into account @Tags [#1536](https://github.com/kotest/kotest/issues/1536) sksamuel
* Bugfix: Ensure exhaustive isn't build with an empty list [#1549](https://github.com/kotest/kotest/issues/1549) Cleidiano Oliveira
* Bugfix: Add concurrent spec runner and fix sequential spec runner [#1547](https://github.com/kotest/kotest/issues/1547) sksamuel
* Bugfix: Take into account `range` for `IntShrinker` and `LongShrinker` [#1535](https://github.com/kotest/kotest/issues/1535) sksamuel
* Feature: Support expressions for tags as an alternative to include/exclude [#863](https://github.com/kotest/kotest/issues/863) sksamuel
* Feature: Expand some matchers to Iterable [#1538](https://github.com/kotest/kotest/issues/1538) Leonardo Colman Lopes
* Improvement: Add the ability to make parameter substitutions when executing http files [#1560](https://github.com/kotest/kotest/issues/1560) Shane Lathrop
* Improvement: Added xGiven / xWhen / xThen options to Behavior spec [#1534](https://github.com/kotest/kotest/issues/1534) sksamuel
* Improvement: Added nicer syntax for Test Containers sksamuel
* Improvement: Restore context to describe [#1565](https://github.com/kotest/kotest/issues/1565) sksamuel
* Breaking Change: Updates method signature of assertSoftly to take object under test as argument Ashish Kumar Joy

### 4.1.1 June 2020

* Bugfix: Issue with describe spec and the intellij plugin fixed [#1528](https://github.com/kotest/kotest/issues/1528)
* Bugfix: Incorrect error message with Exhaustive's when under the min iteration count [#1526](https://github.com/kotest/kotest/issues/1526)

### 4.1.0 June 2020 - [Blog](blog/release_4.1.md)

* Feature: The Kotest IntelliJ plugin has gone final. The plugin requires 4.1. or higher of Kotest. https://plugins.jetbrains.com/plugin/14080-kotest
* Feature: Highlight diff when comparing data classes [#826](https://github.com/kotest/kotest/issues/826) [#1242](https://github.com/kotest/kotest/issues/1242)
* Feature: Improve error message in tolerance matchers [#1230](https://github.com/kotest/kotest/issues/1230)
* Feature: Add Arb for (lat, long) [#1304](https://github.com/kotest/kotest/issues/1304)
* Feature: Integration with Testcontainers [#1353](https://github.com/kotest/kotest/issues/1353)
* Feature: x variants for Behavior / Feature / Expect spec [#1383](https://github.com/kotest/kotest/issues/1383)
* Feature: Add property test global config with before / after prop test callbacks [#1435](https://github.com/kotest/kotest/issues/1435)
* Feature: Parallel execution test cases in Spec [#1362](https://github.com/kotest/kotest/issues/1362)
* Feature: Add variable.assertSoftly [#1427](https://github.com/kotest/kotest/issues/1427)
* Feature: Coroutine helper for timeout [#1447](https://github.com/kotest/kotest/issues/1447)
* Feature: Add timeout to apply to individual tests when invocations > 1 [#1442](https://github.com/kotest/kotest/issues/1442)
* Feature: Add shouldExistInOrder matcher [#1460](https://github.com/kotest/kotest/issues/1460)
* Feature: Added Arb.orNull [#1414](https://github.com/kotest/kotest/issues/1414)
* Feature: Provide a way to remove test prefixes in the test output when using specs which use prefixes [#1486](https://github.com/kotest/kotest/issues/1486)
* Feature: Adds shouldCompleteExceptionallyWith matcher [#1454](https://github.com/kotest/kotest/issues/1454)
* Feature: Exhaustive.merge for two gens with a common supertype [#1502](https://github.com/kotest/kotest/issues/1502)
* Improvement: Added Byte.shouldBeBetween(min, max) and Arb.bytes [#1408](https://github.com/kotest/kotest/issues/1408)
* Improvement: Remove kotlintest aliases [#1457](https://github.com/kotest/kotest/issues/1457)
* Improvement: Parent scopes are not coroutine scopes [#1488](https://github.com/kotest/kotest/issues/1488)
* Improvement: isolation instead of isolationMode [#1418](https://github.com/kotest/kotest/issues/1418)
* Improvement: Reflection equality improvements [#1413](https://github.com/kotest/kotest/issues/1413)
* Improvement: Property tests should report exception of running shrunk input [#1279](https://github.com/kotest/kotest/issues/1279)
* Improvement: Make beforeProject and afterProject as suspend function [#1461](https://github.com/kotest/kotest/issues/1461)
* Improvement: Updated arb flat map to accept lists [#1500](https://github.com/kotest/kotest/issues/1500)
* Improvement: Date generators should allow for specific dates to be selected [#1354](https://github.com/kotest/kotest/issues/1354)
* Bugfix: Test cases with multiline names broken [#1441](https://github.com/kotest/kotest/issues/1441)
* Bugfix: Before\AfterProject catch only one Exception [#1387](https://github.com/kotest/kotest/issues/1387)
* Bugfix: Arb.bind() calls the incorrect constructor [#1487](https://github.com/kotest/kotest/issues/1487)
* Bugfix: Project config dump doesn't include enums properly [#1379](https://github.com/kotest/kotest/issues/1379)
* Bugfix: Add Arb.choose that accepts weighted arbs [#1499](https://github.com/kotest/kotest/issues/1499)
* Bugfix: Arb.list doesn't use ListShrinker [#1493](https://github.com/kotest/kotest/issues/1493)

### 4.0.6 June 2020

* Bugfix: Dependencies of assertions-core are now included properly when not using junit runner [#1425](https://github.com/kotest/kotest/issues/1425)
* Bugfix: checkAll would fail if exhaustive size was very large [#1456](https://github.com/kotest/kotest/issues/1456)
* Bugfix: Show typeclass on java.nio.filePath would cause stack overflow [#1313](https://github.com/kotest/kotest/issues/1313)

### 4.0.5 April 2020

* Bugfix: Focus mode would cause some nested tests to be ignored [#1376](https://github.com/kotest/kotest/issues/1376)
* Bugfix: Arb.choice would include edge cases in the generated values [#1406](https://github.com/kotest/kotest/issues/1406)
* Bugfix: Arb.int and Arb.long edge cases included values outside the specified ranged [#1405](https://github.com/kotest/kotest/issues/1405)

### 4.0.4 April 2020

* Bugfix: Exceptions of type `LinkageError`, most commonly `ExceptionInInitializerError` were not being handled [#1381](https://github.com/kotest/kotest/issues/1381)

### 4.0.3 April 2020

* Feature: Koin support now works for koin 2.1 [#1357](https://github.com/kotest/kotest/issues/1357)
* Deprecation: String context is deprecated in ShouldSpec in favour of a context block. [#1356](https://github.com/kotest/kotest/issues/1356)
* Improvement: Line breaks added to Collection.containExactly matcher [#1380](https://github.com/kotest/kotest/issues/1380)
* Improvement: Tolerance matcher emits better failure message (including plus/minus values) [#1230](https://github.com/kotest/kotest/issues/1230)
* Bugfix: Project config output now writes the correct values of test ordering and isolation mode [#1379](https://github.com/kotest/kotest/issues/1379)
* Bugfix: Order of autoclose is restored to work like 3.4.x (was undefined in 4.0.x) [#1384](https://github.com/kotest/kotest/issues/1384)
* Bugfix: Fix shouldContainExactly for arrays [#1364](https://github.com/kotest/kotest/issues/1364)

### 4.0.2 April 2020

* Feature: Added filter and map to Exhaustives [#1343](https://github.com/kotest/kotest/issues/1343)
* Feature: shouldBeInteger matcher using contracts [#1315](https://github.com/kotest/kotest/issues/1315)
* Bugfix: Fixed issue with xdescribe in describe spec always being active
* Bugfix: Simple tags were using full class names rather than the simple name breaking backwards compatibility [#1346](https://github.com/kotest/kotest/issues/1346)
* Improvement: Caching result of discovery for increased performance in maven [#1325](https://github.com/kotest/kotest/issues/1325)
* Bugfix: Closing resources used in classgraph scan [#1323](https://github.com/kotest/kotest/issues/1323)
* Bugfix: Fixed timeout for coroutine launched inside a test without its own scope [#1345](https://github.com/kotest/kotest/issues/1345)
* Bugfix: Fix Arb.bind returning only the same value [#1348](https://github.com/kotest/kotest/issues/1348)
* Bugfix: Restored usage of opentest4j assertions [#1339](https://github.com/kotest/kotest/issues/1339)
* Bugfix: Fixed missing stacktrace in data driven testing [#1336](https://github.com/kotest/kotest/issues/1336)
* Bugfix: Fixed Arb.instant always returning same value [#1322](https://github.com/kotest/kotest/issues/1322)
* Bugfix: Added workaround for gradle 5 bugs.

### 4.0.1 March 2020

* Improvement: Bumped kotlin to 1.3.71
* Feature: Aded latlong Arb [#1304](https://github.com/kotest/kotest/issues/1304)

### 4.0.0 March 2020

The 4.0.0 release is a large release. With the project rename, the packages have changed and module names have changed.

Major changes:

* The KotlinTest project is now multi-platform. This means most of the modules now require -jvm to be added if you are working server side JVM only. For example, `io.kotlintest:kotlintest-runner-junit5` is now `io.kotest:kotest-runner-junit5-jvm` taking into account package name changes and the platform suffix.
* The main assertions library is now `kotest-assertions-core` and many new assertions (matchers) have been added. This changelog won't list them all. It is simpler to view the [full list](assertions/matchers.md).
* The property test library has moved to a new module `kotest-property` and been reworked to include many new features. The old property test classes are deprecated and will be removed in a future release.
* Many new property test generators have been added. The full list is [here](proptest/gens.md).
* Composable specs have been added in the form of _Test Factories_.
* Project config no longer requires placing in a special package name, but can be placed anywhere in the [classpath](framework/project_config.md).
* @Autoscan has been added for [listeners](framework/listeners.md) and extensions.
* Added DSL version of test lifecycle [callbacks](framework/listeners.md#dsl-methods).

Minor changes.

* Feature: A new JSoup assertions module has been added. [#1028](https://github.com/kotest/kotest/issues/1028)
* Feature: Stats matchers [#851](https://github.com/kotest/kotest/issues/851)
* Feature: Experimental Robolectric Support [#926](https://github.com/kotest/kotest/issues/926)
* Bugfix: shouldNotThrowAny return T instead of Unit [#981](https://github.com/kotest/kotest/issues/981)
* Internal: Removed dependency on Arrow to avoid version conflicts
* Feature: Project wide default test case config
* Feature: whenReady(f) has been replaced with f.whenReady which is coroutine enabled
* Feature: Alphabetic test case ordering
* Feature: All test callbacks are now coroutine enabled
* Feature: forEachAsClue
* Improvement: Support Koin 2.1.0
* Improvement: Explicitly allow internal classes as specs
* Feature: Klock matcher support [#1214](https://github.com/kotest/kotest/issues/1214)
* Feature: JDBC matcher support [#1221](https://github.com/kotest/kotest/issues/1221)


### 3.4.2

* Bugfix: Enhances SpringListener to work correctly with all Spring's Listeners [#950](https://github.com/kotest/kotest/issues/950)

### 3.4.1

* Internal: Remove JUnit redeclarations [#927](https://github.com/kotest/kotest/issues/927)
* Feature: Add infix modifier to more Arrow matchers [#921](https://github.com/kotest/kotest/issues/921)
* Feature: BigDecimal range matchers [#932](https://github.com/kotest/kotest/issues/932)
* Feature: monotonically/strictly increasing/decreasing matcher [#850](https://github.com/kotest/kotest/issues/850)
* Feature: Fixes shouldBe and shouldNotBe comparison [#913](https://github.com/kotest/kotest/issues/913)
* Feature: Add overload to Ktor shouldHaveStatus matcher [#914](https://github.com/kotest/kotest/issues/914)
* Feature: Fail parent tests when child tests fail [#935](https://github.com/kotest/kotest/issues/935)

### 3.4.0

* Feature: Support for running tests with Koin [#907](https://github.com/kotest/kotest/issues/907)
* Feature: Global timeout option can be applied across all tests [#858](https://github.com/kotest/kotest/issues/858)
* Feature: Introduced await as a more feature rich version of eventually [#888](https://github.com/kotest/kotest/issues/888) [#793](https://github.com/kotest/kotest/issues/793)
* Feature: Array overloads for all matchers [#904](https://github.com/kotest/kotest/issues/904)
* Feature: Support Spring's Test Listeners [#887](https://github.com/kotest/kotest/issues/887)
* Feature: Limit Parallelism for some specs [#786](https://github.com/kotest/kotest/issues/786)
* Feature: Added new project listener [#859](https://github.com/kotest/kotest/issues/859)
* Feature: Change System extensions to support different modes [#843](https://github.com/kotest/kotest/issues/843)
* Feature: Print project configurations [#841](https://github.com/kotest/kotest/issues/841) [#866](https://github.com/kotest/kotest/issues/866)
* Feature: New date matcher variations for month, time units, day of week, etc [#899](https://github.com/kotest/kotest/issues/899)
* Feature: Multi line diff min line config option [#706](https://github.com/kotest/kotest/issues/706)
* Feature: Allow nested describe scope in DescribeSpec [#905](https://github.com/kotest/kotest/issues/905)
* Feature: Add matcher for Dates to ignore timezone [#891](https://github.com/kotest/kotest/issues/891)
* Feature: Reflection matchers [#614](https://github.com/kotest/kotest/issues/614) [#894](https://github.com/kotest/kotest/issues/894)
* Feature: Added string matchers for single line and size between [#853](https://github.com/kotest/kotest/issues/853)
* Feature: Added contracts and lambda variations of matchers for arrow types [#802](https://github.com/kotest/kotest/issues/802) [#890](https://github.com/kotest/kotest/issues/890) [#834](https://github.com/kotest/kotest/issues/834)
* Feature: Added matchers for LocalTime [#889](https://github.com/kotest/kotest/issues/889)
* Feature: Added Zoned and Offset date time variants of shouldBeToday [#820](https://github.com/kotest/kotest/issues/820)
* Feature: Add new throwable matchers [#864](https://github.com/kotest/kotest/issues/864)
* Feature: Added matchers for Result [#836](https://github.com/kotest/kotest/issues/836) [#861](https://github.com/kotest/kotest/issues/861)
* Feature: Added big decimal matchers [#875](https://github.com/kotest/kotest/issues/875)
* Feature: Added shouldBeSymbolicLink and shouldHaveParent matchers for files [#871](https://github.com/kotest/kotest/issues/871)
* Feature: Json Matchers from resources [#873](https://github.com/kotest/kotest/issues/873)
* Feature: Added shouldBeZero and shouldNotBeZero matcher for number types [#819](https://github.com/kotest/kotest/issues/819) [#848](https://github.com/kotest/kotest/issues/848)
* Feature: Added shouldContainFiles matcher for path [#854](https://github.com/kotest/kotest/issues/854)
* Feature: The URI matchers should also work on URLs. [#818](https://github.com/kotest/kotest/issues/818)
* Feature: Allow setting isolation mode in project config [#842](https://github.com/kotest/kotest/issues/842)
* Feature: Added containFileDeep File matcher [#846](https://github.com/kotest/kotest/issues/846)
* Feature: Implements SkipTestException [#805](https://github.com/kotest/kotest/issues/805)
* Feature: Implements Infinity and NaN Double Matchers [#801](https://github.com/kotest/kotest/issues/801)
* Feature: Add asClue helper function [#784](https://github.com/kotest/kotest/issues/784)
* Feature: Add infix map matchers using Pair [#792](https://github.com/kotest/kotest/issues/792)
* Feature: Add Short and Btyte primitive gens [#773](https://github.com/kotest/kotest/issues/773)
* Feature: Implement Gen.take(n) function [#758](https://github.com/kotest/kotest/issues/758)
* Feature: Implement Gen.next(predicate) function [#759](https://github.com/kotest/kotest/issues/759)
* Feature: Add support to change sizes of generated lists, sets, maps [#757](https://github.com/kotest/kotest/issues/757)
* Feature: Allow exclusion/inclusion tags at runtime [#761](https://github.com/kotest/kotest/issues/761)
* Bugfix: Added missing part of shouldHaveLength message [#870](https://github.com/kotest/kotest/issues/870))
* Bugfix: Updated json matchers to include actual json in the error
* Bugfix: Fix for before/after test listeners not failing tests [#842](https://github.com/kotest/kotest/issues/842) [#865](https://github.com/kotest/kotest/issues/865)
* Bugfix: Changed autoClose to accept an AutoCloseable [#847](https://github.com/kotest/kotest/issues/847)
* Bugfix: Fixed left vs right issue [#612](https://github.com/kotest/kotest/issues/612)
* Bugfix: Ensure specs that fail in initialisation fail a Maven build [#832](https://github.com/kotest/kotest/issues/832)
* Bugfix: Fixed test engine reporting when there is an exception in either the init block, beforeSpec or the afterSpec method [#771](https://github.com/kotest/kotest/issues/771)
* Internal: io.kotlintest.Result renamed to io.kotlintest.MatcherResult to avoid conflict with new Kotlin class kotlin.Result [#898](https://github.com/kotest/kotest/issues/898)

### 3.3.0

* Feature: Intellij Plugin now available!
* Feature: FunSpec now allows parent context blocks
* Feature: java.time between matcher ([#694](https://github.com/kotest/kotest/issues/694))
* Feature: Constant 'now' listeners ([#693](https://github.com/kotest/kotest/issues/693))
* Feature: PITest plugin ([#687](https://github.com/kotest/kotest/issues/687))
* Feature: Spring mocking injection @MockBean @MockkBean ([#684](https://github.com/kotest/kotest/issues/684))
* Feature: instanceOf and typeOf matchers to use the casted value ([#695](https://github.com/kotest/kotest/issues/695))
* Feature: Digest Matchers [#667](https://github.com/kotest/kotest/issues/667)
* Feature: continually assertion function [#643](https://github.com/kotest/kotest/issues/643)
* Feature: Add project config option for `assertSoftly` [#512](https://github.com/kotest/kotest/issues/512) ([#655](https://github.com/kotest/kotest/issues/655))
* Feature: Implement System Security Manager Extensions ([#640](https://github.com/kotest/kotest/issues/640))
* Feature: Implement System Environment Extension ([#633](https://github.com/kotest/kotest/issues/633))
* Feature: Implement shouldBeOneOf matcher and assertions ([#647](https://github.com/kotest/kotest/issues/647))
* Feature: Add nullability matchers with Kotlin Contracts ([#602](https://github.com/kotest/kotest/issues/602)) ([#646](https://github.com/kotest/kotest/issues/646))
* Feature: SystemProperty Test Helpers [#524](https://github.com/kotest/kotest/issues/524) ([#608](https://github.com/kotest/kotest/issues/608))
* Feature: Timezone / Locale Extension [#587](https://github.com/kotest/kotest/issues/587) ([#609](https://github.com/kotest/kotest/issues/609))
* Feature: Move extensions to Kotlintest-Extensions module ([#629](https://github.com/kotest/kotest/issues/629))
* Feature: Provide range-based numeric generators and javax.time generators [#530](https://github.com/kotest/kotest/issues/530) ([#543](https://github.com/kotest/kotest/issues/543))
* Feature: Extended word spec ([#635](https://github.com/kotest/kotest/issues/635))
* Feature: Implement shouldNotThrow matchers ([#603](https://github.com/kotest/kotest/issues/603))
* Improvement: Make "condensed" multi-line diffs configurable [#607](https://github.com/kotest/kotest/issues/607)
* Improvement: Allow Arrow Either extensions to support nullable types ([#613](https://github.com/kotest/kotest/issues/613))
* Improvement: Enables test bang on all specs ([#606](https://github.com/kotest/kotest/issues/606))
* Improvement: Add property testing extensions for custom generators ([#506](https://github.com/kotest/kotest/issues/506))
* Improvement: Added issue flag in config [#525](https://github.com/kotest/kotest/issues/525)
* Bugfix: Added support for package selectors from junit discovery requests [#597](https://github.com/kotest/kotest/issues/597)
* Bugfix: Disabled top level tests are not marked as ignored in JUnit [#656](https://github.com/kotest/kotest/issues/656)
* Bugfix: Fix containOnlyOnce which return true when no occurrence ([#660](https://github.com/kotest/kotest/issues/660))
* Internal: Auto deploy snapshot on each travis build
* Internal: Remove all deprecated matchers/assertions ([#653](https://github.com/kotest/kotest/issues/653))


### 3.2.1

* Feature: AnnotationSpec now has a `expected` exception configuration [#527](https://github.com/kotest/kotest/issues/527) [#559](https://github.com/kotest/kotest/issues/559)
* Feature: BehaviorSpec gained extra nesting possibilities, with `And` between any other keywords [#562](https://github.com/kotest/kotest/issues/562) [#593](https://github.com/kotest/kotest/issues/593)
* Bugfix: Independent tests were sharing a thread, and one test could timeout a different one for apparently no reason [#588](https://github.com/kotest/kotest/issues/588) [#590](https://github.com/kotest/kotest/issues/590)
* Improvement: Documentation on TestConfig.invocations clarified [#591](https://github.com/kotest/kotest/issues/591) [#592](https://github.com/kotest/kotest/issues/592)


### 3.2.0

* Feature: Support for coroutines directly from tests [#386](https://github.com/kotest/kotest/issues/386)
* Feature: Isolation mode added to more finely control the instances in which tests execute [#379](https://github.com/kotest/kotest/issues/379)
* Feature: When re-running tests, execute previously failing specs first [#388](https://github.com/kotest/kotest/issues/388)
* Feature: Support for @Before and @After in AnnotationSpec for easier migration from JUnit [#513](https://github.com/kotest/kotest/issues/513)
* Feature: Support package selectors in discovery [#461](https://github.com/kotest/kotest/issues/461)
* Improvement: The test listeners have been reworked to make them more powerful and clearer [#494](https://github.com/kotest/kotest/issues/494)
* Improvement: Better support for multi-line string comparisions [#402](https://github.com/kotest/kotest/issues/402)
* Improvement: Gen.oneOf should be covariant [#471](https://github.com/kotest/kotest/issues/471)
* Improvement: Double should have oppostive matchers for shouldBePositive and shouldBeNegative [#435](https://github.com/kotest/kotest/issues/435)
* Improvement: New matchers [#393](https://github.com/kotest/kotest/issues/393) [#325](https://github.com/kotest/kotest/issues/325)
* Bugfix: BehaviorSpec doesn't allow config bug [#495](https://github.com/kotest/kotest/issues/495)
* Bugfix: Error when throwing AssertionError from inside a shouldThrow{} block [#479](https://github.com/kotest/kotest/issues/479)
* Bugfix: Fix test timeouts [#476](https://github.com/kotest/kotest/issues/476)
* Bugfix: Fix annotation spec failure message [#539](https://github.com/kotest/kotest/issues/539)
* Internal: Build now uses Kotlin 1.3 [#379](https://github.com/kotest/kotest/issues/379)
* Internal: Upgraded class scanning to use ClassGraph instead of Reflections [#459](https://github.com/kotest/kotest/issues/459)

### 3.1.11

* Feature: Infix support to String matchers [#443](https://github.com/kotest/kotest/issues/443)
* Feature: Infix support to files, floats, sequences, types and uri matchers [#445](https://github.com/kotest/kotest/issues/445)
* Feature: Infix support to Double matchers [#429](https://github.com/kotest/kotest/issues/429)
* Feature: Infix suport to Map matchers [#417](https://github.com/kotest/kotest/issues/417)
* Feature: `shouldNotBePositive` and `shouldNotBeNegative` for Double matchers [#435](https://github.com/kotest/kotest/issues/435)
* Feature: Support for Duration in time matchers [#423](https://github.com/kotest/kotest/issues/423)
* Feature: arrow-assertion Failure matcher that checks underlying throwable equality [#427](https://github.com/kotest/kotest/issues/427)
* Feature: `shouldNotBeTrue` and `shouldNotBeFalse` for Boolean matchers [#452](https://github.com/kotest/kotest/issues/452)
* Improvement: Documentation for `Gen.int()` [#419](https://github.com/kotest/kotest/issues/419)
* Improvement: Javadocs for Date matchers [#420](https://github.com/kotest/kotest/issues/420)
* Improvement: Better error message for empty collection in matchers [#438](https://github.com/kotest/kotest/issues/438)
* Improvement: Better stacktrace filtering from Failures class [#465](https://github.com/kotest/kotest/issues/465)
* Bugfix: Double matcher `shouldNotBeExactly` had the wrong implementation [#432](https://github.com/kotest/kotest/issues/432)
* Bugfix: Single-thread test had `before` and `after` running in separate thread from the test [#447](https://github.com/kotest/kotest/issues/447)
* Bugfix: Test with invocations > 1 wouldn't complete if test failed [#413](https://github.com/kotest/kotest/issues/413)
* Bugfix: Wrong assertion on `shouldThrow` [#479](https://github.com/kotest/kotest/issues/479) [#484](https://github.com/kotest/kotest/issues/484)



### 3.1.10

* Feature: Infix version of some inline matchers, eg `date1 shouldHaveSameYearAs date2` ([#404](https://github.com/kotest/kotest/issues/404) [#407](https://github.com/kotest/kotest/issues/407) [#409](https://github.com/kotest/kotest/issues/409))
* Feature: Infix support for int and long matchers ([#400](https://github.com/kotest/kotest/issues/400))
* Feature: Added startsWith/endsWith matchers on collections ([#393](https://github.com/kotest/kotest/issues/393))
* Improvement: Use unambiguous representations in collection matchers ([#392](https://github.com/kotest/kotest/issues/392))
* Improvement: Collection matchers now work on `Sequence` too ([#391](https://github.com/kotest/kotest/issues/391))
* Improvement: Added shouldThrowUnit variant of shouldThrow ([#387](https://github.com/kotest/kotest/issues/387))
* Fix: shouldBe on arrays without static type ([#397](https://github.com/kotest/kotest/issues/397))

### 3.1.9

* Feature: Add soft assertions ([#373](https://github.com/kotest/kotest/issues/373))
* Feature: `sortedWith` (and related) matchers. ([#383](https://github.com/kotest/kotest/issues/383))
* Improvement: Removed unnecessary `Comparable<T\>` upper-bound from `sortedWith` matchers. ([#389](https://github.com/kotest/kotest/issues/389))
* Improvement: Improve StringShrinker algorithm ([#377](https://github.com/kotest/kotest/issues/377))
* Bugfix: shouldBeBetween should be shouldBe instead of shouldNotBe ([#390](https://github.com/kotest/kotest/issues/390))
* Bugfix: Fix beLeft is comparing against Either.Right instead of Either.Left ([#374](https://github.com/kotest/kotest/issues/374))
* Internal: Naming executor services for jmx monitoring

### 3.1.8

* Bugfix: Skip tests when MethodSelector is set [#367](https://github.com/kotest/kotest/issues/367) (means can run a single test in intellij)
* Bugfix: Fix error when running single test in kotlintest-tests ([#371](https://github.com/kotest/kotest/issues/371))
* Bugfix Fix table testing forNone and Double between matcher ([#372](https://github.com/kotest/kotest/issues/372))
* Improvement: Remove matcher frames from stacktraces ([#369](https://github.com/kotest/kotest/issues/369))
* Improvement: Use less ambiguous string representations in equality errors ([#368](https://github.com/kotest/kotest/issues/368))
* Improvement: Improve String equality error messages ([#366](https://github.com/kotest/kotest/issues/366))
* Internal: Update kotlin to 1.2.50 ([#365](https://github.com/kotest/kotest/issues/365))

### 3.1.7

* Feature: Added Int/Long.shouldBeNegative and Int/Long.shouldBePositive matchers [#325](https://github.com/kotest/kotest/issues/325)
* Feature: Added Double.shouldBeNegative and Double.shouldBePositive matchers [#325](https://github.com/kotest/kotest/issues/325)
* Feature: Added collection.shouldBeLargerThan(c), collection.shouldBeSmallerThan(c), collection.shouldBeSameSizeAs(c) [#325](https://github.com/kotest/kotest/issues/325)
* Feature: Added collection.shouldHaveAtLeastSize(n) and collection.shouldHaveAtMostSize(n) matchers.
* Feature: Added matcher for uri.opaque
* Feature: Add matchers containsExactly and containsExactlyInAnyOrder ([#360](https://github.com/kotest/kotest/issues/360))
* Feature: Added test case filters
* Bugfix: Running single tests with Gradle command line [#356](https://github.com/kotest/kotest/issues/356)
* Change: Removed coroutine support until it is no longer experimental
* Improvement: Optimize sorted matcher ([#359](https://github.com/kotest/kotest/issues/359))
* Improvement: Allow type matchers to match nullable values. ([#358](https://github.com/kotest/kotest/issues/358))
* Improvement: Allow nullable receivers for string matchers. ([#352](https://github.com/kotest/kotest/issues/352))
* Improvement: Run tests for all rows in a table, even after errors. ([#351](https://github.com/kotest/kotest/issues/351))

### 3.1.6

* Specs now support co-routines [#332](https://github.com/kotest/kotest/issues/332)
* Extension function version of inspectors.
* Inspectors for arrow NonEmptyLists
* New style of data driven tests with parameter name detection
* Extension function style of assert all for property testing
* Updated string matchers to show better error when input is null or empty string
* Allow nullable arguments to more matcher functions. [#350](https://github.com/kotest/kotest/issues/350)
* Added extension functions for table tests [#349](https://github.com/kotest/kotest/issues/349)

### 3.1.5

* Fix for bug in gradle which doesn't support parallel test events
* Bring back Duration extension properties [#343](https://github.com/kotest/kotest/issues/343)
* Added fix for gradle 4.7 issues [#336](https://github.com/kotest/kotest/issues/336)
* shouldBe does not handle java long  [#346](https://github.com/kotest/kotest/issues/346)
* Fixing function return type in documentation for forAll() ([#345](https://github.com/kotest/kotest/issues/345))
* Fixing typos in reference.md ([#344](https://github.com/kotest/kotest/issues/344))
* Make the Table & Row data classes covariant ([#342](https://github.com/kotest/kotest/issues/342))
* Fixing argument names in ReplaceWith of deprecated matchers ([#341](https://github.com/kotest/kotest/issues/341))

### 3.1.4

* Fix eventually nanos conversion ([#340](https://github.com/kotest/kotest/issues/340))
* Improve array shouldBe overloads ([#339](https://github.com/kotest/kotest/issues/339))

### 3.1.3

* Added workaround for gradle 4.7/4.8 error [#336](https://github.com/kotest/kotest/issues/336)
* Fix URI path and URI parameter matchers ([#338](https://github.com/kotest/kotest/issues/338))

### 3.1.2

* Added arrow NonEmptyList isUnique matchers
* Added Float and List Shrinker
* Added inspecting and extracting helper functions. ([#334](https://github.com/kotest/kotest/issues/334))
* Allow tags to be added to specs for all test cases [#333](https://github.com/kotest/kotest/issues/333)
* Support randomized order of top level tests [#328](https://github.com/kotest/kotest/issues/328)

### 3.1.1

* Focus option for top level tests [#329](https://github.com/kotest/kotest/issues/329)
* Improve shrinkage [#331](https://github.com/kotest/kotest/issues/331)
* Updated readme for custom generators [#313](https://github.com/kotest/kotest/issues/313)
* Added generator for UUIDs
* Fixed bug with auto-close not being called. Deprecated ProjectExtension in favour of TestListener.
* Added a couple of edge case matchers to the arrow extension; added arrow matchers for lists.

Version 3.1.0
----------

* **Simplified Setup**

In KotlinTest 3.1.x it is sufficient to enable JUnit in the test block of your gradle build
 instead of using the gradle junit plugin. This step is the same as for any test framework
 that uses the JUnit Platform.

Assuming you have gradle 4.6 or above, then setup your test block like this:

```groovy
test {
    useJUnitPlatform()
}
```

You can additionally enable extra test logging:

```groovy
test {
    useJUnitPlatform()
    testLogging {
        events "FAILED", "SKIPPED", "STANDARD_OUT", "STANDARD_ERROR"
    }
}
```

* **Instance Per Test for all Specs**

In the 3.0.x train, the ability to allow an instance per test was removed from some spec styles due to
implementation difficulties. This has been addressed in 3.1.x and so all spec styles now allow instance
per test as in the 2.0.x releases. Note: The default value is false, so tests will use a single shared
instance of the spec for all tests unless the `isInstancePerTest()` function is overridden to return true.

* **Breaking Change: Config Syntax**

The syntax for config has now changed. Instead of a function call after the test has been defined, it is
now specified after the name of the test.

So, instead of:

```kotlin
"this is a test" {
}.config(...)
```

You would now do:

```kotlin
"this is a test".config(...) {
}
```

* **Matchers as extension functions**

All matchers can now be used as extension functions. So instead of:

```kotlin
file should exist()

or

listOf(1, 2) should containNull()
```

You can do:

```kotlin
file.shouldExist()

or

listOf(1, 2).shouldContainNull()
```

Note: The infix style is **not** deprecated and will be supported in future releases, but the extension function
is intended to be the preferred style moving forward as it allows discovery in the IDE.

* **Dozens of new Matchers**

_even_ and _odd_

Tests that an Int is even or odd:

```kotlin
4 should beEven()
3 shouldNot beEven()

3 should beOdd()
4 shouldNot beOdd()
```

_beInRange_

Asserts that an int or long is in the given range:

```kotlin
3 should beInRange(1..10)
4 should beInRange(1..3)
```

_haveElementAt_

Checks that a collection contains the given element at a specified index:

```kotlin
listOf("a", "b", "c") should haveElementAt(1, "b")
listOf("a", "b", "c") shouldNot haveElementAt(1, "c")
```

Help out the type inferrer when using nulls:

```kotlin
listOf("a", "b", null) should haveElementAt<String?>(2, null)
```

_readable_, _writeable_, _executable_ and _hidden_

Tests if a file is readable, writeable, or hidden:

```kotlin
file should beRadable()
file should beWriteable()
file should beExecutable()
file should beHidden()
```

_absolute_ and _relative_

Tests if a file's path is relative or absolute.

```kotlin
File("/usr/home/sam") should beAbsolute()
File("spark/bin") should beRelative()
```

_startWithPath(path)_

Tests if a file's path begins with the specified prefix:

```kotlin
File("/usr/home/sam") should startWithPath("/usr/home")
File("/usr/home/sam") shouldNot startWithPath("/var")
```

_haveSameHashCodeAs(other)_

Asserts that two objects have the same hash code.

```kotlin
obj1 should haveSameHashCodeAs(obj2)
"hello" shouldNot haveSameHashCodeAs("world")
```

_haveSameLengthAs(other)_

Asserts that two strings have the same length.

```kotlin
"hello" should haveSameLengthAs("world")
"hello" shouldNot haveSameLengthAs("you")
```

_haveScheme, havePort, haveHost, haveParameter, havePath, haveFragment_

Matchers for URIs:

```kotlin
val uri = URI.create("https://localhost:443/index.html?q=findme#results")
uri should haveScheme("https")
uri should haveHost("localhost")
uri should havePort(443)
uri should havePath("/index.html")
uri should haveParameter("q")
uri should haveFragment("results")
```

* Date matchers - before / after / haveSameYear / haveSameDay / haveSameMonth / within
* Collections - containNull, containDuplicates
* Futures - completed, cancelled
* String - haveLineCount, contain(regex)
* Types - haveAnnotation(class)

* **Arrow matcher module**

A new module has been added which includes matchers for [Arrow](http://arrow-kt.io) - the popular and awesome
 functional programming library for Kotlin. To include this module add `kotlintest-assertions-arrow` to your build.

The included matchers are:

_Option_ - Test that an `Option` has the given value or is a `None`. For example:

```kotlin
val option = Option.pure("foo")
option should beSome("foo")

val none = None
none should beNone()
```

_Either_- Test that an `Either` is either a `Right` or `Left`. For example:

```kotlin
Either.right("boo") should beRight("boo")
Either.left("boo") should beLeft("boo")
```

_NonEmptyList_- A collection (no pun intended) of matchers for Arrow's `NonEmptyList`.
These mostly mirror the equivalent `Collection` matchers but for NELs. For example:

```kotlin
NonEmptyList.of(1, 2, null).shouldContainNull()
NonEmptyList.of(1, 2, 3, 4).shouldBeSorted<Int>()
NonEmptyList.of(1, 2, 3, 3).shouldHaveDuplicates()
NonEmptyList.of(1).shouldBeSingleElement(1)
NonEmptyList.of(1, 2, 3).shouldContain(2)
NonEmptyList.of(1, 2, 3).shouldHaveSize(3)
NonEmptyList.of(1, 2, 3).shouldContainNoNulls()
NonEmptyList.of(null, null, null).shouldContainOnlyNulls()
NonEmptyList.of(1, 2, 3, 4, 5).shouldContainAll(3, 2, 1)
```

_Try_ - Test that a `Try` is either `Success` or `Failure`.

```kotlin
Try.Success("foo") should beSuccess("foo")
Try.Failure<Nothing>(RuntimeException()) should beFailure()
```

_Validation_ - Asserts that a `Validation` is either `Valid` or an `Invalid`

```kotlin
Valid("foo") should beValid()
Invalid(RuntimeException()) should beInvalid()
```

* **Generator Bind**

A powerful way of generating random class instances from primitive generators is to use the new `bind` function.
A simple example is to take a data class of two fields, and then use two base generators and bind them to create
random values of that class.

```kotlin
data class User(val email: String, val id: Int)

val userGen = Gen.bind(Gen.string(), Gen.positiveIntegers(), ::User)

assertAll(userGen) {
  it.email shouldNotBe null
  it.id should beGreaterThan(0)
}
```

* **Property Testing: Classify**

When using property testing, it can be useful to see the distribution of values generated, to ensure you're getting
 a good spread of values and not just trival ones. For example, you might want to run a test on a String and you want to
 ensure you're getting good amounts of strings with whitespace.

To generate stats on the distribution, use classify with a predicate, a label if the predicate passes, and a label
 if the predicate fails. For example:

```kotlin
assertAll(Gen.string()) { a ->
    classify(a.contains(" "), "has whitespace", "no whitespace")
    // some test
}
```

And this will output something like:

```
63.70% no whitespace
36.30% has whitespace
```

So we can see we're getting a good spread of both types of value.

You don't have to include two labels if you just wish to tag the "true" case, and you can include more than one
 classification. For example:

```kotlin
forAll(Gen.int()) { a ->
    classify(a == 0, "zero")
    classify(a % 2 == 0, "even number", "odd number")
    a + a == 2 * a
}
```

This will output something like:

```
51.60% even number
48.40% odd number
0.10% zero
```

* **Property Testing: Shrinking**

* **Tag Extensions**

A new type of extension has been added called `TagExtension`. Implementations can override the `tags()` function
defined in this interface to dynamically return the `Tag` instances that should be active at any moment. The existing
system properties `kotlintest.tags.include` and `kotlintest.tags.exclude` are still valid and are not deprecated, but
adding this new extension means extended scope for more complicated logic at runtime.

An example might be to disable any Hadoop tests when not running in an environment that doesn't have the hadoop
home env variable set. After creating a `TagExtension` it must be registered with the project config.

```kotlin
object Hadoop : Tag()

object HadoopTagExtension : TagExtension {
  override fun tags(): Tags =
      if (System.getenv().containsKey("HADOOP_HOME")) Tags.include(Hadoop) else Tags.exclude(Hadoop)
}

object MyProjectConfig : AbstractProjectConfig() {
  override fun extensions(): List<Extension> = listOf(HadoopTagExtension)
}

object SimpleTest : StringSpec({
  "simple test" {
    // this test would only run on environments that have hadoop configured
  }.config(tags = setOf(Hadoop))
})
```

* **Discovery Extensions: instantiate()**

Inside the `DiscoveryExtension` interface the function `fun <T : Spec> instantiate(clazz: KClass<T\>): Spec?` has been added which
allows you to extend the way new instances of `Spec` are created. By default, a no-args constructor is assumed. However, if this
function is overridden then it's possible to support `Spec` classes which have other constructors. For example, the Spring module
now supports constructor injection using this extension. Other use cases might be when you want to always inject some config class,
or if you want to ensure that all your tests extend some custom interface or superclass.

As a reminder, `DiscoveryExtension` instances are added to Project config.

* **System out / error extensions**

An extension that allows you to test for a function that writes to System.out or System.err. To use this extension add
 the module `kotlintest-extensions-system` to your build.

By adding the `NoSystemOutListener` or `NoSystemErrListener` to your config or spec classes, anytime a function tries to write
 to either of these streams, a `SystemOutWriteException` or `SystemErrWriteException` will be raised with the string that
 the function tried to write. This allows you to test for the exception in your code.

For example:

```kotlin
class NoSystemOutOrErrTest : StringSpec() {

  override fun listeners() = listOf(NoSystemOutListener, NoSystemErrListener)

  init {

    "System.out should throw an exception when the listener is added" {
      shouldThrow<SystemOutWriteException> {
        System.out.println("boom")
      }.str shouldBe "boom"
    }

    "System.err should throw an exception when the listener is added" {
      shouldThrow<SystemErrWriteException> {
        System.err.println("boom")
      }.str shouldBe "boom"
    }
  }
}
```

* **System.exit extension**

Another extension that is part of the `kotlintest-extensions-system` module. This extension will allow you to test
 if `System.exit(Int)` is invoked in a function. It achieves this by intercepting any calls to System.exit and instead
 of terminating the JVM, it will throw a `SystemExitException` with the exit code.

For example:

```kotlin
class SystemExitTest : StringSpec() {

  override fun listeners() = listOf(SpecSystemExitListener)

  init {

    "System.exit should throw an exception when the listener is added" {
      shouldThrow<SystemExitException> {
        System.exit(123)
      }.exitCode shouldBe 123
    }
  }
}
```

* **Spring Module Updates**

The spring extension module `kotlintest-extensions-spring` has been updated to allow for constructor injection.
This new extension is called `SpringAutowireConstructorExtension` and must be added to your `ProjectConfig.
Then you can use injected dependencies directly in the primary constructor of your test class.

For example:

```kotlin
@ContextConfiguration(classes = [(Components::class)])
class SpringAutowiredConstructorTest(service: UserService) : WordSpec({
  "SpringListener" should {
    "have autowired the service" {
      service.repository.findUser().name shouldBe "system_user"
    }
  }
})
```

* **JUnit 4 Runner**

A JUnit 4 runner has been added which allows KotlinTest to run using the legacy JUnit 4 platform.
To use this, add `kotlintest-runner-junit4` to your build instead of `kotlintest-runner-junit5`.

Note: This is intended for use when junit5 cannot be used.
It should not be the first choice as functionality is restricted.

Namely:

* In intellij, test output will not be nested
* Project wide beforeAll/afterAll cannot be supported.

Version 3.0.x - March 29 2018
-------------

* **Module split out**

KotlinTest has been split into multiple modules. These include core, assertions, the junit runner, and extensions such as spring,
allure and junit-xml.

The idea is that in a future release, further runners could be added (TestNG) or for JS support (once multi-platform Kotlin is out of beta).
When upgrading you will typically want to add the  `kotlintest-core`,  `kotlintest-assertions` and `kotlintest-runner-junit5` to your build
rather than the old `kotlintest` module which is now defunct. When upgrading, you might find that you need to update imports
to some matchers.

```
testCompile 'io.kotlintest:kotlintest-core:3.0.0'
testCompile 'io.kotlintest:kotlintest-assertions:3.0.0'
testCompile 'io.kotlintest:kotlintest-runner-junit5:3.0.0'
```

Gradle Users:

Also you _must_ include `apply plugin: 'org.junit.platform.gradle.plugin'` in your project and
`classpath "org.junit.platform:junit-platform-gradle-plugin:1.1.0"` to the `dependencies` section of your `buildscript`
or tests will not run (or worse, will hang). This allows gradle to execute
_jUnit-platform-5_ based tests (which KotlinTest builds upon). Note: Gradle says that this is **not** required as of 4.6 but even
with 4.6 it seems to be required.

Maven users:

You need to include the following in your plugins:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.19.1</version>
    <dependencies>
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-surefire-provider</artifactId>
            <version>1.1.0</version>
        </dependency>
    </dependencies>
</plugin>
```

And you must include

```xml
        <dependency>
            <groupId>io.kotlintest</groupId>
            <artifactId>kotlintest-runner-junit5</artifactId>
            <version>${kotlintest.version}</version>
            <scope>test</scope>
        </dependency>
```

as a regular dependency.

* **Breaking: ProjectConfig**

Project wide config in KotlinTest is controlled by implementing a subclass of `AbstractProjectConfig`. In previous versions you could
call this what you wanted, and place it where you wanted, and `KotlinTest` would attempt to find it and use it. This was the cause of
many bug reports about project start up times and reflection errors. So in version 3.0.x onwards, KotlinTest will
no longer attempt to scan the classpath.

Instead you must call this class `ProjectConfig` and place it in a package `io.kotlintest.provided`. It must still be a subclass of
`AbstractProjectConfig` This means kotlintest can do a simple `Class.forName` to find it, and so there is no
 startup penalty nor reflection issues.

Project config now allows you to register multiple types of extensions and listeners, as well as setting parallelism.

* **Breaking: Interceptors have been deprecated and replaced with Listeners**

The previous `inteceptors` were sometimes confusing. You had to invoke the continuation function or the spec/test
would not execute. Not invoking the function didn't mean the spec/test was skipped, but that it would hang.

So interceptors are deprecated, and in some places removed. Those are not removed are now located in classes called
`SpecExtension` and `TestCaseExtension` and those interfaces should be used rather than functions directly.

Here is an example of a migrated interceptor.

```kotlin
val mySpecExtension = object : SpecExtension {
    override fun intercept(spec: Spec, process: () -> Unit) {
      println("Before spec!")
      process()
      println("After spec!")
    }
}
```

As a replacement, in 3.0.0 we've added the `TestListener` interface which is the more traditional before/after style callbacks.
In addition, these methods include the result of the test (success, fail, error, skipped) which gives you more
context in writing plugins. The `TestListener` interface offers everything the old interceptors could do, and more.

Here is an example of a simple listener.

```kotlin
object TimeTracker : TestListener {

  var started = 0L

  override fun beforeTest(description: Description) {
    TimeTrackerTest.started = System.currentTimeMillis()
  }

  override fun afterTest(description: Description, result: TestResult) {
    val duration = System.currentTimeMillis() - TimeTrackerTest.started
    println("Test ${description.fullName()} took ${duration}ms")
  }
}
```

If you want to use these methods in a Spec itself, then you can just override the functions
 directly because a Spec is already a TestListener.

```kotlin
object TimeTracker : WordSpec() {

  var started = 0L

  override fun beforeTest(description: Description) {
    started = System.currentTimeMillis()
  }

  override fun afterTest(description: Description, result: TestResult) {
    val duration = System.currentTimeMillis() - started
    println("Test ${description.fullName()} took ${duration}ms")
  }

  init {
    "some test" should {
      "be timed" {
        // test here
      }
    }
  }
}
```

Listeners can be added project wide by overriding `listeners()` in the `ProjectConfig`.

Note: In the next release, new `Extension` functions will be added which will be similar to the old interceptors, but with
 complete control over the lifecycle. For instance, a future intercept method will enforce that the user skip, run or abort a test
 in the around advice. They will be more complex, and so suited to more advanced use cases. The new `TestListener` interface will remain of course, and is the preferred option.

* **Parallelism**

If you want to run more than one spec class in parallel, you can by overriding `parallelism` inside your projects
`ProjectConfig` or by supplying the system property `kotlintest.parallelism`.

Note the system property always takes precedence over the config.

* **Futures Support**

Test cases now support waiting on futures in a neat way. If you have a value in a `CompletableFuture` that you want
to test against once it completes, then you can do this like this:

```kotlin
val stringFuture: CompletableFuture<String> = ...

"My future test" should {
  "support CompletableFuture<T\>" {
    whenReady(stringFuture) {
      it shouldBe "wibble"
    }
  }
}
```

* **Breaking: Exception Matcher Changes**

The `shouldThrow<T\>` method has been changed to also test for subclasses. For example, `shouldThrow<IOException>` will also match
exceptions of type `FileNotFoundException`. This is different to the behavior in all previous KotlinTest versions. If you wish to
have functionality as before - testing exactly for that type - then you can use the newly added `shouldThrowExactly<T\>`.

* **JUnit XML Module**

Support for writing out reports in junit-format XML has added via the `kotlintest-extensions-junitxml` module which you will need to add to your build. This module
provides a `JUnitXmlListener` which you can register with your project to autowire your tests. You can register this by overriding
`listeners()` in `ProjectConfig`.

```kotlin
class ProjectConfig : AbstractProjectConfig() {
    override fun listeners() = listOf(JUnitXmlListener)
}
```

* **Spring Module**

Spring support has been added via the `kotlintest-extensions-spring` module which you will need to add to your build. This module
provides a `SpringListener` which you can register with your project to autowire your tests. You can register this for just some classes
by overriding the `listeners()` function inside your spec, for example:

```kotlin
class MySpec : ParentSpec() {
    override fun listeners() = listOf(SpringListener)
}
```

Or you can register this for all classes by adding it to the `ProjectConfig`. See the section on _ProjectConfig_ for how
to do this.

* **Breaking: Tag System Property Rename**

The system property used to include/exclude tags has been renamed to `kotlintest.tags.include` and `kotlintest.tags.exclude`. Make
sure you update your build jobs to set the right properties as the old ones no longer have any effect. If the old tags are detected
then a warning message will be emitted on startup.

* **New Matchers**

`beInstanceOf<T\>` has been added to easily test that a class is an instance of T. This is in addition to the more verbose `beInstanceOf(SomeType::class)`.

The following matchers have been added for maps: `containAll`, `haveKeys`, `haveValues`. These will output helpful error messages showing you
which keys/values or entries were missing.

New matchers added for Strings: `haveSameLengthAs(other)`, `beEmpty()`, `beBlank()`, `containOnlyDigits()`, `containADigit()`, `containIgnoringCase(substring)`,
`lowerCase()`, `upperCase()`.

New matchers for URIs: `haveHost(hostname)`, `havePort(port)`, `haveScheme(scheme)`.

New matchers for collections: `containNoNulls()`, `containOnlyNulls()`

* **Breaking: One instance per test changes**

One instance per test is no longer supported for specs which offer _nested scopes_. For example, `WordSpec`. This is because of the tricky
nature of having nested closures work across fresh instances of the spec. When using one instance per test, a fresh spec class is required
for each test, but that means selectively executing some closures and not others in order to ensure the correct state. This has proved
the largest source of bugs in previous versions.

KotlinTest 3.0.x takes a simplified approach. If you want the flexibilty to lay out your tests with nested scopes, then all tests will
execute in the same instance (like Spek and ScalaTest). If you want each test to have it's own instance (like jUnit) then you can either
split up your tests into multiple files, or use a "flat" spec like `FunSpec` or `StringSpec`.

This keeps the implementation an order of magnitude simplier (and therefore less likely to lead to bugs) while offering a pragmatic approach
to keeping both sets of fans happy.

* **New Specs**

Multiple new specs have been added. These are: `AnnotationSpec`, `DescribeSpec` and `ExpectSpec`. Expect spec allows you to use the `context`
and `expect` keywords in your tests, like so:

```kotlin
class ExpectSpecExample : ExpectSpec() {
  init {
    context("some context") {
      expect("some test") {
        // test here
      }
      context("nested context even") {
        expect("some test") {
          // test here
        }
      }
    }
  }
}
```

The `AnnotationSpec` offers functionality to mimic jUnit, in that tests are simply functions annotated with `@io.kotlintest.specs.Test`. For example:

```kotlin
class AnnotationSpecExample : AnnotationSpec() {

  @Test
  fun test1() {

  }

  @Test
  fun test2() {

  }
}
```

And finally, the `DescribeSpec` is similar to SpekFramework, using `describe`, `and`, and `it`. This makes it very useful for those people who are looking
to migrate to KotlinTest from SpekFramework.

```kotlin
class DescribeSpecExample : DescribeSpec() {
  init {
    describe("some context") {
      it("test name") {
        // test here
      }
      describe("nested contexts") {
        and("another context") {
          it("test name") {
            // test here
          }
        }
      }
    }
  }
}
```

* **Property Testing with Matchers**

The ability to use matchers in property testing has been added. Previously property testing worked only with functions that returned a Boolean, like:

```kotlin
"startsWith" {
  forAll(Gen.string(), Gen.string(), { a, b ->
    (a + b).startsWith(a)
  })
}
```

But now you can use `assertAll` and `assertNone` and then use regular matchers inside the block. For example:

```kotlin
"startsWith" {
  assertAll(Gen.string(), Gen.string(), { a, b ->
    a + b should startWith(a)
  })
}
```

This gives you the ability to use multiple matchers inside the same block, and not have to worry about combining all possible errors
into a single boolean result.

* **Generator Edge Cases**

Staying with property testing - the _Generator_ interface has been changed to now provide two types of data.

The first are values that should always be included - those edge cases values which are common sources of bugs.
For example, a generator for Ints should always include values like zero, minus 1, positive 1, Integer.MAX_VALUE and Integer.MIN_VALUE.
Another example would be for a generator for enums. That should include _all_ the values of the enum to ensure
each value is tested.

The second set of values are random values, which are used to give us a greater breadth of values tested.
The Int generator should return random ints from across the entire integer range.

Previously generators used by property testing would only include random values, which meant you were very unlikely to see the
edge cases that usually cause issues - like the aforementioned Integer MAX / MIN. Now you are guaranteed to get the edge
cases first and the random values afterwards.

* **Breaking: MockitoSugar removed**

This interface added a couple of helpers for Mockito, and was used primarily before Kotlin specific mocking libraries appeared.
Now there is little value in this mini-wrapper so it was removed. Simply add whatever mocking library you like to your build
and use it as normal.

* **CsvDataSource**

This class has been added for loading data for table testing. A simple example:

```kotlin
class CsvDataSourceTest : WordSpec() {
  init {

    "CsvDataSource" should {
      "read data from csv file" {

        val source = CsvDataSource(javaClass.getResourceAsStream("/user_data.csv"), CsvFormat())

        val table = source.createTable<Long, String, String>(
            { it: Record -> Row3(it.getLong("id"), it.getString("name"), it.getString("location")) },
            { it: Array<String> -> Headers3(it[0], it[1], it[2]) }
        )

        forAll(table) { a, b, c ->
          a shouldBe gt(0)
          b shouldNotBe null
          c shouldNotBe null
        }
      }
    }
  }
}
```

* **Matcher Negation Errors**

All matchers now have the ability to report a better error when used with `shouldNot` and `shouldNotBe`. Previously a generic error
was generated - which was usually the normal error but with a prefix like "NOT:" but now each built in matcher will provide a full message, for example: `Collection should not contain element 'foo'`


Version 2.0.0, released 2017-03-26
----------------------------------

[Closed Issues](https://github.com/kotlintest/kotlintest/milestone/4?closed=1)

### Added

* You can write tests alternatively into a lambda parameter in the class constructor, eg:

```kotlin
class StringSpecExample : StringSpec({
  "strings.size should return size of string" {
    "hello".length shouldBe 5
    "hello" should haveLength(5)
  }
})
```

* Added `forNone` for table tests, eg

```kotlin
val table = table(
    headers("a", "b"),
    row(0L, 2L),
    row(2L, 2L),
    row(4L, 5L),
    row(4L, 6L)
)

forNone(table) { a, b ->
  3 shouldBe between(a, b)
}
```

* Interceptors have been added. Interceptors allow code to be executed before and after a test. See the main readme for more info.

* Simplified ability to add custom matchers. Simple implement `Matcher<T\>` interface. See readme for more information.

* Added `shouldNot` to invert matchers. Eg, `"hello" shouldNot include("hallo")`

* Deprecated matchers which do not implement Matcher<T\>. Eg, `should have substring(x)` has been deprecated in favour of `"hello" should include("l")`. This is because instances of Matcher<T\> can be combined with `or` and `and` and can be negated with `shouldNot`.

* Added `between` matcher for int and long, eg

```3 shouldBe between(2, 5)```

* Added `singleElement` matcher for collections, eg

```x shouldBe singleElement(y)```

* Added `sorted` matcher for collections, eg

```listOf(1,2,3) shouldBe sorted<Int>()```

* Now supports comparsion of arrays [#116](https://github.com/kotest/kotest/issues/116)

* Added Gen.oneOf<Enum/> to create a generator that returns one of the values for the given Enum class.

### Changed

* Tags are objects derived from `Tag` class now.
* Tags can now be included and/or exluded. It is no longer the case that all untagged tests are
always executed.
* Fixed bugs with parenthesis breaking layout in Intellij #112

### Removed

* FlatSpec was removed because it has an irregular syntax with `config` and is essentially the same
as StringSpec, but more complicated.
* Deprecated method overloads with `duration: Long, unit: TimeUnit`
* `expecting` for testing exceptions (use shouldThrow now)


Version 1.3.2, released 2016-07-05
----------------------------------

### Changed

* Added `a shouldBe exactly(b)` matcher for doubles

* `kotlintest` only pulls in `mockito-core` now instead of `mockito-all`


Version 1.3.1, released 2016-07-03
----------------------------------

### Changed

* Bumped Kotlin version to 1.0.3

Version 1.3.0, released 2016-07-03
----------------------------------

[Closed Issues](https://github.com/kotlintest/kotlintest/issues?utf8=%E2%9C%93&q=is%3Aclosed+milestone%3A2.0)

### Added

* StringSpec. You can use simply use Strings as the basis for tests, eg:

```kotlin
class StringSpecExample : StringSpec() {
  init {
    "strings.size should return size of string" {
      "hello".length shouldBe 5
      "hello" should haveLength(5)
    }

    "strings should support config" {
      "hello".length shouldBe 5
    }.config(invocations = 5)
  }
}
```

* Table Tests. Tables allow you to manually specific combinations of values that should be used, and are useful for
edge cases and other specific values you want to test. The headers are used for when values fail,
the output can show you what inputs were used for what labels. An example of using a table consisting of two-value tuples:

```kotlin
class TableExample : StringSpec(), TableTesting {
  init {
    "numbers should be prime" {
      val table = table(
          headers("a", "b"),
          row(5, 5),
          row(4, 6),
          row(3, 7)
      )
      forAll(table) { a, b ->
        a + b == 10
      }
    }
  }
}
```

* Property tests. Property tests automatically generate values for testings. You provide, or have KotlinTest provide for you, `generators`, which will generate a set of values and the unit test will be executed for each of those values. An example using two strings and asserting that the lengths are correct:

```kotlin
class PropertyExample: StringSpec() {

  "String size" {
    forAll({ a: String, b: String ->
      (a + b).length == a.length + b.length
    })
  }

}
```

That test will be executed 100 times with random values in each test. See more in the readme.

* autoClose. Fields of type `Closeable` can be registered for automatic resource closing:

```kotlin
class StringSpecExample : StringSpec() {
  val reader = autoClose(StringReader("xyz"))

  ...
}
```

* `haveLength` matcher. You can now write for strings:

```kotlin
someString should haveLength(10)
```


* `haveSize` matcher. You can now write for collections:

```kotlin
myCollection should haveSize(4)
```

* `contain` matcher. You can now write

```kotlin
val col = listOf(1,2,3,4,5)
col should contain(4)
```

* `containInAnyOrder` matcher. You can now write

```kotlin
val col = listOf(1,2,3,4,5)
col should containInAnyOrder(4,2,3)
```

* `haveKey` Map<K,V> matcher. You can now write

```kotlin
val map = mapOf(Pair(1, "a"), Pair(2, "b"))
map should haveKey(1)
```

* `haveValue` Map<K,V> matcher. You can now write

```kotlin
val map = mapOf(Pair(1, "a"), Pair(2, "b"))
map should haveValue("a")
```

* `contain` Map<K,V> matcher. You can now write

```kotlin
val map = mapOf(Pair(1, "a"), Pair(2, "b"))
map should contain(1, "a")
```

* `beTheSameInstanceAs` reference matcher. This is an alias for `x should be theSameInstanceAs(y)`, allowing `x should beTheSameInstanceAs(y)` which fits in with new matcher style.

### Changed

### Replaced `timeout` + `timeUnit` with `Duration` ([#29](https://github.com/kotlintest/kotlintest/issues/29))

You can now write `config(timeout = 2.seconds)` instead of
`config(timeout = 2, timeoutUnit = TimeUnit.SECONDS)`.

### Deprecated

nothing

### Removed

nothing

### Fixed

* Ignored tests now display properly. https://github.com/kotlintest/kotlintest/issues/43
* Failing tests reported as a success AND a failure https://github.com/kotlintest/kotlintest/issues/42
