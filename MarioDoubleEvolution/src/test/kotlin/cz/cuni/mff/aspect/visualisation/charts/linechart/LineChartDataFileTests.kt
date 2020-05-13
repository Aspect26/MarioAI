package cz.cuni.mff.aspect.visualisation.charts.linechart

import cz.cuni.mff.aspect.visualisation.charts.DataSeries
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Test
import java.awt.Color
import java.io.File

class LineChartDataFileTests {

    companion object {
        const val TEST_FILE_PATH = "test_file_path.txt"
    }

    @After
    fun after() {
        File(TEST_FILE_PATH).delete()
    }

    @Test
    fun `test saved without exception`() {
        val mockData = mockData()
        LineChartDataFile.storeData(TEST_FILE_PATH, mockData)
    }

    @Test
    fun `test load`() {
        val data = LineChartDataFile.loadData("./src/test/resources/line_chart_data.txt")

        assertEquals("Middle earth chart", data.label)
        assertEquals("Age", data.xLabel)
        assertEquals("Deaths", data.yLabel)
        assertEquals(listOf(1.0, 2.0, 3.0), data.stops)
        assertEquals("There are two data series", 2, data.series.size)
        assertEquals("series1", data.series[0].label)
        assertEquals("series2", data.series[1].label)
        assertEquals("There are two datapoints in the series 1", 2, data.series[0].data.size)
        assertEquals(1.0, data.series[0].data[0].first, 0.00000001)
        assertEquals(2.0, data.series[0].data[0].second, 0.00000001)
        assertEquals(3.0, data.series[0].data[1].first, 0.00000001)
        assertEquals(0.5, data.series[0].data[1].second, 0.00000001)
    }

    @Test
    fun `test load without stops`() {
        val data = LineChartDataFile.loadData("./src/test/resources/line_chart_data_without_stops.txt")

        assertEquals("Middle earth chart", data.label)
        assertEquals("Age", data.xLabel)
        assertEquals("Deaths", data.yLabel)
        assertEquals("There are no stops", 0, data.stops.size)
        assertEquals("There are two data series", 2, data.series.size)
        assertEquals("series1", data.series[0].label)
        assertEquals("series2", data.series[1].label)
        assertEquals("There are two datapoints in the series 1", 2, data.series[0].data.size)
        assertEquals(1.0, data.series[0].data[0].first, 0.00000001)
        assertEquals(2.0, data.series[0].data[0].second, 0.00000001)
        assertEquals(3.0, data.series[0].data[1].first, 0.00000001)
        assertEquals(0.5, data.series[0].data[1].second, 0.00000001)
    }

    @Test
    fun `test save and load didn't change any property`() {
        val initialData = mockData()
        LineChartDataFile.storeData(TEST_FILE_PATH, initialData)
        val loadedData = LineChartDataFile.loadData(TEST_FILE_PATH)

        assertEquals("The stored and loaded data should be equal", initialData, loadedData)
    }

    private fun mockData(): LineChartData =
        LineChartData("Middle earth chart", "Age", "Deaths", listOf(5.0, 12.0, 260.0), listOf(
            DataSeries("first", Color.RED, mutableListOf(Pair(0.0, 1.0), Pair(1.0, 1.0), Pair(3.0, 5.0))),
            DataSeries("second", Color.RED, mutableListOf(Pair(0.0, 1.0))),
            DataSeries("third", Color.RED, mutableListOf(Pair(0.0, 1.0)))
        ))

}