package lifecycle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import io.kotest.property.lifecycle.afterProperty
import io.kotest.property.lifecycle.beforeProperty

class LifecyleTest : FunSpec() {
   init {

      var counter = 0

      beforeProperty {
         counter++
      }

      afterProperty {
         counter++
      }

      test("property test") {
         checkAll<String, String>(iterations = 31) { a, b -> a + b shouldBe "$a$b" }
      }

      afterSpec {
         counter shouldBe 62
      }
   }


}
