package cz.cuni.mff.aspect.evolution.levels.chunks

import cz.cuni.mff.aspect.evolution.jenetics.alterers.MarkovChainMutator
import cz.cuni.mff.aspect.evolution.jenetics.genotype.MarkovChainGenotypeFactory
import cz.cuni.mff.aspect.evolution.levels.JeneticsLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.PCLevelEvaluator
import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.utils.getDoubleValues
import cz.cuni.mff.aspect.visualisation.charts.evolution.EvolutionLineChart
import io.jenetics.*
import io.jenetics.util.Factory

/**
 * Implementation of a Super Mario level generator evolution using [PCLevelGenerator] algorithm for level generation.
 * The implementation is highly customizable in terms of multiple properties of the evolution, which can be specified
 * via primary constructor.
 *
 * @param populationSize population size during the evolution.
 * @param generationsCount number of generations of the evolution.
 * @param fitnessFunction function computing fitness value of individuals.
 * @param objectiveFunction function computing objective value of individuals.
 * @param evaluateOnLevelsCount number specifying on how many levels, generated by a generator represented by some individual
 * the fitness and objective functions should be computed.
 * @param chartLabel label of the evolution's chart.
 * @param displayChart specifies, whether the evolution's chart should be displayed in realtime.
 * @see EvolutionLineChart for more specifics about the chart.
 * @see PCLevelGenerator for more info about the level generation process.
 */
class PCLevelGeneratorEvolution(
    populationSize: Int,
    generationsCount: Int,
    private val fitnessFunction: PCLevelEvaluator<Float>,
    private val objectiveFunction: PCLevelEvaluator<Float>,
    private val evaluateOnLevelsCount: Int = DEFAULT_EVALUATE_ON_LEVELS_COUNT,
    private val chunksCount: Int = DEFAULT_CHUNKS_COUNT,
    chartLabel: String = DEFAULT_CHART_LABEL,
    displayChart: Boolean = true
) : JeneticsLevelGeneratorEvolution<PCLevelGenerator>(
    populationSize,
    generationsCount,
    fitnessOptimization = fitnessFunction.optimize,
    objectiveOptimization = objectiveFunction.optimize,
    alterers = arrayOf(MarkovChainMutator(PCLevelGenerator.DEFAULT_CHUNKS_COUNT, 0.2, 0.2, 0.2)),
    survivorsSelector = EliteSelector(2),
    offspringSelector = RouletteWheelSelector(),
    displayChart = displayChart,
    chartLabel = chartLabel
) {
    override fun createGenotypeFactory(): Factory<Genotype<DoubleGene>> =
        MarkovChainGenotypeFactory(PCLevelGenerator.DEFAULT_CHUNKS_COUNT, PCLevelGenerator.ENEMY_TYPES_COUNT + 1)

    override fun entityFromIndividual(genotype: Genotype<DoubleGene>): PCLevelGenerator =
        PCLevelGenerator(genotype.getDoubleValues().toList(), this.chunksCount)

    override fun entityToIndividual(levelGenerator: PCLevelGenerator): Genotype<DoubleGene> =
        Genotype.of(DoubleChromosome.of(levelGenerator.data.map { DoubleGene.of(it, 0.0, 1.0) }))

    override fun computeFitnessAndObjective(genotype: Genotype<DoubleGene>): Pair<Float, Float> {
        val levelGenerator = this.entityFromIndividual(genotype)

        val levels: MutableList<MarioLevel> = mutableListOf()
        val metadata: MutableList<ChunksLevelMetadata> = mutableListOf()
        val gameStatistics: MutableList<GameStatistics> = mutableListOf()

        repeat(this.evaluateOnLevelsCount) {
            val agent = agentFactory()
            val level = levelGenerator.generate()
            val levelMetadata = levelGenerator.lastChunksMetadata

            val marioSimulator = GameSimulator(2500)
            val currentGameStatistics = marioSimulator.playMario(agent, level, false)

            levels.add(level)
            metadata.add(levelMetadata)
            gameStatistics.add(currentGameStatistics)
        }

        return Pair(this.fitnessFunction(levels, metadata, gameStatistics), this.objectiveFunction(levels, metadata, gameStatistics))
    }

    companion object {
        private const val DEFAULT_CHUNKS_COUNT: Int = 35
        private const val DEFAULT_EVALUATE_ON_LEVELS_COUNT: Int = 5
        private const val DEFAULT_CHART_LABEL: String = "PC Level Generator Evolution"
    }

}
