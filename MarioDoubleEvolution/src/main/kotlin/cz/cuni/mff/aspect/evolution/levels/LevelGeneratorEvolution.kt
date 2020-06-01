package cz.cuni.mff.aspect.evolution.levels

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.visualisation.charts.evolution.EvolutionLineChart


/**
 * Interface representing evolution of mario levels. The evolution can return multiple levels.
 *
 * @param T level generator type
 */
interface LevelGeneratorEvolution<T: LevelGenerator> {

    /**
     * An evolution chart of the current evolution.
     * @see EvolutionLineChart for more information about the chart.
     */
    var chart: EvolutionLineChart

    /**
     * Evolves a level generator using agents from the given agent factory.
     *
     * @param agentFactory factory creating agents used for level evaluations.
     */
    fun evolve(agentFactory: () -> IAgent): LevelGeneratorEvolutionResult<T>

    /**
     * Continues the evolution using given initial population.
     *
     * @param agentFactory factory creating agents used for level evaluations.
     * @param initialPopulation the initial population of the evolution
     */
    fun continueEvolution(agentFactory: () -> IAgent, initialPopulation: List<T>): LevelGeneratorEvolutionResult<T>

}
