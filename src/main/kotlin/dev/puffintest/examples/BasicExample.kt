package dev.puffintest.examples

import dev.puffintest.expectContains
import dev.puffintest.expectEqual
import dev.puffintest.expectLengthBetween
import dev.puffintest.runTests
import dev.puffintest.test
import kotlin.system.exitProcess

fun main() {
    val status = runTests(
        listOf(
            test("formats a title") {
                expectEqual("HELLO, PUFFIN", "hello, puffin".uppercase())
            },
            test("finds an item", tags = setOf("unit")) {
                expectContains(listOf("puffin", "tern"), "puffin")
                expectLengthBetween(listOf("puffin", "tern"), 1, 3)
            },
        ),
    )
    exitProcess(status)
}
