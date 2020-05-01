package cz.cuni.mff.aspect.evolution.levels

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart


/**
 * Interface representing evolution of mario levels. The evolution can return multiple levels.
 */
interface LevelGeneratorEvolution {

    /**
     * Get line chart of the evolution
     */
    val chart: EvolutionLineChart

    /**
     * Evolved a level generator using agents from the given agent factory
     */
    fun evolve(agentFactory: () -> IAgent): LevelGenerator

}
