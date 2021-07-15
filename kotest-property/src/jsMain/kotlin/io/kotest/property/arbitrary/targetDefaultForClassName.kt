package io.kotest.property.arbitrary

import io.kotest.property.Arb
import kotlin.reflect.KClass

@Suppress("NOTHING_TO_INLINE")
actual fun <A : Any> targetDefaultForClass(kclass: KClass<A>): Arb<A>? = null
