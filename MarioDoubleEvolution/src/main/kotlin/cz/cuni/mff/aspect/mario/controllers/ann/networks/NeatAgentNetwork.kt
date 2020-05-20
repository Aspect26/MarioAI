package cz.cuni.mff.aspect.mario.controllers.ann.networks

import ch.idsia.agents.controllers.modules.Entities
import ch.idsia.agents.controllers.modules.Tiles
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity
import com.evo.NEAT.Genome
import cz.cuni.mff.aspect.mario.controllers.MarioAction
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.utils.DeepCopy
import java.io.*

/**
 * Wrapper atop neural network evolved by NEAT algorithm.
 *
 * @constructor constructs the network wrapper using given [networkSettings] and the evolved individual [genome].
 * @see Genome for more information about NEAT individuals.
 */
class NeatAgentNetwork(val networkSettings: NetworkSettings, val genome: Genome) : ControllerArtificialNetwork, Serializable {

    override fun chooseAction(tiles: Tiles, entities: Entities, mario: MarioEntity): List<MarioAction> {
        val input: FloatArray = this.createInput(tiles, entities, mario)
        val output: FloatArray = this.genome.evaluateNetwork(input)
        val actions: ArrayList<MarioAction> = ArrayList()

        this.addActionIfOutputActivated(actions, output, 0, MarioAction.RUN_LEFT)
        this.addActionIfOutputActivated(actions, output, 1, MarioAction.RUN_RIGHT)
        this.addActionIfOutputActivated(actions, output, 2, MarioAction.JUMP)
        this.addActionIfOutputActivated(actions, output, 3, MarioAction.SPECIAL)

        return actions
    }

    override fun newInstance(): ControllerArtificialNetwork {
        val genomeCopy = DeepCopy.copy(this.genome)
        return NeatAgentNetwork(this.networkSettings.copy(), genomeCopy)
    }

    override fun setNetworkWeights(weights: DoubleArray) {
        TODO("not implemented")
    }

    override fun compareTo(other: ControllerArtificialNetwork): Int {
        TODO("not implemented")
    }

    override val weightsCount: Int get() = throw UnsupportedOperationException("Calling weights counts is not supported for NEAT network")

    private fun createInput(tiles: Tiles, entities: Entities, mario: MarioEntity): FloatArray {
        val networkBuilder = NetworkInputBuilder()
            .tiles(tiles)
            .entities(entities)
            .mario(mario)
            .receptiveFieldSize(this.networkSettings.receptiveFieldSizeRow, this.networkSettings.receptiveFieldSizeColumn)
            .receptiveFieldOffset(this.networkSettings.receptiveFieldRowOffset, this.networkSettings.receptiveFieldColumnOffset)
            .useDenseInput(this.networkSettings.denseInput)
            .oneHotOnEnemies(this.networkSettings.oneHotOnEnemies)

        return networkBuilder.buildFloat()
    }

    private fun addActionIfOutputActivated(actions: ArrayList<MarioAction>, output: FloatArray, outputIndex: Int, action: MarioAction) {
        if ((output[outputIndex]) > CHOOSE_ACTION_THRESHOLD) {
            actions.add(action)
        }
    }

    companion object {
        private val serialVersionUID = -6994844472540119145L

        private const val CHOOSE_ACTION_THRESHOLD = 0.95

        const val OUTPUT_LAYER_SIZE = 4

        fun inputLayerSize(networkSettings: NetworkSettings, denseInput: Boolean = false): Int =
            2 * networkSettings.receptiveFieldSizeRow * networkSettings.receptiveFieldSizeColumn * if (denseInput) 4 else 1
    }

}
