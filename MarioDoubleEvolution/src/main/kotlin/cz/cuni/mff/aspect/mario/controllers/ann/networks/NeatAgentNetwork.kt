package cz.cuni.mff.aspect.mario.controllers.ann.networks

import ch.idsia.agents.controllers.modules.Entities
import ch.idsia.agents.controllers.modules.Tiles
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity
import com.evo.NEAT.Genome
import cz.cuni.mff.aspect.mario.controllers.MarioAction
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import java.io.*


class NeatAgentNetwork(private val networkSettings: NetworkSettings, private val genome: Genome) : ControllerArtificialNetwork, Serializable {

    var denseInput: Boolean = false

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
        // TODO: check if we can do this nicer
        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(this.genome)

        val bais = ByteArrayInputStream(baos.toByteArray())
        val ois = ObjectInputStream(bais)

        val genomeCopy = ois.readObject() as Genome

        return NeatAgentNetwork(this.networkSettings.copy(), genomeCopy)
    }

    override fun setNetworkWeights(weights: DoubleArray) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun compareTo(other: ControllerArtificialNetwork): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val weightsCount: Int get() = this.inputLayerSize * this.networkSettings.hiddenLayerSize + this.networkSettings.hiddenLayerSize * OUTPUT_LAYER_SIZE
    val inputLayerSize: Int get() = 2 * this.networkSettings.receptiveFieldSizeRow * this.networkSettings.receptiveFieldSizeColumn * if (this.denseInput) 4 else 1

    private fun createInput(tiles: Tiles, entities: Entities, mario: MarioEntity): FloatArray {
        val networkBuilder = NetworkInputBuilder()
            .tiles(tiles)
            .entities(entities)
            .mario(mario)
            .receptiveFieldSize(this.networkSettings.receptiveFieldSizeRow, this.networkSettings.receptiveFieldSizeColumn)
            .receptiveFieldOffset(this.networkSettings.receptiveFieldRowOffset, this.networkSettings.receptiveFieldColumnOffset)

        if (this.denseInput)
            networkBuilder.useDenserInput()

        return networkBuilder.buildFloat()
    }

    // TODO: generalise this too!
    private fun addActionIfOutputActivated(actions: ArrayList<MarioAction>, output: FloatArray, outputIndex: Int, action: MarioAction) {
        if ((output[outputIndex]) > CHOOSE_ACTION_THRESHOLD) {
            actions.add(action)
        }
    }

    companion object {
        private val serialVersionUID = -6994844472540119145L

        private const val OUTPUT_LAYER_SIZE = 4
        private const val CHOOSE_ACTION_THRESHOLD = 0.95
    }

}
