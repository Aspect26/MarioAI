package cz.cuni.mff.aspect.evolution.levels

import ch.idsia.agents.IAgent


/**
 * Interface representing evolution of mario levels. The evolution can return multiple levels.
 */
interface LevelGeneratorEvolution {

    fun evolve(agentFactory: () -> IAgent): LevelGenerator

}