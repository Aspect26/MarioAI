package cz.cuni.mff.aspect.utils

import org.apache.commons.math3.exception.OutOfRangeException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DiscretizeTests {

    @Test
    fun `lesser than all discrete values`() {
        assertThrows<OutOfRangeException> {
            discretize(0.05, arrayOf(0.1, 0.2, 0.3))
        }
    }

    @Test
    fun `first value`() {
        val result = discretize(0.1, arrayOf(0.0, 0.2, 0.3))
        assertEquals(0.0, result, ROUNDING_ERROR_DELTA)
    }

    @Test
    fun `middle value`() {
        val result = discretize(0.35, arrayOf(0.0, 0.2, 0.3, 0.4, 0.5))
        assertEquals(0.3, result, ROUNDING_ERROR_DELTA)
    }

    @Test
    fun `end value`() {
        val result = discretize(0.8, arrayOf(0.0, 0.2, 0.3, 0.4, 0.5))
        assertEquals(0.5, result, ROUNDING_ERROR_DELTA)
    }

    @Test
    fun `boundary value`() {
        val result = discretize(0.3, arrayOf(0.0, 0.2, 0.3, 0.4, 0.5))
        assertEquals(0.3, result, ROUNDING_ERROR_DELTA)
    }

    @Test
    fun `integer value`() {
        val result = discretize(5, arrayOf(1, 3, 7, 10))
        assertEquals(3, result)
    }

    companion object {
        const val ROUNDING_ERROR_DELTA = 0.000001
    }
}