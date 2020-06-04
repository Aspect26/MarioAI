package cz.cuni.mff.aspect.evolution.controller

import com.evo.NEAT.Genome
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.HiddenLayerControllerNetwork
import cz.cuni.mff.aspect.mario.controllers.ann.networks.NeatAgentNetwork

/** Object taking care of serializing and deserializing mario controllers. */
object ControllerSerializer {

    /**
     * Serializes the given controller to its string representation if the controller is supported. Supported
     * controllers are [cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController]. The controller can be then
     * deserialized using [deserialize].
     *
     * @param controller the controller to be serialized.
     */
    fun serialize(controller: MarioController): String {
        if (controller !is SimpleANNController) throw IllegalArgumentException("Not supported controller type '${controller.javaClass.simpleName}'")

        return when (controller.network) {
            is NeatAgentNetwork -> {
                "${ControllerIdentifiers.ANN_NEAT}:${controller.network.genome.serialize()}:" +
                        this.serializeNetworkSettings(controller.network.networkSettings)
            }
            is HiddenLayerControllerNetwork -> {
                    "${ControllerIdentifiers.ANN_HIDDEN}:${controller.network.getNetworkWeights().joinToString(",")}:" +
                        this.serializeNetworkSettings(controller.network.networkSettings)
            }
            else -> {
                throw IllegalArgumentException("Not supported controller network type '${controller.network.javaClass.simpleName}'")
            }
        }
    }

    /**
     * Deserializes mario controller from string created by [serialize].
     *
     * @param data controller serialization representation.
     */
    fun deserialize(data: String): MarioController {
        val dataParts = data.split(":")
        return when (dataParts[0]) {
            ControllerIdentifiers.ANN_NEAT -> {
                val genome = Genome.deserialize(dataParts[1])
                val networkSettings = this.deserializeNetworkSettings(dataParts[2])

                val network = NeatAgentNetwork(networkSettings, genome)
                SimpleANNController(network)
            }
            ControllerIdentifiers.ANN_HIDDEN -> {
                val weightsList = dataParts[1].split(",").map { it.toDouble() }
                val weightsArray = DoubleArray(weightsList.size) { weightsList[it] }
                val networkSettings = this.deserializeNetworkSettings(dataParts[2])

                val network = HiddenLayerControllerNetwork(networkSettings)
                network.setNetworkWeights(weightsArray)

                SimpleANNController(network)
            }
            else -> {
                throw IllegalArgumentException("Not supported controller type '${dataParts[0]}'")
            }
        }
    }

    private fun serializeNetworkSettings(networkSettings: NetworkSettings): String =
        "${networkSettings.receptiveFieldSizeRow}+${networkSettings.receptiveFieldSizeColumn}+" +
                "${networkSettings.receptiveFieldRowOffset}+${networkSettings.receptiveFieldColumnOffset}+" +
                "${networkSettings.hiddenLayerSize}+${networkSettings.denseInput}+${networkSettings.oneHotOnEnemies}"

    private fun deserializeNetworkSettings(data: String): NetworkSettings {
        val dataParts = data.split("+")
        return NetworkSettings(receptiveFieldSizeRow = dataParts[0].toInt(), receptiveFieldSizeColumn = dataParts[1].toInt(),
            receptiveFieldRowOffset = dataParts[2].toInt(), receptiveFieldColumnOffset = dataParts[3].toInt(),
            hiddenLayerSize = dataParts[4].toInt(), denseInput = dataParts[5].toBoolean(), oneHotOnEnemies = dataParts[6].toBoolean())
    }

    private object ControllerIdentifiers {
        const val ANN_HIDDEN = "HIDDEN"
        const val ANN_NEAT = "NEAT"
    }

}