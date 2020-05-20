package cz.cuni.mff.aspect.mario.controllers.ann

import ch.idsia.agents.controllers.modules.Entities
import ch.idsia.agents.controllers.modules.Tiles
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity
import cz.cuni.mff.aspect.mario.controllers.MarioAction
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.ControllerArtificialNetwork
import cz.cuni.mff.aspect.mario.controllers.ann.networks.NeatAgentNetwork
import cz.cuni.mff.aspect.mario.controllers.ann.networks.HiddenLayerControllerNetwork


/** A simple controller which uses an artificial neural [network] to choose actions to play. */
class SimpleANNController(val network: ControllerArtificialNetwork) : MarioController {

    override fun playAction(tiles: Tiles, entities: Entities, mario: MarioEntity): List<MarioAction> {
        return this.network.chooseAction(tiles, entities, mario)
    }

    override fun copy(): MarioController = SimpleANNController(this.network.newInstance())

    companion object {
        private val serialVersionUID = -9169720247413687669L
    }

}
