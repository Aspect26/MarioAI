package cz.cuni.mff.aspect.visualisation.charts

import java.awt.Color
import kotlin.math.min

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

    private fun updateChartData() {
        val evolutionDataSeries = this.createEmptyDataSeries()

        val firstChartData = this.getChartData(this.firstChart)
        val secondChartData = this.getChartData(this.secondChart)

        this.adjustGenerationStart(firstChartData, secondChartData)
        val coevolutionStops = this.createCoevolutionStops(firstChartData, secondChartData)

        evolutionDataSeries.asList().forEachIndexed { seriesIndex, dataSeries ->
            dataSeries.data = listOf(firstChartData, secondChartData).asSequence().flatten().map { evolutionDataSeries ->
                evolutionDataSeries.asList()[seriesIndex].data
            }.flatten().sortedBy { it.first }.toMutableList()
        }

        this.lineChart.updateChart(
            evolutionDataSeries.asList(),
            coevolutionStops
        )
    }

    private fun createEmptyDataSeries(): EvolutionDataSeries =
        EvolutionDataSeries(
            DataSeries("Best fitness", Color(255, 0, 0), mutableListOf()),
            DataSeries("Average fitness", Color(255, 113, 96), mutableListOf()),
            DataSeries("Best objective value", Color(0, 0, 255), mutableListOf()),
            DataSeries("Average objective value", Color(78, 147, 255), mutableListOf())
        )

    /**
     * Splits the chart's data series according to chart's stops
     */
    private fun getChartData(chart: EvolutionLineChart): List<EvolutionDataSeries> =
        chart.stops.mapIndexed { stopIndex, currentStop ->
            val fromIndex = if (stopIndex == 0) 0 else chart.stops[stopIndex - 1].toInt()
            val toIndex = currentStop.toInt()

            EvolutionDataSeries(
                DataSeries(data = this.copySeriesData(chart.dataSeries.bestFitness.data, fromIndex, toIndex)),
                DataSeries(data = this.copySeriesData(chart.dataSeries.averageFitness.data, fromIndex, toIndex)),
                DataSeries(data = this.copySeriesData(chart.dataSeries.bestObjective.data, fromIndex, toIndex)),
                DataSeries(data = this.copySeriesData(chart.dataSeries.averageObjective.data, fromIndex, toIndex))
            )
        }

    private fun copySeriesData(dataSeries: List<Pair<Double, Double>>, fromIndex: Int, toIndex: Int): MutableList<Pair<Double, Double>> =
        dataSeries.subList(fromIndex, toIndex).map { Pair(it.first, it.second) }.toMutableList()

    private fun adjustGenerationStart(firstEvolution: List<EvolutionDataSeries>, secondEvolution: List<EvolutionDataSeries>) {
        val maxCoevolutionGeneration = min(firstEvolution.size, secondEvolution.size)

        for (coevolutionGeneration in 0 until maxCoevolutionGeneration) {
            val firstEvolutionShouldStartAt = if (coevolutionGeneration == 0) 1.0 else secondEvolution[coevolutionGeneration - 1].bestFitness.data.map { it.first }.max()!! + 1
            this.adjustGenerationStart(firstEvolution[coevolutionGeneration], firstEvolutionShouldStartAt)

            val secondEvolutionShouldStartAt = firstEvolution[coevolutionGeneration].bestFitness.data.map { it.first }.max()!! + 1
            this.adjustGenerationStart(secondEvolution[coevolutionGeneration], secondEvolutionShouldStartAt)
        }
    }

    private fun adjustGenerationStart(evolutionSeries: EvolutionDataSeries, shouldStartAt: Double) {
        evolutionSeries.bestFitness = this.adjustGenerationStart(evolutionSeries.bestFitness, shouldStartAt)
        evolutionSeries.averageFitness = this.adjustGenerationStart(evolutionSeries.averageFitness, shouldStartAt)
        evolutionSeries.bestObjective = this.adjustGenerationStart(evolutionSeries.bestObjective, shouldStartAt)
        evolutionSeries.averageObjective = this.adjustGenerationStart(evolutionSeries.averageObjective, shouldStartAt)
    }

    private fun adjustGenerationStart(dataSeries: DataSeries, shouldStartAt: Double): DataSeries {
        val startsAt = dataSeries.data[0].first
        val adjustment = shouldStartAt - startsAt

        return DataSeries(dataSeries.label, dataSeries.color, dataSeries.data.map { dataPoint ->
            Pair(dataPoint.first + adjustment, dataPoint.second)
        }.toMutableList())
    }

    private fun createCoevolutionStops(firstEvolution: List<EvolutionDataSeries>, secondEvolution: List<EvolutionDataSeries>): List<Double> =
        listOf(firstEvolution, secondEvolution).flatten().map { evolutionDataSeries ->
            evolutionDataSeries.bestFitness.data.maxBy { it.first }!!.first
        }.sorted()
}