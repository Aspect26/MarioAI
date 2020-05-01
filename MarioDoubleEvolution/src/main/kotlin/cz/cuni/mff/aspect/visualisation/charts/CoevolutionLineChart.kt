package cz.cuni.mff.aspect.visualisation.charts

import java.awt.Color
import kotlin.math.max

class CoevolutionLineChart(
    private val firstChart: EvolutionLineChart,
    private val secondChart: EvolutionLineChart,
    label: String = "Coevolution"
) {

    private val lineChart = LineChart(label, "Generations", "Fitness")

    init {
        updateChartData()
    }

    fun show() = this.lineChart.renderChart()

    fun storeChart(path: String) = this.lineChart.save(path)

    // TODO: refactor me
    private fun updateChartData() {
        val bestFitnessSeries = DataSeries("Best fitness", Color(255, 0, 0), mutableListOf())
        val averageFitnessSeries = DataSeries("Average fitness", Color(255, 113, 96), mutableListOf())
        val bestObjectiveSeries = DataSeries("Best objective value", Color(0, 0, 255), mutableListOf())
        val averageObjectiveSeries = DataSeries("Average objective value", Color(78, 147, 255), mutableListOf())

        val dataSeries = listOf(bestFitnessSeries, averageFitnessSeries, bestObjectiveSeries, averageObjectiveSeries)

        val firstChartData = this.getChartData(this.firstChart)
        val secondChartData = this.getChartData(this.secondChart)

        val coevolutionStops = mutableListOf<Double>()
        var firstEvolutionGenerationShift = 0.0
        var secondEvolutionGenerationShift = 0.0

        for (coevolutionIndex in 0 until max(firstChartData[0].size, secondChartData[0].size)) {
            if (coevolutionIndex < firstChartData[0].size) {
                secondEvolutionGenerationShift = firstChartData[0][coevolutionIndex].data.maxBy { it.first }!!.first

                firstChartData.forEachIndexed { index, chartSeries ->
                    val chartData = chartSeries[coevolutionIndex].data
                    dataSeries[index].data.addAll(chartData.map {
                            dataPoint -> Pair(dataPoint.first + firstEvolutionGenerationShift, dataPoint.second)
                    })
                }
            }
            coevolutionStops.add(bestFitnessSeries.data.maxBy { it.first }!!.first)

            if (coevolutionIndex < secondChartData[0].size) {
                firstEvolutionGenerationShift = secondChartData[0][coevolutionIndex].data.maxBy { it.first }!!.first

                secondChartData.forEachIndexed { index, chartSeries ->
                    val chartData = chartSeries[coevolutionIndex].data
                    dataSeries[index].data.addAll(chartData.map {
                            dataPoint -> Pair(dataPoint.first + secondEvolutionGenerationShift, dataPoint.second)
                    })
                }

            }
            coevolutionStops.add(bestFitnessSeries.data.maxBy { it.first }!!.first)
        }

        this.lineChart.updateChart(
            dataSeries,
            coevolutionStops
        )
    }

    /**
     * Splits the chart's data series according to chart's stops
     */
    private fun getChartData(chart: EvolutionLineChart): List<List<DataSeries>> =
        chart.dataSeries.map { dataSeries ->
            val data: List<MutableList<Pair<Double, Double>>> = dataSeries.data.withIndex()
                .groupBy { chart.stops.reversed().firstOrNull { stop -> (stop - 1) < it.index } }
                .map { it.value.map { it.value }.toMutableList() }

            data.map {
                DataSeries(dataSeries.label, dataSeries.color, it)
            }
        }

}