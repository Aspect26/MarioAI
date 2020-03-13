package cz.cuni.mff.aspect.evolution.results

import ch.idsia.agents.controllers.modules.Entities
import ch.idsia.agents.controllers.modules.Tiles
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioAction
import cz.cuni.mff.aspect.mario.controllers.MarioController

class GoingRightController : MarioController {
    override fun playAction(tiles: Tiles, entities: Entities, mario: MarioEntity): List<MarioAction> {
        return listOf(MarioAction.RUN_RIGHT)
    }
}

class GoingRightAgent : MarioAgent(
    GoingRightController()
)