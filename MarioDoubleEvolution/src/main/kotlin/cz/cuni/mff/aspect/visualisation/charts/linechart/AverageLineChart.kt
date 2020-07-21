package cz.cuni.mff.aspect.visualisation.charts.linechart

import cz.cuni.mff.aspect.visualisation.charts.DataSeries

/**
 * Creates a line chart with data series averaged from given list of line charts.
 *
 * Data series whose names are equal, are considered the same, and those are averaged.
 */
class AverageLineChart(lineCharts: Array<LineChart>) {

    val averagedLineChart: LineChart = this.createAveragedLineChart(lineCharts)

    fun renderChart() {
        this.averagedLineChart.renderChart()
    }

    fun storeChart(filePath: String) {
        this.averagedLineChart.save(filePath)
    }

    private fun createAveragedLineChart(lineCharts: Array<LineChart>): LineChart {
        val allSeries = lineCharts.map { it.series }.flatten()
        val allSeriesNames = allSeries.map { it.label }.distinct()

        val averagedSeries = allSeriesNames.map { seriesName -> this.createAveragedDataSeries(allSeries.filter { it.label == seriesName }) }
        val averagedLineChartData = LineChartData(
            "Average: ${lineCharts[0].label}",
            lineCharts[0].xLabel,
            "Average: ${lineCharts[0].yLabel}",
            lineCharts.map { it.stops }.flatten().distinct().sorted(),
            averagedSeries
        )

        return LineChart.loadFromData(averagedLineChartData)
    }

    private fun createAveragedDataSeries(dataSeries: List<DataSeries>): DataSeries {
        if (dataSeries.isEmpty()) throw IllegalArgumentException("Can't average zero data series.")
        if (dataSeries.size == 1) return dataSeries[0]

        val label = dataSeries[0].label
        val color = dataSeries[0].color
        val averagedData = DataAverage.compute(dataSeries.map { it.data })

        return DataSeries(label, color, averagedData)
    }

    internal object DataAverage {

        fun compute(data: List<List<Pair<Double, Double>>>): MutableList<Pair<Double, Double>> {
            val averagedData = mutableListOf<Pair<Double, Double>>()
            val currentPositions = IntArray(data.size) { 0 }

            while (currentPositions.filterIndexed { dataIndex, _ -> currentPositions[dataIndex] < data[dataIndex].size }.isNotEmpty()) {
                val valuesAtCurrentPositions: List<Pair<Double, Double>?> =
                    data.mapIndexed { dataIndex, _ -> if (currentPositions[dataIndex] >= data[dataIndex].size) null else data[dataIndex][currentPositions[dataIndex]] }

                val minXValueAtCurrentPositions = valuesAtCurrentPositions.minBy { dataPoint -> dataPoint?.first ?: Double.POSITIVE_INFINITY }!!.first
                val minValuesAtCurrentPositions = valuesAtCurrentPositions.map { value -> if (value?.first == minXValueAtCurrentPositions) value else null }

                minValuesAtCurrentPositions.forEachIndexed { index, value -> if (value != null) currentPositions[index]++ }
                val averageValue = this.averageValues(minValuesAtCurrentPositions.filterNotNull())

                averagedData.add(averageValue)
            }

            return averagedData
        }

        private fun averageValues(values: List<Pair<Double, Double>>): Pair<Double, Double> {
            return Pair(values[0].first, values.map { (_, y) -> y}.average())
        }

    }

    companion object {
        fun fromData(dataSeries: List<LineChartData>): AverageLineChart =
            AverageLineChart(dataSeries.map { LineChart.loadFromData(it) }.toTypedArray())
    }

}