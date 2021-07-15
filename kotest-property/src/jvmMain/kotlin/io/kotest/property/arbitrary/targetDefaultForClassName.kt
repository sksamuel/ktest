package io.kotest.property.arbitrary

import io.kotest.property.Arb
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

@Suppress("UNCHECKED_CAST")
actual fun <A : Any> targetDefaultForClass(kclass: KClass<A>): Arb<A>? {
   return when {
      kclass.isSubclassOf(List::class) -> {
         val type = object : TypeReference<A>() {}.type as ParameterizedType
         val first = type.actualTypeArguments.first() as WildcardType
         val upper = first.upperBounds.first() as Class<*>
         Arb.list(defaultForClass<Any>(upper.kotlin) as Arb<Any>) as Arb<A>
      }
      kclass.isSubclassOf(Set::class) -> {
         val type = object : TypeReference<A>() {}.type as ParameterizedType
         val first = type.actualTypeArguments.first() as WildcardType
         val upper = first.upperBounds.first() as Class<*>
         Arb.set(defaultForClass<Any>(upper.kotlin) as Arb<Any>) as Arb<A>
      }
      kclass.isSubclassOf(Pair::class) -> {
         val type = object : TypeReference<A>() {}.type as ParameterizedType
         val first = (type.actualTypeArguments[0] as WildcardType).upperBounds.first() as Class<*>
         val second = (type.actualTypeArguments[1] as WildcardType).upperBounds.first() as Class<*>
         Arb.pair(defaultForClass<Any>(first.kotlin)!!, defaultForClass<Any>(second.kotlin)!!) as Arb<A>
      }
      kclass.isSubclassOf(Map::class) -> {
         val type = object : TypeReference<A>() {}.type as ParameterizedType
         // map key type can have or have not variance
         val first = if (type.actualTypeArguments[0] is Class<*>) {
            type.actualTypeArguments[0] as Class<*>
         } else {
            (type.actualTypeArguments[0] as WildcardType).upperBounds.first() as Class<*>
         }
         val second = (type.actualTypeArguments[1] as WildcardType).upperBounds.first() as Class<*>
         Arb.map(defaultForClass<Any>(first.kotlin)!!, defaultForClass<Any>(second.kotlin)!!) as Arb<A>
      }
      kclass == LocalDate::class -> Arb.localDate() as Arb<A>
      kclass == LocalDateTime::class -> Arb.localDateTime() as Arb<A>
      kclass == LocalTime::class -> Arb.localTime() as Arb<A>
      kclass == Period::class -> Arb.period() as Arb<A>
      kclass == BigDecimal::class -> Arb.bigDecimal() as Arb<A>
      kclass.isData -> Arb.bind(kclass)
      else -> null
   }
}

// need some supertype that types a type param so it gets baked into the class file
abstract class TypeReference<T> : Comparable<TypeReference<T>> {
   // this is the type of T
   val type: Type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]

   override fun compareTo(other: TypeReference<T>) = 0
}
