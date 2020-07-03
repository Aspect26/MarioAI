package cz.cuni.mff.aspect.visualisation.charts.evolution

import cz.cuni.mff.aspect.visualisation.charts.DataSeries
import cz.cuni.mff.aspect.visualisation.charts.linechart.LineChart
import java.awt.Color

/**
 * Implementation of a line chart for coevolution. It gets two [EvolutionLineChart]'s in constructor parameters and
 * constructs one coevolution chart containing fitnesses from both charts and second coevolution chart containing
 * objective values from both charts.
 */
class CoevolutionLineChart(
    private val firstChart: EvolutionLineChart,
    private val secondChart: EvolutionLineChart,
    label: String = "Coevolution"
) {

    private val fitnessLineChart =
        LineChart(label, "Generations", "Fitness")
    private val objectiveLineChart = LineChart(
        label,
        "Generations",
        "Objective"
    )

    init {
        updateChartData()
    }

    fun show() {
        this.fitnessLineChart.renderChart()
        this.objectiveLineChart.renderChart()
    }

    fun storeChart(path: String) {
        val pathParts = path.split(".")
        val extension = pathParts.last()
        val fileName = pathParts.subList(0, pathParts.size - 1).joinToString(".")

        this.fitnessLineChart.save("$fileName-fitness.$extension")
        this.objectiveLineChart.save("$fileName-objective.$extension")
    }

    private fun updateChartData() {
        val firstChartData = this.getChartData(this.firstChart)
        val secondChartData = this.getChartData(this.secondChart)

        this.normalizeGenerations(firstChartData)
        this.normalizeGenerations(secondChartData)

        val firstEvolutionDataSeries = this.createEmptyDataSeries(this.firstChart.label, Color(0, 150, 0), Color(0, 150, 0))
        val secondEvolutionDataSeries = this.createEmptyDataSeries(this.secondChart.label, Color(200, 150, 0), Color(200, 150, 0))

        firstEvolutionDataSeries.asList().forEachIndexed { seriesIndex, seriesData ->
            seriesData.data = firstChartData.map { evolutionDataSeries -> evolutionDataSeries.asList()[seriesIndex].data }.flatten().toMutableList()
        }

        secondEvolutionDataSeries.asList().forEachIndexed { seriesIndex, seriesData ->
            seriesData.data = secondChartData.map { evolutionDataSeries -> evolutionDataSeries.asList()[seriesIndex].data }.flatten().toMutableList()
        }

        this.fitnessLineChart.updateChart(
            listOf(firstEvolutionDataSeries.bestFitness, firstEvolutionDataSeries.averageFitness,
                secondEvolutionDataSeries.bestFitness, secondEvolutionDataSeries.averageFitness),
            emptyList()
        )

        this.objectiveLineChart.updateChart(
            listOf(firstEvolutionDataSeries.bestObjective, firstEvolutionDataSeries.averageObjective,
                secondEvolutionDataSeries.bestObjective, secondEvolutionDataSeries.averageObjective),
            emptyList()
        )
    }

    private fun createEmptyDataSeries(evolutionLabel: String, fitnessColor: Color, objectiveColor: Color): EvolutionDataSeries =
        EvolutionDataSeries(
            DataSeries(
                "$evolutionLabel - best fitness",
                fitnessColor,
                mutableListOf()
            ),
            DataSeries(
                "$evolutionLabel - average fitness",
                fitnessColor.brighter(),
                mutableListOf()
            ),
            DataSeries(
                "$evolutionLabel - best objective value",
                objectiveColor,
                mutableListOf()
            ),
            DataSeries(
                "$evolutionLabel - average objective value",
                objectiveColor.brighter(),
                mutableListOf()
            )
        )

    /**
     * Splits the chart's data series according to chart's stops
     */
    private fun getChartData(chart: EvolutionLineChart): List<EvolutionDataSeries> {
        val allStops = chart.stops.toMutableList()
        if (chart.dataSeries.averageFitness.data.isNotEmpty())
            allStops.add(chart.dataSeries.averageFitness.data.last().first)

        return allStops.mapIndexed { stopIndex, currentStop ->
            val fromIndex = if (stopIndex == 0) 0 else chart.stops[stopIndex - 1].toInt()
            val toIndex = currentStop.toInt()

            EvolutionDataSeries(
                DataSeries(
                    data = this.copySeriesData(
                        chart.dataSeries.bestFitness.data,
                        fromIndex,
                        toIndex
                    )
                ),
                DataSeries(
                    data = this.copySeriesData(
                        chart.dataSeries.averageFitness.data,
                        fromIndex,
                        toIndex
                    )
                ),
                DataSeries(
                    data = this.copySeriesData(
                        chart.dataSeries.bestObjective.data,
                        fromIndex,
                        toIndex
                    )
                ),
                DataSeries(
                    data = this.copySeriesData(
                        chart.dataSeries.averageObjective.data,
                        fromIndex,
                        toIndex
                    )
                )
            )
        }
    }

    private fun copySeriesData(dataSeries: List<Pair<Double, Double>>, fromIndex: Int, toIndex: Int): MutableList<Pair<Double, Double>> =
        dataSeries.subList(fromIndex, toIndex).map { Pair(it.first, it.second) }.toMutableList()

    private fun normalizeGenerations(chartData: List<EvolutionDataSeries>) {
        chartData.forEachIndexed { index, evolutionDataSeries ->
            this.normalizeDataSeries(evolutionDataSeries.bestFitness, index.toDouble())
            this.normalizeDataSeries(evolutionDataSeries.bestObjective, index.toDouble())
            this.normalizeDataSeries(evolutionDataSeries.averageFitness, index.toDouble())
            this.normalizeDataSeries(evolutionDataSeries.averageObjective, index.toDouble())
        }
    }

    private fun normalizeDataSeries(dataSeries: DataSeries, shiftX: Double) {
        val minGeneration = dataSeries.data.first().first
        val maxGeneration = dataSeries.data.last().first
        val minMaxDifference = maxGeneration - minGeneration

        dataSeries.data = dataSeries.data.map { (generation, value) ->
            Pair((generation - minGeneration) / minMaxDifference + shiftX, value)
        }.toMutableList()
    }

}