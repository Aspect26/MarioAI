package cz.cuni.mff.aspect.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MinTests {

    @Test
    fun `empty arguments`() {
        assertThrows<java.lang.IllegalArgumentException> {
            min<Float>(emptyList())
        }
    }

    @Test
    fun `single argument`() {
        assertEquals(5, min<Int>(listOf(5)))
        assertEquals(42f, min<Float>(listOf(42f)))
        assertEquals(66.0, min<Double>(listOf(66.0)), 0.0000001)
    }

    @Test
    fun `two arguments`() {
        assertEquals(5, min<Int>(listOf(5, 6)))
        assertEquals(5, min<Int>(listOf(20, 5)))
    }

    @Test
    fun `more arguments`() {
        assertEquals(5, min<Int>(listOf(5, 6, 7)))
        assertEquals(5, min<Int>(listOf(6, 5, 7)))
        assertEquals(5, min<Int>(listOf(7, 6, 5)))
    }

    @Test
    fun `more arguments 2`() {
        assertEquals(0, min<Int>(listOf(8, 5, 6, 2, 1, 4, 3, 0, 2, 1)))
    }
}