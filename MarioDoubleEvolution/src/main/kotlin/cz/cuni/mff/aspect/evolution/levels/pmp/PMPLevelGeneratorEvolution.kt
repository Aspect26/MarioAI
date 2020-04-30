package cz.cuni.mff.aspect.evolution.levels.pmp

import cz.cuni.mff.aspect.evolution.levels.JeneticsLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.DistanceLinearityDifficultyCompressionDiscretizedEvaluator
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.PMPLevelGeneratorEvaluator
import cz.cuni.mff.aspect.evolution.jenetics.alterers.UpdatedGaussianMutator
import cz.cuni.mff.aspect.utils.getDoubleValues
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart
import io.jenetics.*

class PMPLevelGeneratorEvolution(
    populationSize: Int = 50,
    generationsCount: Int = 100,
    private val levelLength: Int = 200,
    private val evaluateOnLevelsCount: Int = 5,
    private val fitnessFunction: PMPLevelGeneratorEvaluator<Float> = DistanceLinearityDifficultyCompressionDiscretizedEvaluator(),
    private val maxProbability: Double = 1.0,
    chartLabel: String = "PMP Level Evolution",
    displayChart: Boolean = true
) : JeneticsLevelGeneratorEvolution(
    populationSize,
    generationsCount,
    fitnessFunction.optimize,
    alterers = arrayOf(UpdatedGaussianMutator(0.5, 0.6)),
    survivorsSelector = EliteSelector(2),
    offspringSelector = RouletteWheelSelector(),
    displayChart = displayChart,
    chart = EvolutionLineChart(chartLabel, hideNegative = false)
) {
    override fun createInitialGenotype(): Genotype<DoubleGene> =
        Genotype.of(DoubleChromosome.of(List<DoubleGene>(PMPLevelGenerator.PROBABILITIES_COUNT) { DoubleGene.of(0.0, 0.0, this.maxProbability) }))

    override fun levelGeneratorFromIndividual(genotype: Genotype<DoubleGene>): LevelGenerator =
        PMPLevelGenerator(genotype.getDoubleValues(), this.levelLength)

    override fun computeFitnessAndObjective(genotype: Genotype<DoubleGene>): Pair<Float, Float> {
        val genes = genotype.getDoubleValues()
        val levelGenerator = PMPLevelGenerator(genes, this.levelLength)

        return Pair(this.fitnessFunction(levelGenerator, this.agentFactory, this.evaluateOnLevelsCount), 0f)
    }

}
