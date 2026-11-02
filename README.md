# PuffinTest for Kotlin

PuffinTest is an early Kotlin/JVM library for named test cases and readable expectation failures. It currently provides equality, inequality, truth, falsehood, membership, and typed exception checks; explicit failure; tags; skips; name and tag filters; per-case hooks; and sequential execution.

The initial implementation targets JDK 17 and uses Gradle. It has not been published to Maven Central.

## Run the example

From this directory, run:

```sh
gradle run
```

Declare cases with `test(name) { ... }` and run them with `runTests`:

```kotlin
val cases = listOf(
    test("greets the user") {
        expectEqual("HELLO", "hello".uppercase())
        expectBetween(18.0, 13.0, 19.0)
    },
)
val status = runTests(cases)
```

The runner returns `0` when all cases pass and `1` when a case fails or no cases match. Each case runs independently. Failure reports show the case, expectation message, expected and actual values when available, and the first stack location outside PuffinTest.

The main helpers are `expectEqual`, `expectNotEqual`, `expectClose`, `expectBetween`, `expectGreaterThan`, `expectLessThan`, `expectAtLeast`, `expectAtMost`, `expectLength`, `expectLengthBetween`, `expectTrue`, `expectFalse`, `expectNull`, `expectNotNull`, `expectEmpty`, `expectNotEmpty`, `expectStartsWith`, `expectEndsWith`, `expectNotStartsWith`, `expectNotEndsWith`, `expectContains` and `expectNotContains`, including string overloads, `expectThrows<T>`, `expectErrorMessage`, `expectMatches`, `expectNotMatches`, and `fail`. `expectBetween` uses inclusive numeric bounds. Prefix, suffix, and substring checks are case-sensitive. Regex checks use Kotlin `Regex` and search for a match anywhere in the string. `runTests` also accepts `filter`, `tag`, `failFast`, `beforeEach`, and `afterEach` options.

This implementation is an early draft and has not yet been verified. Equality follows Kotlin's `==` semantics except that array values are compared with Java deep equality. It is synchronous and JVM-only.
