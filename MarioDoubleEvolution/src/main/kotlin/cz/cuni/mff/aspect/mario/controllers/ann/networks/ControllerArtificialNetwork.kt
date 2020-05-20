package cz.cuni.mff.aspect.mario.controllers.ann.networks

import ch.idsia.agents.controllers.modules.Entities
import ch.idsia.agents.controllers.modules.Tiles
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity
import cz.cuni.mff.aspect.mario.controllers.MarioAction


/** Interface for artificial neural networks used in [cz.cuni.mff.aspect.mario.controllers.MarioController] for action selection. */
interface ControllerArtificialNetwork : Comparable<ControllerArtificialNetwork> {

    /** Number of all weights in the network. */
    val weightsCount: Int

    /** Sets all the network's weights. */
    fun setNetworkWeights(weights: DoubleArray)

    /** Chooses an agent action in given environment. */
    fun chooseAction(tiles: Tiles, entities: Entities, mario: MarioEntity): List<MarioAction>

    /** Creates a new instance of this network. */
    fun newInstance(): ControllerArtificialNetwork

}
