package cz.cuni.mff.aspect.visualisation.charts

import cz.cuni.mff.aspect.evolution.Charted
import java.awt.Color

class EvolutionLineChart(label: String = "Evolution", private val hideNegative: Boolean = false) :
    Charted {

    private val lineChart = LineChart(label, "Generations", "Fitness")
    private val stops = mutableListOf<Double>()

    private val bestFitnessSeries = SeriesData("Best fitness", Color(255, 0, 0), mutableListOf())
    private val averageFitnessSeries = SeriesData("Average fitness", Color(255, 113, 96), mutableListOf())
    private val bestObjectiveSeries = SeriesData("Best objective value", Color(0, 0, 255), mutableListOf())
    private val averageObjectiveSeries = SeriesData("Average objective value", Color(78, 147, 255), mutableListOf())

    private val dataSeries: List<SeriesData> = listOf(
        bestFitnessSeries,
        averageFitnessSeries,
        bestObjectiveSeries,
        averageObjectiveSeries
    )

    private val currentGeneration get() = this.bestFitnessSeries.data.map { it.first }.max()?.toInt() ?: 0

    val isShown get() = this.lineChart.isShown

    val isEmpty get() = this.bestFitnessSeries.data.isEmpty()

    fun show() = this.lineChart.renderChart()

    fun nextGeneration(bestFitness: Double, averageFitness: Double, bestObjective: Double, averageObjective: Double) =
        this.setGeneration(currentGeneration + 1, bestFitness, averageFitness, bestObjective, averageObjective)

    fun addStop() = this.stops.add(currentGeneration.toDouble())

    private fun setGeneration(generation: Int, bestFitness: Double, averageFitness: Double, bestObjective: Double, averageObjective: Double) {
        this.addData(generation, bestFitness, averageFitness, bestObjective, averageObjective)
        this.lineChart.updateChart(this.dataSeries, this.stops)
    }

    private fun addData(generation: Int, bestFitness: Double, averageFitness: Double, bestObjective: Double, averageObjective: Double) {
        this.bestFitnessSeries.data.add(createDataPoint(generation, bestFitness))
        this.averageFitnessSeries.data.add(createDataPoint(generation, averageFitness))
        this.bestObjectiveSeries.data.add(createDataPoint(generation, bestObjective))
        this.averageObjectiveSeries.data.add(createDataPoint(generation, averageObjective))
    }

    private fun createDataPoint(generation: Int, value: Double): Pair<Double, Double> =
        Pair(generation.toDouble(), if (hideNegative) value.coerceAtLeast(0.0) else value)

    override fun storeChart(path: String) = this.lineChart.save(path)

}
