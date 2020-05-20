package cz.cuni.mff.aspect.mario.controllers.ann

import java.io.Serializable

/**
 * Settings of an artificial neural network which controls Super Mario agents.
 *
 * @param receptiveFieldSizeRow number of rows in agent's receptive field size.
 * @param receptiveFieldSizeColumn number of columns in agent's receptive field size.
 * @param receptiveFieldRowOffset row offset of agent's receptive field. The default agent's location is in the middle
 * of the receptive field.
 * @param receptiveFieldColumnOffset column offset of agent's receptive field. The default agent's location is in the middle
 * of the receptive field.
 * @param hiddenLayerSize number of neurons in the hidden layer.
 * @param denseInput specifies, whether the input grid tiles should be split in a half resulting in more dense grid around
 * the agent being provided to the network.
 * @param oneHotOnEnemies specifies, whether the enemies should be encoded using one hot encoding.
 */
data class NetworkSettings(val receptiveFieldSizeRow: Int = 3,
                           val receptiveFieldSizeColumn: Int = 3,
                           val receptiveFieldRowOffset: Int = 0,
                           val receptiveFieldColumnOffset: Int = 1,
                           val hiddenLayerSize: Int = 7,
                           val denseInput: Boolean = false,
                           val oneHotOnEnemies: Boolean = false) : Serializable {
    companion object {
        private val serialVersionUID = -8169720247413687669L
    }
}
