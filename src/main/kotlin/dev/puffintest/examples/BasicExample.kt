package dev.puffintest.examples

import dev.puffintest.expectContains
import dev.puffintest.expectEqual
import dev.puffintest.expectErrorMessage
import dev.puffintest.expectLengthBetween
import dev.puffintest.expectLength
import dev.puffintest.expectNull
import dev.puffintest.expectNotNull
import dev.puffintest.expectMatches
import dev.puffintest.expectNotMatches
import dev.puffintest.expectGreaterThan
import dev.puffintest.expectLessThan
import dev.puffintest.expectAtLeast
import dev.puffintest.expectAtMost
import dev.puffintest.expectNotBetween
import dev.puffintest.expectFinite
import dev.puffintest.expectEmpty
import dev.puffintest.expectNotEmpty
import dev.puffintest.runTests
import dev.puffintest.test
import kotlin.system.exitProcess

fun main() {
    val status = runTests(
        listOf(
            test("formats a title") {
                expectEqual("HELLO, PUFFIN", "hello, puffin".uppercase())
                expectNull(null)
                expectNotNull("puffin")
                expectEmpty(emptyList<String>())
                expectNotEmpty(listOf("puffin"))
                val error = expectThrows<NumberFormatException> { "bad".toInt() }
                expectErrorMessage(error, "bad")
                expectMatches("order-4821 accepted", "order-\\d+ accepted")
                expectNotMatches("request failed", "\\b2xx\\b")
                expectGreaterThan(5.0, 3.0)
                expectLessThan(2.0, 3.0)
                expectAtLeast(3.0, 3.0)
                expectAtMost(3.0, 3.0)
                expectNotBetween(4.0, 1.0, 3.0)
                expectNotClose(0.1, 0.9)
                expectFinite(3.14)
            },
            test("finds an item", tags = setOf("unit")) {
                expectContains(listOf("puffin", "tern"), "puffin")
                expectContains("puffin test", "test")
                expectNotContains("puffin test", "penguin")
                expectLengthBetween(listOf("puffin", "tern"), 1, 3)
                expectLength(listOf("puffin", "tern"), 2)
            },
        ),
    )
    exitProcess(status)
}
