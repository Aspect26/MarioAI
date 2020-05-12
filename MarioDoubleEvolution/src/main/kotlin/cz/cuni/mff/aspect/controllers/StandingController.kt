package cz.cuni.mff.aspect.controllers

import ch.idsia.agents.controllers.modules.Entities
import ch.idsia.agents.controllers.modules.Tiles
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioAction
import cz.cuni.mff.aspect.mario.controllers.MarioController

/**
 * Controller which does nothing.
 */
class StandingController : MarioController {
    override fun playAction(tiles: Tiles, entities: Entities, mario: MarioEntity): List<MarioAction> {
        return emptyList()
    }

    override fun copy(): MarioController = StandingController()
}
