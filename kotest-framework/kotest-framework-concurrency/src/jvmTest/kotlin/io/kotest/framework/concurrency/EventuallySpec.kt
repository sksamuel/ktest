package io.kotest.framework.concurrency

import io.kotest.assertions.all
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

private fun Int.seconds(): Long = Duration.ofSeconds(this.toLong()).toMillis()
private fun Int.milliseconds(): Long = this.toLong()

@OptIn(ExperimentalKotest::class)
class EventuallySpec : FunSpec({
   test("eventually pass working tests") {
      eventually(5.seconds()) {
         System.currentTimeMillis()
      }
   }

   test("eventually passes tests that completed within the time allowed") {
      val end = System.currentTimeMillis() + 250L
      eventually(5.seconds()) {
         if (System.currentTimeMillis() < end)
            1 shouldBe 2
      }
   }

   test("eventually fails tests that do not complete within the time allowed") {
      shouldThrow<RuntimeException> {
         eventually(150L) {
            throw RuntimeException("foo")
         }
      }
   }

   test("eventually returns the result computed inside") {
      val result = eventually(2.seconds()) {
         1
      }
      result shouldBe 1
   }

   test("eventually passes tests that completed within the time allowed, AssertionError") {
      val end = System.currentTimeMillis() + 250
      eventually(5.seconds()) {
         if (System.currentTimeMillis() < end)
            assert(false)
      }
   }

   test("eventually fails tests that throw unexpected exception type") {
      shouldThrow<NullPointerException> {
         eventually(2.seconds()).suppressExceptions(IOException::class) {
            (null as String?)!!.length
         }
      }
   }

   test("eventually passes tests that throws FileNotFoundException for some time") {
      val end = System.currentTimeMillis() + 250
      eventually(5.seconds()).suppressExceptions(FileNotFoundException::class) {
         if (System.currentTimeMillis() < end)
            throw FileNotFoundException("foo")
      }
   }

   test("eventually handles kotlin assertion errors") {
      var thrown = false
      eventually(100.milliseconds()) {
         if (!thrown) {
            thrown = true
            throw AssertionError("boom")
         }
      }
   }

   test("eventually handles java assertion errors") {
      var thrown = false
      eventually(100.milliseconds()) {
         if (!thrown) {
            thrown = true
            throw java.lang.AssertionError("boom")
         }
      }
   }

   test("eventually displays the first and last underlying failures") {
      var count = 0
      val message = shouldThrow<AssertionError> {
         eventually(100.milliseconds()) {
            if (count == 0) {
               count = 1
               fail("first")
            } else {
               fail("last")
            }
         }
      }.message

      // TODO: add this assertion when we can use kotlin.time again
//         message.shouldContain("Eventually block failed after 100ms; attempted \\d+ time\\(s\\); FixedInterval\\(duration=25.0ms\\) delay between attempts".toRegex())
      message.shouldContain("The first error was caused by: first")
      message.shouldContain("The last error was caused by: last")
   }

   test("eventually allows suspendable functions") {
      eventually(100.milliseconds()) {
         delay(25)
         System.currentTimeMillis()
      }
   }

   test("eventually allows configuring interval delay") {
      var count = 0
      eventually(200.milliseconds()).withInterval(40.milliseconds().fixed()) {
         count += 1
      }
      count.shouldBeLessThan(6)
   }

   test("eventually does one final iteration if we never executed before interval expired") {
      val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
      async(dispatcher) {
         Thread.sleep(2000)
      }
      val counter = AtomicInteger(0)
      withContext(dispatcher) {
         // we won't be able to run in here
         eventually(1.seconds()).withInterval(100.milliseconds().fixed()) {
            counter.incrementAndGet()
         }
      }
      counter.get().shouldBe(1)
   }

   test("eventually does one final iteration if we only executed once and the last delay > interval") {
      val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
      // this will start immediately, free the dispatcher to allow eventually to run once, then block the thread
      async(dispatcher) {
         delay(100.milliseconds())
         Thread.sleep(500)
      }
      val counter = AtomicInteger(0)
      withContext(dispatcher) {
         // this will execute once immediately, then the earlier async will steal the thread
         // and then since the delay has been > interval and times == 1, we will execute once more
         eventually(250.milliseconds()).withInterval(25.milliseconds().fixed()) {
            counter.incrementAndGet() shouldBe 2
         }
      }
      counter.get().shouldBe(2)
   }

   test("eventually handles shouldNotBeNull") {
      measureTimeMillis {
         shouldThrow<java.lang.AssertionError> {
            eventually(50.milliseconds()) {
               val str: String? = null
               str.shouldNotBeNull()
            }
         }
      }.shouldBeGreaterThanOrEqual(50)
   }

   test("eventually with boolean predicate") {
      eventually(5.seconds()) {
         System.currentTimeMillis() > 0
      }
   }

   test("eventually with boolean predicate and interval") {
      eventually(5.seconds()).withInterval(1.seconds().fixed()) {
         System.currentTimeMillis() > 0
      }
   }

   test("eventually with T predicate") {
      var t = ""
      eventually(5.seconds()).withPredicate<String>({ it.result == "xxxx" }) {
         t += "x"
         t
      }
   }

   test("eventually with T predicate and interval") {
      var t = ""
      val result =
         eventually(5.seconds()).withInterval(250.milliseconds().fixed()).withPredicate({ it.result == "xxxxxxxxxxx" }) {
            t += "x"
            t
         }
      result shouldBe "xxxxxxxxxxx"
   }

   test("eventually with T predicate, interval, and listener") {
      var t = ""
      val latch = CountDownLatch(5)
      val result = eventually(5.seconds()).withInterval(250.milliseconds().fixed())
         .withListener<String>({ latch.countDown() })
         .withPredicate({ it.result == "xxxxxxxxxxx" }) {
            t += "x"
            t
         }

      latch.await(15, TimeUnit.SECONDS) shouldBe true
      result shouldBe "xxxxxxxxxxx"
   }

   test("eventually with T predicate, listener, and shortCircuit") {
      var t = ""
      val message = shouldThrow<EventuallyShortCircuitException> {
         eventually(5.seconds()).withInterval(250.milliseconds().fixed())
            .withShortCircuit<String>({ it.result == "xx" })
            .withPredicate({ it.result == "xxxxxxxxxxx" }) {
               t += "x"
               t
            }
      }.message

      all(message) {
         this.shouldContain("The provided shortCircuit function caused eventually to exit early")
         this.shouldContain("EventuallyState(result=xx")
      }
   }

   test("eventually fails tests that fail a predicate") {
      shouldThrow<AssertionError> {
         eventually(1.seconds()).withPredicate({ it.result == 2 }) {
            1
         }
      }
   }

   test("eventually supports fibonacci intervals") {
      var t = ""
      val latch = CountDownLatch(5)

      val result = eventually(10.seconds()).withInterval(200.milliseconds().fixed())
         .withPredicate({ latch.countDown(); it.result == "xxxxxx" }) {
            t += "x"
            t
         }

      latch.await(10, TimeUnit.SECONDS) shouldBe true
      result shouldBe "xxxxxx"
   }

   test("eventually has a shareable configuration and can be converted from basic config to generic config") {
      val slow = BasicEventuallyConfig(duration = 5.seconds())
      val fast = slow.copy(retries = 5)

      assertSoftly {
         slow.retries shouldBe Int.MAX_VALUE
         fast.retries shouldBe 5
         slow.patience.duration shouldBe 5.seconds()
         fast.patience.duration shouldBe 5.seconds()
      }

      slow {
         5
      }

      var t = ""
      fast.withPredicate(predicate = { it.result == "xxx" }) {
         t += "x"
         t
      }

      t shouldBe "xxx"
   }

   test("eventually throws if retry limit is exceeded") {
      val message = shouldThrow<AssertionError> {
         eventually(100000).withRetries(2) {
            1 shouldBe 2
         }
      }.message

      // TODO: re-enable this when we have kotlin.time again
//         message.shouldContain("Eventually block failed after Infinity")
      message.shouldContain("attempted 2 time(s)")
   }

   test("eventually overrides assertion to hard assertion before executing assertion and reset it after executing") {
      val target = System.currentTimeMillis() + 1000
      val message = shouldThrow<AssertionError> {
         all {
            withClue("Eventually which should pass") {
               eventually(2.seconds()) {
                  System.currentTimeMillis() shouldBeGreaterThan target
               }
            }
            withClue("1 should never be 2") {
               1 shouldBe 2
            }
            withClue("2 should never be 3") {
               2 shouldBe 3
            }
         }
      }.message

      message shouldContain "1) 1 should never be 2"
      message shouldContain "2) 2 should never be 3"
   }

   test("eventually calls the listener when an exception is thrown in the producer function") {
      var state: EventuallyState<Unit>? = null

      shouldThrow<Throwable> {
         eventually(250.milliseconds()).withRetries(1).withListener(listener = {
            if (state == null) {
               state = it
            }
         }) {
            withClue("1 should never be 2") {
               1 shouldBe 2
            }
         }
      }

      state.shouldNotBeNull()
      state?.firstError?.message shouldContain "1 should never be 2"
   }

   test("allows exception based on predicate") {
      var i = 0
      eventually(2.seconds()).withSuppressExceptionIf({ it.message == "foo" }) {
         if (i++ < 3) {
            throw AssertionError("foo")
         }
      }
   }

   test("does not allow an exception based on predicate") {
      shouldThrow<AssertionError> {
         var i = 0
         eventually(2.seconds()).withSuppressExceptionIf({ it.message == "bar" }) {
            if (i++ < 3) {
               throw AssertionError("foo")
            }
         }
      }.message shouldBe "foo"
   }

   test("allows a set of exceptions") {
      val exceptions = setOf(
         Pair(FileNotFoundException::class, FileNotFoundException()),
         Pair(AssertionError::class, AssertionError()),
         Pair(java.lang.RuntimeException::class, java.lang.RuntimeException())
      )
      var i = 0

      eventually(5.seconds()).suppressExceptions(*exceptions.map { it.first }.toTypedArray()) {
         exceptions.elementAtOrNull(i++)?.run {
            throw this.second
         }
      }

      i shouldBe exceptions.size + 1
   }
})
