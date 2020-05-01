package cz.cuni.mff.aspect.evolution.levels.chunks

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.evolution.jenetics.alterers.MarkovChainMutator
import cz.cuni.mff.aspect.evolution.jenetics.genotype.MarkovChainGenotypeFactory
import cz.cuni.mff.aspect.evolution.ChartedJeneticsEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.PCLevelGeneratorEvaluator
import cz.cuni.mff.aspect.utils.getDoubleValues
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart
import io.jenetics.*
import io.jenetics.util.Factory

class PCLevelGeneratorEvolution(
    populationSize: Int,
    generationsCount: Int,
    private val fitnessFunction: PCLevelGeneratorEvaluator<Float>,
    private val evaluateOnLevelsCount: Int = DEFAULT_EVALUATE_ON_LEVELS_COUNT,
    private val chunksCount: Int = DEFAULT_CHUNKS_COUNT,
    chartLabel: String = DEFAULT_CHART_LABEL,
    displayChart: Boolean = true
) : ChartedJeneticsEvolution<LevelGenerator>(
    populationSize,
    generationsCount,
    optimize = fitnessFunction.optimize,
    alterers = arrayOf(MarkovChainMutator(PCLevelGenerator.DEFAULT_CHUNKS_COUNT, 0.2, 0.2, 0.2)),
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

    override fun createInitialGenotype(): Factory<Genotype<DoubleGene>> =
        MarkovChainGenotypeFactory(PCLevelGenerator.DEFAULT_CHUNKS_COUNT, PCLevelGenerator.ENEMY_TYPES_COUNT + 1)

    override fun entityFromIndividual(genotype: Genotype<DoubleGene>): LevelGenerator =
        PCLevelGenerator(genotype.getDoubleValues().toList(), this.chunksCount)

    override fun computeFitnessAndObjective(genotype: Genotype<DoubleGene>): Pair<Float, Float> {
        val genes = genotype.getDoubleValues()
        val levelGenerator = PCLevelGenerator(genes.toList(), this.chunksCount)

        return Pair(this.fitnessFunction(levelGenerator, this.agentFactory, this.evaluateOnLevelsCount), 0f)
    }

    companion object {
        private const val DEFAULT_CHUNKS_COUNT: Int = 35
        private const val DEFAULT_EVALUATE_ON_LEVELS_COUNT: Int = 5
        private const val DEFAULT_CHART_LABEL: String = "PC Level Generator Evolution"
    }

}
