@file:Suppress("unused")

package io.kotest.assertions.json

import kotlin.math.abs

enum class CompareMode {
   /** Forces strict type handling, e.g. true != "true" */
   Strict,

   /** Relaxes type handling, enabling conversion of string values to expected type before comparison.
    * 3.14 == "3.14"
    * true == "true"
    */
   Lenient
}

enum class CompareOrder {
   Strict,
   Lenient
}

enum class FieldComparison {
   /** Verifies all expected key-values match, and that no extra keys exist in the actual */
   All,
   /** Verifies all expected key-values match, ignoring keys in actual that are not specified in expected */
   IgnoringUnspecified
}

/**
 * Compares two json trees, returning a detailed error message if they differ.
 */
internal fun compare(
   path: List<String>,
   expected: JsonNode,
   actual: JsonNode,
   mode: CompareMode,
   order: CompareOrder,
   fieldComparison: FieldComparison,
): JsonError? {
   return when (expected) {
      is JsonNode.ObjectNode -> when (actual) {
         is JsonNode.ObjectNode -> compareObjects(path, expected, actual, mode, order, fieldComparison)
         else -> JsonError.ExpectedObject(path, actual)
      }
      is JsonNode.ArrayNode -> when (actual) {
         is JsonNode.ArrayNode -> compareArrays(path, expected, actual, mode, order, fieldComparison)
         else -> JsonError.ExpectedArray(path, actual)
      }
      is JsonNode.BooleanNode -> compareBoolean(path, expected, actual, mode)
      is JsonNode.StringNode -> compareString(path, expected, actual, mode)
      is JsonNode.LongNode -> compareLong(path, expected, actual, mode)
      is JsonNode.DoubleNode -> compareDouble(path, expected, actual, mode)
      is JsonNode.FloatNode -> compareFloat(path, expected, actual, mode)
      is JsonNode.IntNode -> compareInt(path, expected, actual, mode)
      JsonNode.NullNode -> compareNull(path, actual)
   }
}

internal fun compareObjects(
   path: List<String>,
   expected: JsonNode.ObjectNode,
   actual: JsonNode.ObjectNode,
   mode: CompareMode,
   order: CompareOrder,
   fieldComparison: FieldComparison,
): JsonError? {

   if (fieldComparison == FieldComparison.All) {
      val keys1 = expected.elements.keys
      val keys2 = actual.elements.keys

      if (keys1.size < keys2.size) {
         val missing = keys2 - keys1
         return JsonError.ObjectMissingKeys(path, missing)
      }

      if (keys2.size < keys1.size) {
         val extra = keys1 - keys2
         return JsonError.ObjectExtraKeys(path, extra)
      }
   }

   // when using strict order mode, the order of elements in json matters, normally, we don't care
   when (order) {
      CompareOrder.Strict ->
         expected.elements.entries.withIndex().zip(actual.elements.entries).forEach { (e, a) ->
            if (a.key != e.value.key) return JsonError.NameOrderDiff(path, e.index, e.value.key, a.key)
            val error = compare(path + a.key, e.value.value, a.value, mode, order, fieldComparison)
            if (error != null) return error
         }
      CompareOrder.Lenient ->
         expected.elements.entries.forEach { (name, e) ->
            val a = actual.elements[name] ?: return JsonError.ObjectMissingKeys(path, setOf(name))
            val error = compare(path + name, e, a, mode, order, fieldComparison)
            if (error != null) return error
         }
   }

   return null
}

internal fun compareArrays(
   path: List<String>,
   expected: JsonNode.ArrayNode,
   actual: JsonNode.ArrayNode,
   mode: CompareMode,
   order: CompareOrder,
   fieldComparison: FieldComparison,
): JsonError? {

   if (expected.elements.size != actual.elements.size)
      return JsonError.UnequalArrayLength(path, expected.elements.size, actual.elements.size)

   expected.elements.withIndex().zip(actual.elements.withIndex()).forEach { (a, b) ->
      val error = compare(path + "[${a.index}]", a.value, b.value, mode, order, fieldComparison)
      if (error != null) return error
   }

   return null
}

/**
 * When comparing a string, if the [mode] is [CompareMode.Lenient] we can convert the actual node to a string.
 */
internal fun compareString(path: List<String>, expected: JsonNode.StringNode, actual: JsonNode, mode: CompareMode): JsonError? {
   return when {
      actual is JsonNode.StringNode -> compareStrings(path, expected.value, actual.value)
      mode == CompareMode.Lenient -> when (actual) {
         is JsonNode.BooleanNode -> compareStrings(path, expected.value, actual.value.toString())
         is JsonNode.DoubleNode -> compareStrings(path, expected.value, actual.value.toString())
         is JsonNode.LongNode -> compareStrings(path, expected.value, actual.value.toString())
         else -> JsonError.IncompatibleTypes(path, expected, actual)
      }
      else -> JsonError.IncompatibleTypes(path, expected, actual)
   }
}

internal fun compareStrings(path: List<String>, expected: String, actual: String): JsonError? {
   return when (expected) {
      actual -> null
      else -> JsonError.UnequalStrings(path, expected, actual)
   }
}

/**
 * When comparing a boolean, if the [mode] is [CompareMode.Lenient] and the actual node is a text
 * node with "true" or "false", then we convert.
 */
internal fun compareBoolean(
   path: List<String>,
   expected: JsonNode.BooleanNode,
   actual: JsonNode,
   mode: CompareMode
): JsonError? {
   return when {
      actual is JsonNode.BooleanNode -> compareBooleans(path, expected.value, actual.value)
      mode == CompareMode.Lenient && actual is JsonNode.StringNode -> when (actual.value) {
         "true" -> compareBooleans(path, expected.value, true)
         "false" -> compareBooleans(path, expected.value, false)
         else -> JsonError.UnequalValues(path, expected, actual)
      }
      else -> JsonError.IncompatibleTypes(path, expected, actual)
   }
}

internal fun compareBooleans(path: List<String>, expected: Boolean, actual: Boolean): JsonError? {
   return when (expected) {
      actual -> null
      else -> JsonError.UnequalBooleans(path, expected, actual)
   }
}

/**
 * When comparing a boolean, if the [mode] is [CompareMode.Lenient] and the actual node is a text
 * node with "true" or "false", then we convert.
 */
internal fun compareLong(path: List<String>, expected: JsonNode.LongNode, actual: JsonNode, mode: CompareMode): JsonError? {
   return when {
      actual is JsonNode.LongNode -> compareLongs(path, expected.value, actual.value)
      mode == CompareMode.Lenient && actual is JsonNode.StringNode -> when (val l = actual.value.toLongOrNull()) {
         null -> JsonError.IncompatibleTypes(path, expected, actual)
         else -> compareLongs(path, expected.value, l)
      }
      else -> JsonError.IncompatibleTypes(path, expected, actual)
   }
}

internal fun compareLongs(path: List<String>, expected: Long, actual: Long): JsonError? {
   return when (expected) {
      actual -> null
      else -> JsonError.UnequalValues(path, expected, actual)
   }
}

/**
 * When comparing a boolean, if the [mode] is [CompareMode.Lenient] and the actual node is a text
 * node with "true" or "false", then we convert.
 */
internal fun compareDouble(path: List<String>, expected: JsonNode.DoubleNode, actual: JsonNode, mode: CompareMode): JsonError? {
   return when {
      actual is JsonNode.DoubleNode -> compareDoubles(path, expected.value, actual.value)
      actual is JsonNode.LongNode -> compareDoubles(path, expected.value, actual.value.toDouble())
      actual is JsonNode.FloatNode -> compareDoubles(path, expected.value, actual.value.toDouble())
      actual is JsonNode.IntNode -> compareDoubles(path, expected.value, actual.value.toDouble())
      mode == CompareMode.Lenient && actual is JsonNode.StringNode -> when (val d = actual.value.toDoubleOrNull()) {
         null -> JsonError.IncompatibleTypes(path, expected, actual)
         else -> compareDoubles(path, expected.value, d)
      }
      else -> JsonError.IncompatibleTypes(path, expected, actual)
   }
}

internal fun compareDoubles(path: List<String>, expected: Double, actual: Double): JsonError? {
   return when {
      abs(expected - actual) <= Double.MIN_VALUE -> null
      else -> JsonError.UnequalValues(path, expected, actual)
   }
}

internal fun compareFloat(path: List<String>, expected: JsonNode.FloatNode, actual: JsonNode, mode: CompareMode): JsonError? {
   return when {
      actual is JsonNode.FloatNode -> compareFloats(path, expected.value, actual.value)
      actual is JsonNode.LongNode -> compareFloats(path, expected.value, actual.value.toFloat())
      actual is JsonNode.DoubleNode -> compareFloats(path, expected.value, actual.value.toFloat())
      actual is JsonNode.IntNode -> compareFloats(path, expected.value, actual.value.toFloat())
      mode == CompareMode.Lenient && actual is JsonNode.StringNode -> when (val d = actual.value.toFloatOrNull()) {
         null -> JsonError.IncompatibleTypes(path, expected, actual)
         else -> compareFloats(path, expected.value, d)
      }
      else -> JsonError.IncompatibleTypes(path, expected, actual)
   }
}

internal fun compareFloats(path: List<String>, expected: Float, actual: Float): JsonError? {
   return when {
      abs(expected - actual) <= Float.MIN_VALUE -> null
      else -> JsonError.UnequalValues(path, expected, actual)
   }
}

internal fun compareInt(path: List<String>, expected: JsonNode.IntNode, actual: JsonNode, mode: CompareMode): JsonError? {
   return when {
      actual is JsonNode.IntNode -> compareInts(path, expected.value, actual.value)
      actual is JsonNode.FloatNode -> compareInts(path, expected.value, actual.value.toInt())
      actual is JsonNode.LongNode -> compareInts(path, expected.value, actual.value.toInt())
      actual is JsonNode.DoubleNode -> compareInts(path, expected.value, actual.value.toInt())
      mode == CompareMode.Lenient && actual is JsonNode.StringNode -> when (val d = actual.value.toIntOrNull()) {
         null -> JsonError.IncompatibleTypes(path, expected, actual)
         else -> compareInts(path, expected.value, d)
      }
      else -> JsonError.IncompatibleTypes(path, expected, actual)
   }
}

internal fun compareInts(path: List<String>, expected: Int, actual: Int): JsonError? {
   return when (expected) {
       actual -> null
       else -> JsonError.UnequalValues(path, expected, actual)
   }
}

internal fun compareNull(path: List<String>, b: JsonNode): JsonError? {
   return when (b) {
      is JsonNode.NullNode -> null
      else -> JsonError.ExpectedNull(path, b)
   }
}
