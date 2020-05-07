package cz.cuni.mff.aspect.evolution.levels.pmp

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.evolution.ChartedJeneticsEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.jenetics.alterers.UpdatedGaussianMutator
import cz.cuni.mff.aspect.evolution.levels.LevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.PMPLevelEvaluator
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.PMPLevelMetadata
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.utils.getDoubleValues
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart
import io.jenetics.*

class PMPLevelGeneratorEvolution(
    populationSize: Int,
    generationsCount: Int,
    private val fitnessFunction: PMPLevelEvaluator<Float>,
    private val objectiveFunction: PMPLevelEvaluator<Float>,
    private val evaluateOnLevelsCount: Int = DEFAULT_EVALUATE_ON_LEVELS_COUNT,
    private val levelLength: Int = DEFAULT_LEVEL_LENGTH,
    chartLabel: String = DEFAULT_CHART_LABEL,
    displayChart: Boolean = true
) : ChartedJeneticsEvolution<LevelGenerator>(
    populationSize,
    generationsCount,
    optimize = fitnessFunction.optimize,
    alterers = arrayOf(UpdatedGaussianMutator(0.5, 0.6)),
    survivorsSelector = EliteSelector(2),
    offspringSelector = RouletteWheelSelector(),
    displayChart = displayChart,
    chart = EvolutionLineChart(chartLabel, hideNegative = false)
), LevelGeneratorEvolution {

    private lateinit var agentFactory: () -> IAgent

    override fun evolve(agentFactory: () -> IAgent): LevelGenerator {
        this.agentFactory = agentFactory
        return this.evolve()
    }

    override fun createInitialGenotype(): Genotype<DoubleGene> =
        Genotype.of(DoubleChromosome.of(List<DoubleGene>(PMPLevelGenerator.PROBABILITIES_COUNT) { DoubleGene.of(0.0, 0.0, 1.0) }))

    override fun entityFromIndividual(genotype: Genotype<DoubleGene>): LevelGenerator =
        PMPLevelGenerator(genotype.getDoubleValues(), this.levelLength)

    override fun computeFitnessAndObjective(genotype: Genotype<DoubleGene>): Pair<Float, Float> {
        val genes = genotype.getDoubleValues()
        val levelGenerator = PMPLevelGenerator(genes, this.levelLength)

        val levels: MutableList<MarioLevel> = mutableListOf()
        val metadata: MutableList<PMPLevelMetadata> = mutableListOf()
        val gameStatistics: MutableList<GameStatistics> = mutableListOf()

        repeat(this.evaluateOnLevelsCount) {
            val agent = agentFactory()
            val level = levelGenerator.generate()
            val levelMetadata = levelGenerator.lastMetadata

            val marioSimulator = GameSimulator(2500)
            val currentGameStatistics = marioSimulator.playMario(agent, level, false)

            levels.add(level)
            metadata.add(levelMetadata)
            gameStatistics.add(currentGameStatistics)
        }

        return Pair(this.fitnessFunction(levels, metadata, gameStatistics), this.objectiveFunction(levels, metadata, gameStatistics))
    }

    companion object {
        private const val DEFAULT_LEVEL_LENGTH = 200
        private const val DEFAULT_EVALUATE_ON_LEVELS_COUNT: Int = 5
        private const val DEFAULT_CHART_LABEL = "PMP Level Generator Evolution"
    }

}
