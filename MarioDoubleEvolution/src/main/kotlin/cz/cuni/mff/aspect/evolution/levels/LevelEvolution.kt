package cz.cuni.mff.aspect.evolution.levels

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.level.MarioLevel
import java.util.function.Function


/**
 * Interface representing evolution of mario levels. The evolution can return multiple levels.
 */
interface LevelEvolution {

    fun evolve(agentFactory: () -> IAgent): Array<MarioLevel>

}