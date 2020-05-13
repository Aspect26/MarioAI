package cz.cuni.mff.aspect.visualisation.charts

/** Represents collection of data series for an [EvolutionLineChart]. */
data class EvolutionDataSeries(
    var bestFitness: DataSeries,
    var averageFitness: DataSeries,
    var bestObjective: DataSeries,
    var averageObjective: DataSeries
) {

    fun asList(): List<DataSeries> =
        listOf(bestFitness, averageFitness, bestObjective, averageObjective)

}