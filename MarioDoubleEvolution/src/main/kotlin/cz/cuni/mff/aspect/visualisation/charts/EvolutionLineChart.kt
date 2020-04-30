package cz.cuni.mff.aspect.visualisation.charts

import cz.cuni.mff.aspect.evolution.Charted
import java.awt.Color

class EvolutionLineChart(label: String = "Evolution", private val hideFirst: Int = 0, private val hideNegative: Boolean = false) :
    Charted {

    private val lineChart = LineChart(label, "Generations", "Fitness")
    private val stops = mutableListOf<Double>()

    private val data: List<Triple<String, Color, MutableList<Pair<Double, Double>>>> = listOf(
        Triple("Best fitness", Color(255, 0, 0), mutableListOf()),
        Triple("Average fitness", Color(255, 113, 96), mutableListOf()),

        Triple("Best objective value", Color(0, 0, 255), mutableListOf()),
        Triple("Average objective value", Color(78, 147, 255), mutableListOf())
    )

    private val currentGeneration get() = this.data[0].third.map { it.first }.max()?.toInt() ?: 0

    val isShown get() = this.lineChart.isShown

    val isEmpty get() = this.data[0].third.isEmpty()

    fun show() = this.lineChart.renderChart()

    fun nextGeneration(bestFitness: Double, averageFitness: Double, bestObjective: Double, averageObjective: Double) =
        this.setGeneration(currentGeneration + 1, bestFitness, averageFitness, bestObjective, averageObjective)

    fun addStop() = this.stops.add(currentGeneration.toDouble())

    private fun setGeneration(generation: Int, bestFitness: Double, averageFitness: Double, bestObjective: Double, averageObjective: Double) {
        this.data[0].third.add(Pair(generation.toDouble(), if (hideNegative) 0.0.coerceAtLeast(bestFitness) else bestFitness))
        this.data[1].third.add(Pair(generation.toDouble(), if (hideNegative) 0.0.coerceAtLeast(averageFitness) else averageFitness))
        this.data[2].third.add(Pair(generation.toDouble(), if (hideNegative) 0.0.coerceAtLeast(bestObjective) else bestObjective))
        this.data[3].third.add(Pair(generation.toDouble(), if (hideNegative) 0.0.coerceAtLeast(averageObjective) else averageObjective))

        if (this.hideFirst > 0 && this.data[0].third.size > this.hideFirst) {
            val offset = this.hideFirst.coerceAtMost(this.data[0].third.size - this.hideFirst)
            val shownData = this.data.map { lineData -> Triple(lineData.first, lineData.second, lineData.third.subList(offset, lineData.third.size)) }
            this.lineChart.updateChart(shownData, this.stops.toList())
        } else {
            this.lineChart.updateChart(this.data, this.stops.toList())
        }
    }

    override fun storeChart(path: String) = this.lineChart.save(path)

}
