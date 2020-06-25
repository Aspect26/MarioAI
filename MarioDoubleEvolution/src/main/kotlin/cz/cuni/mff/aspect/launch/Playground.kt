package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.visualisation.charts.linechart.AverageLineChart
import cz.cuni.mff.aspect.visualisation.charts.linechart.LineChart
import cz.cuni.mff.aspect.visualisation.charts.linechart.LineChartDataFile
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser


/** Launcher used for development purposes. */
fun main() {
//    LevelVisualiser().display(LevelPostProcessor.postProcess(PMPLevelGenerator().generate(), true))

    outputData()
//    renderAverageChart()
}

private fun outputData() {
    val experiment = "50:50:DO:0.65:7:5x5:false:false:100"
    val path = "data/experiments/final-experiments/neuro/$experiment/average_chart.svg.dat"
    val chartData = LineChartDataFile.loadData(path)

    chartData.series.forEach {
        println("${it.label} : ${it.data.map { it.second }.max()}")
    }
}

private fun renderAverageChart() {
    val experiment = "50:50:DO:0.65:5:5x5:false:false"
    val lineCharts = (1 .. 3)
        .map{ LineChart.loadFromFile("data/experiments/final-experiments/neuro/$experiment/NeuroEvolution, experiment ${it}_chart.svg.dat") }

    val averageChart = AverageLineChart(lineCharts.toTypedArray())
    averageChart.renderChart()
}