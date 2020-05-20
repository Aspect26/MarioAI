package cz.cuni.mff.aspect.mario.controllers

import ch.idsia.agents.controllers.modules.Entities
import ch.idsia.agents.controllers.modules.Tiles
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity
import java.io.Serializable


/** Interface for Super Mario game controllers, which choose, which actions should an agent play in given environment. */
interface MarioController : Serializable {

    /**
     * Specifies, which actions a Super Mario agent should play in given environment.
     *
     * @param tiles grid of tiles surrounding the agent.
     * @param entities grid of entities surrounding the agent.
     * @param mario the mario playing the game entity.
     */
    fun playAction(tiles: Tiles, entities: Entities, mario: MarioEntity): List<MarioAction>

    /** Copies this controller. */
    fun copy(): MarioController

}
