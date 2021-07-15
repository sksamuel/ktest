package io.kotest.property

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

typealias BeforeProperty = suspend () -> Unit
typealias AfterProperty = suspend () -> Unit

data class BeforePropertyContextElement(val before: BeforeProperty) :
   AbstractCoroutineContextElement(BeforePropertyContextElement) {
   companion object Key : CoroutineContext.Key<BeforePropertyContextElement>
}

data class AfterPropertyContextElement(val after: AfterProperty) :
   AbstractCoroutineContextElement(AfterPropertyContextElement) {
   companion object Key : CoroutineContext.Key<AfterPropertyContextElement>
}
