package cz.cuni.mff.aspect.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SlidingWindowTests {

    @Test
    fun `doesn't exceed limit`() {
        val limit = 5
        val history = SlidingWindow<Int>(limit)
        repeat(limit * 10) {
            history.push(5)
        }

        val itemsInHistory = history.getAll().size
        assertEquals(limit, itemsInHistory, "The amount of items in the history should be at its limit")
    }

    @Test
    fun `items are correctly ordered when not exceeding limit`() {
        val itemsToInsert = arrayOf(1, 30, 20, 10, 68)
        val history = SlidingWindow<Int>(100)

        itemsToInsert.forEach { history.push(it) }
        val resultItems = history.getAll()

        itemsToInsert.reversed().forEachIndexed { index, item ->
            assertEquals(item, resultItems[index], "The item at index $index is incorrect")
        }
    }

    @Test
    fun `items are correctly ordered when exceeding limit`() {
        val limit = 5
        val itemsToInsert = arrayOf(1, 30, 20, 10, 68, 20, 12, 15, 16, 2, 86, 66, 21, 41, 2, 35)
        val history = SlidingWindow<Int>(limit)

        itemsToInsert.forEach { history.push(it) }
        val resultItems = history.getAll()

        assertEquals(limit, resultItems.size, "The amount of items in the history should be at its limit")
        itemsToInsert.reversed().subList(0, limit).forEachIndexed { index, item ->
            assertEquals(item, resultItems[index], "The item at index $index is incorrect")
        }
    }
}