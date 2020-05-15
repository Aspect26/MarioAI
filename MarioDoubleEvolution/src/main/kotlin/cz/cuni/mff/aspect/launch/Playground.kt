package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.visualisation.charts.linechart.LineChart
import cz.cuni.mff.aspect.visualisation.charts.linechart.LineChartDataFile

/** Launcher used for development needs. */
fun main() {
    val chartData = LineChartDataFile.loadData("./data/coev/08_dense_input/neuro_pc/coev-fitness.svg.dat")
    chartData.series[2].multiplyValuesBy(1000.0)
    chartData.series[3].multiplyValuesBy(1000.0)

    val chart = LineChart.loadFromData(chartData)
    chart.renderChart()

    // chart.save("./data/coev/08_dense_input/neuro_pc/coev-fitness.svg")
}
