package cz.cuni.mff.aspect.visualisation.charts

import cz.cuni.mff.aspect.evolution.Charted
import java.awt.Color

class EvolutionLineChart(label: String = "Evolution", private val hideFirst: Int = 0, private val hideNegative: Boolean = false) :
    Charted {

    private val lineChart = LineChart(label, "Generations", "Fitness")
    private val stops = mutableListOf<Double>()

    private val data: List<Triple<String, Color, MutableList<Pair<Double, Double>>>> = listOf(
        Triple("Max fitness", Color(255, 0, 0), mutableListOf()),
        Triple("Average fitness", Color(255, 113, 96), mutableListOf()),

        Triple("Max objective value", Color(0, 0, 255), mutableListOf()),
        Triple("Average objective value", Color(78, 147, 255), mutableListOf())
    )

    val isShown get() = this.lineChart.isShown

    fun show() {
        this.lineChart.renderChart()
    }

    fun nextGeneration(maxFitness: Double, averageFitness: Double, maxObjective: Double, averageObjective: Double) {
        val currentGeneration = this.data[0].third.map { it.first }.max()?.toInt() ?: 0
        this.setGeneration(currentGeneration + 1, maxFitness, averageFitness, maxObjective, averageObjective)
    }

    fun addStop() {
        val currentGeneration = this.data[0].third.map { it.first }.max()?.toInt() ?: 0
        this.stops.add(currentGeneration.toDouble())
    }

    private fun setGeneration(generation: Int, maxFitness: Double, averageFitness: Double, maxObjective: Double, averageObjective: Double) {
        this.data[0].third.add(Pair(generation.toDouble(), if (hideNegative) 0.0.coerceAtLeast(maxFitness) else maxFitness))
        this.data[1].third.add(Pair(generation.toDouble(), if (hideNegative) 0.0.coerceAtLeast(averageFitness) else averageFitness))
        this.data[2].third.add(Pair(generation.toDouble(), if (hideNegative) 0.0.coerceAtLeast(maxObjective) else maxObjective))
        this.data[3].third.add(Pair(generation.toDouble(), if (hideNegative) 0.0.coerceAtLeast(averageObjective) else averageObjective))

        if (this.hideFirst > 0 && this.data[0].third.size > this.hideFirst) {
            val offset = this.hideFirst.coerceAtMost(this.data[0].third.size - this.hideFirst)
            val shownData = this.data.map { lineData -> Triple(lineData.first, lineData.second, lineData.third.subList(offset, lineData.third.size)) }
            this.lineChart.updateChart(shownData, this.stops.toList())
        } else {
            this.lineChart.updateChart(this.data, this.stops.toList())
        }
    }

    override fun storeChart(path: String) {
        this.lineChart.save(path)
    }

}
