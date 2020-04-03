package cz.cuni.mff.aspect.controllers

import ch.idsia.agents.controllers.modules.Entities
import ch.idsia.agents.controllers.modules.Tiles
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity
import cz.cuni.mff.aspect.mario.controllers.MarioAction
import cz.cuni.mff.aspect.mario.controllers.MarioController

class GoingRightAndJumpingController : MarioController {
    override fun playAction(tiles: Tiles, entities: Entities, mario: MarioEntity): List<MarioAction> {
        return listOf(MarioAction.RUN_RIGHT, MarioAction.JUMP)
    }

    override fun copy(): MarioController = GoingRightAndJumpingController()
}
