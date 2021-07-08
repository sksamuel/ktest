---
title: Json Matchers
slug: json-matchers.html
sidebar_label: Json
---

Kotest provides powerful JSON assertions in the `kotest-assertions-json` module.
These allow flexible testing of json strings without the need to worry about formatting or ordering.
They provide precise error messages when comparing json so that the error can be easily found in a large json structure.

This module is available for JVM and JS targets.

## shouldEqualJson

`json.shouldEqualJson(other)` asserts that the left-hand side represents the same
JSON structure as the right-hand side.

The matcher allows for different formatting, and for different order of keys.

For example, the following two JSON strings would be considered equal:

```json
{
   "name": "sam",
   "location": "chicago",
   "age" : 41
}
```

and

```json
{ "age" : 41, "name": "sam", "location": "chicago" }
```

The inverse of this matcher is `shouldNotEqualJson` which will error if two JSON strings
_are_ considered equal.

### CompareMode

`shouldEqualJson` supports a parameter called `CompareMode` which can be used to guide comparison of types that contain
compatible values.

By setting this to `CompareMode.Lenient`, types that can be coerced to match are considered equal. For example,
the string value `"true"` and the boolean value `true` will be considered equal if compare mode is lenient.

Similarly, the string value `"123"` and the number value `123` will match in lenient mode.

For example:

```kotlin
val a = """ { "a": "true", "b": "123" } """
val b = """ { "a": true, "b": 123 } """

// this would pass
a.shouldEqualJson(b, CompareOrder.Lenient)

// this would fail
a.shouldEqualJson(b)
```

:::note
Longs and doubles will always attempt to match regardless of this setting.
:::

The default is `CompareMode.Strict` which will consider any values unequal if they have different types.

### CompareOrder

`shouldEqualJson` supports a parameter called `CompareOrder` which can be used to force object comparision
to consider field order. By default, the order of fields in an object does not matter, and so

```json
{ "a": "foo", "b": "bar" }
```

and

```json
{ "b": "bar", "a": "foo" }
```

would be considered equal. Setting this parameter to `CompareOrder.Strict` means that the above example would fail. For example:

```kotlin
val a = """ { "a": "foo", "b": "bar" } """
val b = """ { "b": "bar", "a": "foo" } """

// this would fail
a.shouldEqualJson(b, CompareOrder.Strict)

// this would pass
a.shouldEqualJson(b)
```

Targets: **JVM**, **JS**

## shouldEqualSpecifiedJson
Behaves a lot like `shouldEqualJson`, but ignores extra keys present in the actual structure. By comparison, `shouldEqualJson` requires the entire structure to match. Using `shouldEqualSpecifiedJson` will make the comparison use only specified fields, for example:

```kotlin
val a = """ { "a": true, "date": "2019-11-03" } """
val b = """ { "a": true } """

// this would pass
a shouldEqualSpecifiedJson b

// this would fail
a shouldEqualJson b
```

`shouldEqualSpecifiedJson` also supports the `CompareMode` and `CompareOrder` parameters.

Targets: **JVM**, **JS**

## shouldContainJsonKey

`json?.shouldContainJsonKey("$.json.path")` asserts that a JSON string contains the given JSON path.

The inverse of this matcher is `shouldNotContainJsonKey` which will error if a JSON string _does_ contain the given JSON path.

Targets: **JVM**

## shouldContainJsonKeyValue

`str?.shouldContainJsonKeyValue("$.json.path", value)` asserts that a JSON string contains a JSON path with a specific `value`.

The inverse of this matcher is `shouldNotContainJsonKeyValue` which will error if a JSON string _does_ contain the given value at the given JSON path.

Targets: **JVM**

## shouldMatchJsonResource

`json?.shouldMatchJsonResource("/file.json")` asserts that the JSON is equal to the existing test reosource `/file.json`, ignoring properties' order and formatting.

Targets: **JVM**

:::note
JSON matchers on the JVM are built using the Jackson library.
:::
