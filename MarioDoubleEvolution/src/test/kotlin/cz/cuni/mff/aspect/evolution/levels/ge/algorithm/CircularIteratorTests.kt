package cz.cuni.mff.aspect.evolution.levels.ge.algorithm

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CircularIteratorTests {

    @Test
    fun `first next() call should return first value`() {
        val testArray = arrayOf(1, 2, 3)
        val circularIterator = CircularIterator(testArray)
        val firstReceivedValue = circularIterator.next()

        assertEquals(1, firstReceivedValue, "The first next() call should return first value in the underlying array")
    }

    @Test
    fun `5th next() calls should return 5th value`() {
        val testArray = arrayOf(1, 2, 3, 4, 5, 6, 7, 8)
        val circularIterator = CircularIterator(testArray)

        circularIterator.next()
        circularIterator.next()
        circularIterator.next()
        circularIterator.next()
        val fifthReceivedValue = circularIterator.next()

        assertEquals(5, fifthReceivedValue, "5th next() calls should return 5th value")
    }

    @Test
    fun `first 3 next() calls should return first 3 values`() {
        val testArray = arrayOf(1, 2, 3, 4, 5, 6, 7, 8)
        val circularIterator = CircularIterator(testArray)

        val firstReceivedValue = circularIterator.next()
        val secondReceivedValue = circularIterator.next()
        val thirdReceivedValue = circularIterator.next()

        assertEquals(1, firstReceivedValue, "1st next() calls should return 1st value")
        assertEquals(2, secondReceivedValue, "2nd next() calls should return 2nd value")
        assertEquals(3, thirdReceivedValue, "3rd next() calls should return 3rd value")
    }

    @Test
    fun `4th next() call on 3 element array should return the first value in the array`() {
        val testArray = arrayOf(1, 2, 3)
        val circularIterator = CircularIterator(testArray)

        circularIterator.next()
        circularIterator.next()
        circularIterator.next()
        val fourthReceivedValue = circularIterator.next()

        assertEquals(1, fourthReceivedValue, "Next returned wrong value")
    }

    @Test
    fun `20th next() call on 3 element array should return the second value in the array`() {
        val testArray = arrayOf(1, 2, 3)
        val circularIterator = CircularIterator(testArray)

        for (x in 1..19)
            circularIterator.next()

        val twentiethReceivedValue = circularIterator.next()

        assertEquals(2, twentiethReceivedValue, "Next returned wrong value")
    }

    @Test
    fun `zero next() calls should make wrapCount zero`() {
        val testArray = arrayOf(1, 2, 3, 4, 5, 6)
        val circularIterator = CircularIterator(testArray)

        assertEquals(0, circularIterator.wrapsCount, "Wrong wrap count")
    }

    @Test
    fun `calling next() less times than there are elements should make wrapCount zero`() {
        val testArray = arrayOf(1, 2, 3, 4, 5, 6)
        val circularIterator = CircularIterator(testArray)

        for (x in 1..4)
            circularIterator.next()

        assertEquals(0, circularIterator.wrapsCount, "Wrong wrap count")
    }

    @Test
    fun `20 next() calls on 3 element array should make wrap count 6`() {
        val testArray = arrayOf(1, 2, 3)
        val circularIterator = CircularIterator(testArray)

        for (x in 1..20)
            circularIterator.next()

        assertEquals(6, circularIterator.wrapsCount, "Next returned wrong value")
    }

    @Test
    fun `21 next() calls on 3 element array should make wrap count 6`() {
        val testArray = arrayOf(1, 2, 3)
        val circularIterator = CircularIterator(testArray)

        for (x in 1..21)
            circularIterator.next()

        assertEquals(6, circularIterator.wrapsCount, "Next returned wrong value")
    }

    @Test
    fun `22 next() calls on 3 element array should make wrap count 7`() {
        val testArray = arrayOf(1, 2, 3)
        val circularIterator = CircularIterator(testArray)

        for (x in 1..22)
            circularIterator.next()

        assertEquals(7, circularIterator.wrapsCount, "Next returned wrong value")
    }
}