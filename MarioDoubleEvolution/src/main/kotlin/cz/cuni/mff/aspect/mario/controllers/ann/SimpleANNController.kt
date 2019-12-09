package cz.cuni.mff.aspect.mario.controllers.ann

import ch.idsia.agents.controllers.modules.Entities
import ch.idsia.agents.controllers.modules.Tiles
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity
import cz.cuni.mff.aspect.mario.controllers.MarioAction
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.ControllerArtificialNetwork
import cz.cuni.mff.aspect.mario.controllers.ann.networks.NeatAgentNetwork
import cz.cuni.mff.aspect.mario.controllers.ann.networks.UpdatedAgentNetwork


/**
 * A very simple controller which uses simple ANN to control mario agent.
 */
class SimpleANNController(private var network: ControllerArtificialNetwork) : MarioController {

    override fun playAction(tiles: Tiles, entities: Entities, mario: MarioEntity): List<MarioAction> {
        return this.network.chooseAction(tiles, entities, mario)
    }

    fun setLegacy() {
        if (this.network is UpdatedAgentNetwork) {
            (this.network as UpdatedAgentNetwork).legacy = true
        }
    }

    fun setDenseInput() {
        // TODO: CRITICAL pls this :(
        if (this.network is UpdatedAgentNetwork) {
            (this.network as UpdatedAgentNetwork).denseInput = true
        }
        else if (this.network is NeatAgentNetwork) {
            (this.network as NeatAgentNetwork).denseInput = true
        }
    }

    companion object {
        private val serialVersionUID = -9169720247413687669L
    }

}
