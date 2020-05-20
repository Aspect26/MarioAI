package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.visualisation.charts.DataSeries
import cz.cuni.mff.aspect.visualisation.charts.linechart.AverageLineChart
import cz.cuni.mff.aspect.visualisation.charts.linechart.LineChartData
import java.awt.Color
import kotlin.random.Random

/** Launcher used for development needs. */
fun main() {
//    val averagedLineChart = AverageLineChart.fromData(listOf(
//        LineChartData("l", "x", "y", listOf(), listOf(
//            DataSeries("A", Color.BLUE, mutableListOf(Pair(-1.0, 0.0), Pair(0.0, 1.0), Pair(1.0, 2.0))),
//            DataSeries("B", Color.RED, mutableListOf(Pair(0.0, 1.0), Pair(1.0, 2.0)))
//        )),
//        LineChartData("l", "x", "y", listOf(), listOf(
//            DataSeries("A", Color.BLUE, mutableListOf(Pair(0.0, 1.0), Pair(1.0, 4.0)))
//        ))
//    ))


    val random = Random(0)
    val initialDataSeries = DataSeries("A", Color.BLUE, (0 until 100).map { Pair(it.toDouble(), it.toDouble())}.toMutableList())
    val mutatedDataSeries = (0 until 100).map {
        val data = initialDataSeries.data.map { (x, y) -> Pair(x, y + random.nextDouble(-2.0, 2.0)) }.toMutableList()
        DataSeries("A", Color.BLUE, data)
    }

    val averagedLineChart = AverageLineChart.fromData(mutatedDataSeries.map {
        LineChartData("l", "x", "y", listOf(), listOf(it))
    })

    averagedLineChart.renderChart()
}
