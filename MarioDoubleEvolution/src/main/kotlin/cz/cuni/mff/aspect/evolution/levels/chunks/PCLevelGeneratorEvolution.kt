package cz.cuni.mff.aspect.evolution.levels.chunks

import cz.cuni.mff.aspect.evolution.jenetics.alterers.MarkovChainMutator
import cz.cuni.mff.aspect.evolution.jenetics.genotype.MarkovChainGenotypeFactory
import cz.cuni.mff.aspect.evolution.levels.JeneticsLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.LinearityEvaluator
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.PCLevelGeneratorEvaluator
import cz.cuni.mff.aspect.utils.getDoubleValues
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart
import io.jenetics.*
import io.jenetics.util.Factory


class ChunksLevelGeneratorEvolution(
    populationSize: Int = POPULATION_SIZE,
    generationsCount: Int = GENERATIONS_COUNT,
    private val fitnessFunction: PCLevelGeneratorEvaluator<Float> = LinearityEvaluator(),
    private val evaluateOnLevelsCount: Int = 5,
    private val chunksCount: Int = 35,
    chartLabel: String = "Chunks level generator evolution",
    displayChart: Boolean = true
) : JeneticsLevelGeneratorEvolution(
    populationSize,
    generationsCount,
    optimize = fitnessFunction.optimize,
    alterers = arrayOf(MarkovChainMutator(PCLevelGenerator.DEFAULT_CHUNKS_COUNT, 0.2, 0.2, 0.2)),
    survivorsSelector = EliteSelector(2),
    offspringSelector = RouletteWheelSelector(),
    displayChart = displayChart,
    chart = EvolutionLineChart(chartLabel, hideNegative = false)
) {

    override fun createInitialGenotype(): Factory<Genotype<DoubleGene>> =
        MarkovChainGenotypeFactory(PCLevelGenerator.DEFAULT_CHUNKS_COUNT, PCLevelGenerator.ENEMY_TYPES_COUNT + 1)

    override fun levelGeneratorFromIndividual(genotype: Genotype<DoubleGene>): LevelGenerator =
        PCLevelGenerator(genotype.getDoubleValues().toList(), this.chunksCount)

    override fun computeFitnessAndObjective(genotype: Genotype<DoubleGene>): Pair<Float, Float> {
        val genes = genotype.getDoubleValues()
        val levelGenerator = PCLevelGenerator(genes.toList(), this.chunksCount)

        return Pair(this.fitnessFunction(levelGenerator, this.agentFactory, this.evaluateOnLevelsCount), 0f)
    }

    companion object {
        private const val POPULATION_SIZE: Int = 70
        private const val GENERATIONS_COUNT: Int = 50
    }

}
