package cz.cuni.mff.aspect.visualisation.charts

data class EvolutionDataSeries(
    var bestFitness: DataSeries,
    var averageFitness: DataSeries,
    var bestObjective: DataSeries,
    var averageObjective: DataSeries
) {

    fun asList(): List<DataSeries> =
        listOf(bestFitness, averageFitness, bestObjective, averageObjective)

}