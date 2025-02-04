package cz.cuni.mff.aspect.mario.controllers.ann.networks

import ch.idsia.agents.controllers.modules.Entities
import ch.idsia.agents.controllers.modules.Tiles
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity
import cz.cuni.mff.aspect.mario.controllers.MarioAction
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.cpu.nativecpu.NDArray
import org.nd4j.linalg.learning.config.Nesterovs
import java.io.Serializable


/**
 * Implementation of a [ControllerArtificialNetwork] representing an artificial neural network with one hidden layer.
 *
 * The network contains one hidden dense layer of a configurable size. The input size is also configurable, in a way that
 * the size of mario's receptive field can be specified. The output layer is always 4 neurons, each one representing
 * one of Mario's actions (go left, go right, jump and shoot).
 *
 * @see cz.cuni.mff.aspect.mario.controllers.ann.networks.NetworkInputBuilder for more info about how the network's
 * input is created.
 */
class HiddenLayerControllerNetwork(val networkSettings: NetworkSettings) : ControllerArtificialNetwork, Serializable {

    private val network: MultiLayerNetwork = this.createNetwork()

    override fun compareTo(other: ControllerArtificialNetwork): Int {
        TODO("not implemented")
    }

    override val weightsCount: Int get() = this.inputLayerSize * this.networkSettings.hiddenLayerSize +
            this.networkSettings.hiddenLayerSize * OUTPUT_LAYER_SIZE + biasSize
    private val biasSize: Int get() = this.networkSettings.hiddenLayerSize + OUTPUT_LAYER_SIZE
    private val inputLayerSize: Int get() = NetworkInputBuilder.inputSize(this.networkSettings)

    override fun newInstance(): ControllerArtificialNetwork =
        HiddenLayerControllerNetwork(this.networkSettings.copy())

    override fun chooseAction(tiles: Tiles, entities: Entities, mario: MarioEntity): List<MarioAction> {
        val input = this.createInput(tiles, entities, mario)
        val output = this.network.output(NDArray(arrayOf(input)))
        val actions: ArrayList<MarioAction> = ArrayList()

        this.addActionIfOutputActivated(actions, output, 0, MarioAction.RUN_LEFT)
        this.addActionIfOutputActivated(actions, output, 1, MarioAction.RUN_RIGHT)
        this.addActionIfOutputActivated(actions, output, 2, MarioAction.JUMP)
        this.addActionIfOutputActivated(actions, output, 3, MarioAction.SPECIAL)

        return actions
    }

    override fun setNetworkWeights(weights: DoubleArray) {
        val floatWeights = FloatArray(weights.size) { weights[it].toFloat() }
        val ndArray = NDArray(floatWeights)
        this.network.setParameters(ndArray)
    }

    fun getNetworkWeights(): DoubleArray {
        val weights = this.network.params()
        return DoubleArray(weights.length()) {
            weights.getDouble(it)
        }
    }

    private fun createNetwork(): MultiLayerNetwork {
        val multiLayerConf: MultiLayerConfiguration = NeuralNetConfiguration.Builder()
            .seed(123).learningRate(0.1).iterations(1).optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).updater(Nesterovs(0.9)).biasInit(1.0)
            .list()
            .layer(0, DenseLayer.Builder().nIn(this.inputLayerSize).nOut(this.networkSettings.hiddenLayerSize).weightInit(WeightInit.XAVIER).activation(Activation.RELU).build())
            .layer(1, OutputLayer.Builder().nIn(this.networkSettings.hiddenLayerSize).nOut(OUTPUT_LAYER_SIZE).weightInit(WeightInit.XAVIER).activation(Activation.SIGMOID).build())
            .pretrain(false).backprop(false)
            .build()

        val multiLayerNetwork = MultiLayerNetwork(multiLayerConf)
        multiLayerNetwork.init()

        return multiLayerNetwork
    }

    private fun createInput(tiles: Tiles, entities: Entities, mario: MarioEntity): DoubleArray {
        val networkInputBuilder = NetworkInputBuilder()
            .tiles(tiles)
            .entities(entities)
            .mario(mario)
            .receptiveFieldSize(this.networkSettings.receptiveFieldSizeRow, this.networkSettings.receptiveFieldSizeColumn)
            .receptiveFieldOffset(this.networkSettings.receptiveFieldRowOffset, this.networkSettings.receptiveFieldColumnOffset)
            .useDenseInput(this.networkSettings.denseInput)
            .oneHotOnEnemies(this.networkSettings.oneHotOnEnemies)

        return networkInputBuilder.buildDouble()
    }

    private fun addActionIfOutputActivated(actions: ArrayList<MarioAction>, output: INDArray, outputIndex: Int, action: MarioAction) {
        if ((output.getScalar(0, outputIndex).element() as Float) > CHOOSE_ACTION_THRESHOLD) {
            actions.add(action)
        }
    }

    companion object {
        private val serialVersionUID = -1794183199102411681L
        private const val OUTPUT_LAYER_SIZE = 4
        private const val CHOOSE_ACTION_THRESHOLD = 0.95
    }
}
