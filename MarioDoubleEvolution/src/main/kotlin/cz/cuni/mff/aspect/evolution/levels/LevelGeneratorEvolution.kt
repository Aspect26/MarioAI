package cz.cuni.mff.aspect.evolution.levels

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.visualisation.charts.evolution.EvolutionLineChart
import java.io.Serializable


/** Interface representing evolution of mario levels. The evolution can return multiple levels. */
interface LevelGeneratorEvolution : Serializable {

    /**
     * An evolution chart of the current evolution.
     * @see EvolutionLineChart for more information about the chart.
     */
    var chart: EvolutionLineChart

    /** Evolves a level generator using agents from the given agent factory. */
    fun evolve(agentFactory: () -> IAgent): LevelGenerator

}
