package cz.cuni.mff.aspect.mario.controllers.ann

import java.io.Serializable


data class NetworkSettings(val receptiveFieldSizeRow: Int = 3,
                           val receptiveFieldSizeColumn: Int = 3,
                           val receptiveFieldRowOffset: Int = 0,
                           val receptiveFieldColumnOffset: Int = 1,
                           val hiddenLayerSize: Int = 7,
                           val denseInput: Boolean = false) : Serializable
