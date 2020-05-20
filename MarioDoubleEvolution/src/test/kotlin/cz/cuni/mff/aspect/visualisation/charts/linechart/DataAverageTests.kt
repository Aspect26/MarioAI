package cz.cuni.mff.aspect.visualisation.charts.linechart

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DataAverageTests {

    @Test
    fun `test average single values`() {
        val dataToAverage = listOf(
            listOf(Pair(0.0, 1.0)),
            listOf(Pair(0.0, 2.0)),
            listOf(Pair(0.0, 3.0)),
            listOf(Pair(0.0, 4.0)),
            listOf(Pair(0.0, 5.0))
        )

        val result = AverageLineChart.DataAverage.compute(dataToAverage)

        assertEquals(1, result.size, "There should be only one value")
        assertEquals(Pair(0.0, 3.0), result[0])
    }

    @Test
    fun `test average multiple values`() {
        val dataToAverage = listOf(
            listOf(Pair(0.0, 1.0)),
            listOf(Pair(0.0, 3.0)),
            listOf(Pair(1.0, 3.0))
        )

        val result = AverageLineChart.DataAverage.compute(dataToAverage)

        assertEquals(2, result.size, "There should be two values")
        assertEquals(Pair(0.0, 2.0), result[0])
        assertEquals(Pair(1.0, 3.0), result[1])
    }

    @Test
    fun `test average multiple values 2`() {
        val dataToAverage = listOf(
            listOf(Pair(0.0, 1.0)),
            listOf(Pair(0.0, 2.0)),
            listOf(Pair(0.0, 3.0), Pair(1.0, 3.0))
        )

        val result = AverageLineChart.DataAverage.compute(dataToAverage)

        assertEquals(2, result.size, "There should be two values")
        assertEquals(Pair(0.0, 2.0), result[0])
        assertEquals(Pair(1.0, 3.0), result[1])
    }

}