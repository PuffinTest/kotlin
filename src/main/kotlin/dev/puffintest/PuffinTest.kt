package dev.puffintest

import java.util.Objects

/** A named test callback with optional tags or a skip reason. */
data class TestCase(
    val name: String,
    val body: () -> Unit,
    val tags: Set<String> = emptySet(),
    val skipReason: String? = null,
) {
    init {
        require(name.isNotBlank()) { "test name must not be blank" }
        require(tags.none { it.isBlank() }) { "tags must contain nonempty names" }
        require(skipReason == null || skipReason.isNotBlank()) { "skipReason must not be blank" }
    }
}

/** Declare a named test case. */
fun test(
    name: String,
    tags: Set<String> = emptySet(),
    skipReason: String? = null,
    body: () -> Unit,
): TestCase = TestCase(name.trim(), body, tags.map { it.trim() }.toSet(), skipReason)

@PublishedApi
internal class ExpectationFailure(
    message: String,
    val expected: Any? = null,
    val actual: Any? = null,
    val hasValues: Boolean = false,
) : AssertionError(message)

/** Fail unless [actual] and [expected] are equal, including nested arrays. */
fun expectEqual(actual: Any?, expected: Any?, message: String? = null) {
    if (!Objects.deepEquals(actual, expected)) {
        throw ExpectationFailure(message ?: "Values are not equal", expected, actual, hasValues = true)
    }
}

/** Fail unless [actual] and [unexpected] differ. */
fun expectNotEqual(actual: Any?, unexpected: Any?, message: String? = null) {
    if (Objects.deepEquals(actual, unexpected)) {
        throw ExpectationFailure(message ?: "Values are unexpectedly equal", "different from $unexpected", actual, hasValues = true)
    }
}

/** Fail unless [actual] starts with [prefix]. */
fun expectStartsWith(actual: String, prefix: String, message: String? = null) {
    if (!actual.startsWith(prefix)) {
        throw ExpectationFailure(message ?: "Text does not start with the expected prefix", "text starting with $prefix", actual, hasValues = true)
    }
}

/** Fail unless [actual] ends with [suffix]. */
fun expectEndsWith(actual: String, suffix: String, message: String? = null) {
    if (!actual.endsWith(suffix)) {
        throw ExpectationFailure(message ?: "Text does not end with the expected suffix", "text ending with $suffix", actual, hasValues = true)
    }
}

/** Fail if [actual] starts with [prefix]. */
fun expectNotStartsWith(actual: String, prefix: String, message: String? = null) {
    if (actual.startsWith(prefix)) {
        throw ExpectationFailure(message ?: "Text unexpectedly starts with the prefix", "text not starting with $prefix", actual, hasValues = true)
    }
}

/** Fail if [actual] ends with [suffix]. */
fun expectNotEndsWith(actual: String, suffix: String, message: String? = null) {
    if (actual.endsWith(suffix)) {
        throw ExpectationFailure(message ?: "Text unexpectedly ends with the suffix", "text not ending with $suffix", actual, hasValues = true)
    }
}

/** Fail unless [actual] has a length in the inclusive [minimum] to [maximum] range. */
fun expectLengthBetween(actual: Any, minimum: Int, maximum: Int, message: String? = null) {
    require(minimum >= 0 && maximum >= 0 && minimum <= maximum) { "length bounds must be nonnegative and ordered" }
    val length = when (actual) {
        is CharSequence -> actual.length
        is Collection<*> -> actual.size
        is Map<*, *> -> actual.size
        is Array<*> -> actual.size
        is BooleanArray -> actual.size
        is ByteArray -> actual.size
        is CharArray -> actual.size
        is DoubleArray -> actual.size
        is FloatArray -> actual.size
        is IntArray -> actual.size
        is LongArray -> actual.size
        is ShortArray -> actual.size
        else -> throw IllegalArgumentException("value must be a collection, map, string, or array")
    }
    if (length !in minimum..maximum) {
        throw ExpectationFailure(message ?: "Value length is outside the expected range", "length between $minimum and $maximum, inclusive", length, hasValues = true)
    }
}

/** Fail unless two numbers are close under relative and absolute tolerances. */
fun expectClose(actual: Double, expected: Double, relTolerance: Double = 1e-9, absTolerance: Double = 0.0) {
    require(relTolerance.isFinite() && absTolerance.isFinite() && relTolerance >= 0 && absTolerance >= 0) {
        "tolerances must be finite and nonnegative"
    }
    val difference = kotlin.math.abs(actual - expected)
    val scale = maxOf(kotlin.math.abs(actual), kotlin.math.abs(expected))
    val close = actual == expected || (actual.isFinite() && expected.isFinite() &&
        (difference <= absTolerance || (scale != 0.0 && difference / scale <= relTolerance)))
    if (!close) throw ExpectationFailure("Values are not close", expected, actual, hasValues = true)
}

/** Fail unless [actual] is between [minimum] and [maximum], inclusive. */
fun expectBetween(actual: Double, minimum: Double, maximum: Double) {
    require(!minimum.isNaN() && !maximum.isNaN() && minimum <= maximum) {
        "minimum must be less than or equal to maximum"
    }
    if (!(actual >= minimum && actual <= maximum)) {
        throw ExpectationFailure("Value is outside the expected range", "between $minimum and $maximum, inclusive", actual, hasValues = true)
    }
}

/** Fail unless [actual] is true. */
fun expectTrue(actual: Boolean, message: String? = null) {
    if (!actual) throw ExpectationFailure(message ?: "Expected true", true, actual, hasValues = true)
}

/** Fail unless [actual] is false. */
fun expectFalse(actual: Boolean, message: String? = null) {
    if (actual) throw ExpectationFailure(message ?: "Expected false", false, actual, hasValues = true)
}

/** Fail unless [actual] is null. */
fun expectNull(actual: Any?, message: String? = null) {
    if (actual != null) throw ExpectationFailure(message ?: "Expected null", null, actual, hasValues = true)
}

/** Fail unless [actual] is non-null. */
fun expectNotNull(actual: Any?, message: String? = null) {
    if (actual == null) throw ExpectationFailure(message ?: "Expected a non-null value", "non-null", actual, hasValues = true)
}

/** Fail unless [item] is present in [container]. */
fun expectContains(container: Iterable<*>, item: Any?, message: String? = null) {
    if (container.none { Objects.deepEquals(it, item) }) {
        throw ExpectationFailure(message ?: "Item is not present in the container", "a container containing $item", container, hasValues = true)
    }
}

/** Fail if [item] occurs in [container]. */
fun expectNotContains(container: Iterable<*>, item: Any?, message: String? = null) {
    if (container.any { Objects.deepEquals(it, item) }) {
        throw ExpectationFailure(message ?: "Item is unexpectedly present in the container", "a container not containing $item", container, hasValues = true)
    }
}

/** Fail unless [item] is a key in [container]. */
fun expectContains(container: Map<*, *>, item: Any?, message: String? = null) {
    if (!container.containsKey(item)) {
        throw ExpectationFailure(message ?: "Key is not present in the map", "a map containing key $item", container, hasValues = true)
    }
}

/** Fail if [item] is a key in [container]. */
fun expectNotContains(container: Map<*, *>, item: Any?, message: String? = null) {
    if (container.containsKey(item)) {
        throw ExpectationFailure(message ?: "Key is unexpectedly present in the map", "a map without key $item", container, hasValues = true)
    }
}

/** Explicitly fail the current test case. */
fun fail(message: String = "Explicit failure"): Nothing = throw ExpectationFailure(message)

/** Require [block] to throw an exception of type [T] and return it. */
inline fun <reified T : Throwable> expectThrows(noinline block: () -> Unit): T {
    val thrown = try {
        block()
        null
    } catch (error: Throwable) {
        if (error is VirtualMachineError || error is ThreadDeath || error is LinkageError) throw error
        error
    }
    if (thrown is T) return thrown
    val actual = thrown?.javaClass?.simpleName ?: "no exception"
    throw ExpectationFailure("Expected ${T::class.simpleName} to be thrown, got $actual", T::class.simpleName, actual, hasValues = true)
}

/** Run cases sequentially and return 0 on success or 1 on failure. */
fun runTests(
    cases: Iterable<TestCase>,
    filter: String? = null,
    tag: String? = null,
    failFast: Boolean = false,
    beforeEach: (() -> Unit)? = null,
    afterEach: (() -> Unit)? = null,
): Int {
    val selected = cases.filter { testCase ->
        (filter == null || testCase.name.contains(filter, ignoreCase = true)) &&
            (tag == null || testCase.tags.any { it.equals(tag, ignoreCase = true) })
    }
    var passed = 0
    var failed = 0
    var skipped = 0
    var processed = 0
    for (testCase in selected) {
        processed++
        if (testCase.skipReason != null) {
            println("SKIP  ${testCase.name} (${testCase.skipReason})")
            skipped++
            continue
        }
        var setupSucceeded = false
        var phase = "beforeEach"
        var caseFailed = false
        try {
            beforeEach?.invoke()
            setupSucceeded = true
            phase = "test"
            testCase.body()
            phase = "afterEach"
            afterEach?.invoke()
            println("PASS  ${testCase.name}")
            passed++
        } catch (error: Exception) {
            failed++
            caseFailed = true
            printFailure(testCase, error, phase)
        } catch (error: AssertionError) {
            failed++
            caseFailed = true
            printFailure(testCase, error, phase)
        }
        if (setupSucceeded && phase != "afterEach" && afterEach != null && caseFailed) {
            try {
                afterEach()
            } catch (error: Exception) {
                failed++
                printFailure(testCase, error, "afterEach")
            } catch (error: AssertionError) {
                failed++
                printFailure(testCase, error, "afterEach")
            }
        }
        if (caseFailed && failFast) break
    }
    println("\n$processed processed: $passed passed, $failed failed, $skipped skipped")
    if (selected.isEmpty()) {
        when {
            filter != null && tag != null -> println("No test cases matched name '$filter' and tag '$tag'")
            filter != null -> println("No test cases matched name '$filter'")
            tag != null -> println("No test cases matched tag '$tag'")
            else -> println("No test cases were found")
        }
        return 1
    }
    return if (failed == 0) 0 else 1
}

private fun printFailure(testCase: TestCase, error: Throwable, phase: String) {
    val suffix = if (phase == "test") "" else " ($phase)"
    println("FAIL  ${testCase.name}$suffix")
    println("      ${error::class.simpleName}: ${error.message}")
    if (error is ExpectationFailure && error.hasValues) {
        println("      Expected: ${error.expected}")
        println("      Actual:   ${error.actual}")
    }
    error.stackTrace.firstOrNull { it.className != "dev.puffintest.PuffinTestKt" }?.let {
        println("      ${it.fileName}:${it.lineNumber}")
    }
}
