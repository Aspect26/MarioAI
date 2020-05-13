package cz.cuni.mff.aspect.visualisation.charts.linechart

import cz.cuni.mff.aspect.visualisation.charts.DataSeries
import java.awt.Color
import java.io.File

/** Stores and loads [LineChart] data to/from a given file. */
// TODO: unit test me
internal object LineChartDataFile {

    fun storeData(filePath: String, lineChartData: LineChartData) {
        val contentBuilder = StringBuilder()
        with(contentBuilder) {
            append(lineChartData.label)
            append(System.lineSeparator())
            append(lineChartData.xLabel)
            append(System.lineSeparator())
            append(lineChartData.yLabel)
            append(System.lineSeparator())
            append(lineChartData.stops.joinToString(","))
        }

        lineChartData.series.forEach {
            with(contentBuilder) {
                append(System.lineSeparator())
                append(it.label)
                append(System.lineSeparator())
                append(it.color.rgb)
                append(System.lineSeparator())
                append(it.data.joinToString(" ") { (x, y) -> "$x:$y" })
            }
        }

        File(filePath).writeText(contentBuilder.toString())
    }

    fun loadData(filePath: String): LineChartData {
        val file = File(filePath)
        if (!file.exists() || file.isDirectory)
            throw IllegalArgumentException("Can't load line chart data from '$filePath' because the file either does not exist or is a directory.")

        val rawData = file.readLines()
        val label = rawData[0]
        val xLabel = rawData[1]
        val yLabel = rawData[2]
        val stops: List<Double> = rawData[0].split(",").map { it.toDouble() }
        val series: MutableList<DataSeries> = mutableListOf()

        for (lineIndex in 3 until rawData.size step 3) {
            val seriesLabel = rawData[lineIndex]
            val seriesColor = Color(rawData[lineIndex + 1].toInt())
            val seriesData: MutableList<Pair<Double, Double>> = rawData[lineIndex + 2]
                .split(" ")
                .map { Pair(it.split(":")[0].toDouble(), it.split(":")[1].toDouble()) }
                .toMutableList()

            val dataSeries = DataSeries(seriesLabel, seriesColor, seriesData)
            series.add(dataSeries)
        }

        return LineChartData(label, xLabel, yLabel, stops, series)
    }

}