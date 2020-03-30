package cz.cuni.mff.aspect.agents

import ch.idsia.agents.controllers.modules.Entities
import ch.idsia.agents.controllers.modules.Tiles
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioAction
import cz.cuni.mff.aspect.mario.controllers.MarioController
import java.util.*

class RandomController : MarioController {

    val random = Random()

    override fun playAction(tiles: Tiles, entities: Entities, mario: MarioEntity): List<MarioAction> {
        val action = when (random.nextInt(4)) {
            0 -> MarioAction.RUN_RIGHT
            1 -> MarioAction.RUN_LEFT
            2 -> MarioAction.JUMP
            3 -> MarioAction.SPECIAL
            else -> MarioAction.JUMP
        }
        return listOf(action)
    }

    override fun copy(): MarioController = RandomController()

}

class RandomAgent : MarioAgent(RandomController())
