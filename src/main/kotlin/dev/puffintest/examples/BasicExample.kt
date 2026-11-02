package dev.puffintest.examples

import dev.puffintest.expectContains
import dev.puffintest.expectEqual
import dev.puffintest.expectErrorMessage
import dev.puffintest.expectLengthBetween
import dev.puffintest.expectLength
import dev.puffintest.expectNull
import dev.puffintest.expectNotNull
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
