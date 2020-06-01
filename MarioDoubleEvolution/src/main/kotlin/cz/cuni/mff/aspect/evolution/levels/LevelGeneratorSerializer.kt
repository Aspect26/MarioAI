package cz.cuni.mff.aspect.evolution.levels

import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator

/** Object taking care of serializing and deserializing level generators. */
object LevelGeneratorSerializer {

    /**
     * Serializes the given level generator to its string representation if the generator is supported. Supported level
     * generators are [cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator] and
     * [cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator]. The generator can be then deserialized using
     * [deserialize].
     *
     * @param levelGenerator the generator to be serialized.
     */
    fun serialize(levelGenerator: LevelGenerator): String {
        return when (levelGenerator) {
            is PMPLevelGenerator -> {
                levelGenerator.generate()
                "${GeneratorIdentifiers.PMP}:${levelGenerator.data.toList().joinToString(",")}:${levelGenerator.lastMetadata.levelLength}"
            }
            is PCLevelGenerator -> {
                levelGenerator.generate()
                "${GeneratorIdentifiers.PC}:${levelGenerator.data.joinToString(",")}:${levelGenerator.lastChunksMetadata.chunks.size}"
            }
            else -> {
                throw IllegalArgumentException("Not supported generator type '${levelGenerator.javaClass.simpleName}'")
            }
        }
    }

    /**
     * Deserializes level generator from string created by [serialize].
     *
     * @param data level generator serialization representation.
     */
    fun <T: LevelGenerator> deserialize(data: String): T {
        val dataParts = data.split(":")
        return when (dataParts[0]) {
            GeneratorIdentifiers.PMP -> {
                val probabilitiesList = dataParts[1].split(",").map { it.toDouble() }
                val probabilitiesArray = DoubleArray(probabilitiesList.size) { probabilitiesList[it] }
                val length = dataParts[2].toInt()
                PMPLevelGenerator(probabilitiesArray, length) as T
            }
            GeneratorIdentifiers.PC -> {
                val probabilitiesList = dataParts[1].split(",").map { it.toDouble() }
                val chunksCount = dataParts[2].toInt()
                PCLevelGenerator(probabilitiesList, chunksCount) as T
            }
            else -> {
                throw IllegalArgumentException("Not supported generator type '${dataParts[0]}'")
            }
        }
    }

    private object GeneratorIdentifiers {
        const val PMP = "PMP"
        const val PC = "PC"
    }

}