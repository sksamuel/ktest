package io.kotest.property.arbitrary

import io.kotest.property.Arb
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * Returns an [Arb] where each value is a randomly created instance of [A].
 * These instances are created by selecting the primaryConstructor of T and then
 * auto-detecting a generator for each parameter of that constructor.
 * T must be a class type.
 *
 * Note: This method only supports "basic" parameter types - string, boolean and so on,
 * and data classes. If your class has more complex requirements, you can use Arb.bind(gen1, gen2...)
 * where the parameter generators are supplied programatically.
 */
inline fun <reified A : Any> Arb.Companion.bind(): Arb<A> = Arb.bind(A::class)

fun <A : Any> Arb.Companion.bind(kclass: KClass<A>): Arb<A> {
   val constructor = kclass.primaryConstructor ?: error("could not locate a primary constructor")
   check(constructor.parameters.isNotEmpty()) { "${kclass.qualifiedName} constructor must contain at least 1 parameter" }

   val arbs: List<Arb<Any>> = constructor.parameters.map { param ->
      forClass(param.type.classifier as KClass<*>)
   }

   return Arb.bind(arbs) { params -> constructor.call(*params.toTypedArray()) }
}
