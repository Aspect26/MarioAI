package cz.cuni.mff.aspect.evolution.levels

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.evolution.ChartedJeneticsEvolution
import cz.cuni.mff.aspect.evolution.JeneticsEvolutionResult
import cz.cuni.mff.aspect.visualisation.charts.evolution.EvolutionLineChart
import io.jenetics.*

/**
 * Specialization of [JeneticsLevelGeneratorEvolution] on [LevelGenerator]s.
 *
 * @param populationSize population size during the evolution.
 * @param generationsCount number of generations of the evolution.
 * @param fitnessOptimization specifies whether fitness should be minimized or maximized (required for chart creation).
 * @param objectiveOptimization specifies whether objective values should be minimized or maximized (required for chart creation).
 * @param alterers specifies alterers (mutators) for the evolution.
 * @param survivorsSelector specifies survivors selector.
 * @param offspringSelector specifies offstrings selector.
 * @param displayChart specifies, whether the evolution's chart should be displayed in realtime.
 * @param chartLabel label of the evolution's chart.
 * @see EvolutionLineChart for more specifics about the chart.
 */
abstract class JeneticsLevelGeneratorEvolution<T: LevelGenerator>(
    populationSize: Int,
    generationsCount: Int,
    fitnessOptimization: Optimize,
    objectiveOptimization: Optimize,
    alterers: Array<Alterer<DoubleGene, Float>>,
    survivorsSelector: Selector<DoubleGene, Float>,
    offspringSelector: Selector<DoubleGene, Float>,
    displayChart: Boolean,
    chartLabel: String
) : ChartedJeneticsEvolution<T>(
    populationSize = populationSize,
    generationsCount = generationsCount,
    fitnessOptimization = fitnessOptimization,
    objectiveOptimization = objectiveOptimization,
    alterers = alterers,
    survivorsSelector = survivorsSelector,
    offspringSelector = offspringSelector,
    displayChart = displayChart,
    chart = EvolutionLineChart(
        chartLabel,
        hideNegative = false
    )
), LevelGeneratorEvolution<T> {

    protected lateinit var agentFactory: () -> IAgent

    override fun evolve(agentFactory: () -> IAgent): LevelGeneratorEvolutionResult<T> {
        this.agentFactory = agentFactory
        val evolutionResult = this.evolve()

        return this.createLevelGeneratorEvolutionResult(evolutionResult)
    }

    override fun continueEvolution(agentFactory: () -> IAgent, initialPopulation: List<T>): LevelGeneratorEvolutionResult<T> {
        this.agentFactory = agentFactory
        val evolutionResult = this.continueEvolution(initialPopulation.map(this::entityToIndividual))

        return this.createLevelGeneratorEvolutionResult(evolutionResult)
    }

    abstract fun entityToIndividual(levelGenerator: T): Genotype<DoubleGene>

    private fun createLevelGeneratorEvolutionResult(evolutionResult: JeneticsEvolutionResult<T>): LevelGeneratorEvolutionResult<T> {
        return LevelGeneratorEvolutionResult(
            evolutionResult.bestIndividual,
            evolutionResult.lastGenerationPopulation.map(this::entityFromIndividual)
        )
    }

}
