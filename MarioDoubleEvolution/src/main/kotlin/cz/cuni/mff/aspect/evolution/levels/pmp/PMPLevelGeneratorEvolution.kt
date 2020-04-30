package cz.cuni.mff.aspect.evolution.levels.pmp

import cz.cuni.mff.aspect.evolution.levels.JeneticsLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.DistanceLinearityDifficultyCompressionDiscretizedEvaluator
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.PMPLevelGeneratorEvaluator
import cz.cuni.mff.aspect.evolution.jenetics.alterers.UpdatedGaussianMutator
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGeneratorEvolution
import cz.cuni.mff.aspect.utils.getDoubleValues
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart
import io.jenetics.*

class PMPLevelGeneratorEvolution(
    populationSize: Int,
    generationsCount: Int,
    private val fitnessFunction: PMPLevelGeneratorEvaluator<Float>,
    private val evaluateOnLevelsCount: Int = DEFAULT_EVALUATE_ON_LEVELS_COUNT,
    private val levelLength: Int = DEFAULT_LEVEL_LENGTH,
    chartLabel: String = DEFAULT_CHART_LABEL,
    displayChart: Boolean = true
) : JeneticsLevelGeneratorEvolution(
    populationSize,
    generationsCount,
    optimize = fitnessFunction.optimize,
    alterers = arrayOf(UpdatedGaussianMutator(0.5, 0.6)),
    survivorsSelector = EliteSelector(2),
    offspringSelector = RouletteWheelSelector(),
    displayChart = displayChart,
    chart = EvolutionLineChart(chartLabel, hideNegative = false)
) {

    override fun createInitialGenotype(): Genotype<DoubleGene> =
        Genotype.of(DoubleChromosome.of(List<DoubleGene>(PMPLevelGenerator.PROBABILITIES_COUNT) { DoubleGene.of(0.0, 0.0, 1.0) }))

    override fun levelGeneratorFromIndividual(genotype: Genotype<DoubleGene>): LevelGenerator =
        PMPLevelGenerator(genotype.getDoubleValues(), this.levelLength)

    override fun computeFitnessAndObjective(genotype: Genotype<DoubleGene>): Pair<Float, Float> {
        val genes = genotype.getDoubleValues()
        val levelGenerator = PMPLevelGenerator(genes, this.levelLength)

        return Pair(this.fitnessFunction(levelGenerator, this.agentFactory, this.evaluateOnLevelsCount), 0f)
    }

    companion object {
        private const val DEFAULT_LEVEL_LENGTH = 200
        private const val DEFAULT_EVALUATE_ON_LEVELS_COUNT: Int = 5
        private const val DEFAULT_CHART_LABEL = "PMP Level Generator Evolution"
    }

}
