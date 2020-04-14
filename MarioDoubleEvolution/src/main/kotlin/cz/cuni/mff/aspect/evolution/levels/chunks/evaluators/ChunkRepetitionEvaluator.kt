package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import kotlin.math.pow

class ChunkRepetitionEvaluator : PCSummingEvaluator() {

    override fun evaluateOne(level: MarioLevel, levelMetadata: ChunksLevelMetadata, gameStatistics: GameStatistics): Float {
        var chunkRepetitions = 0f
        var currentRepetitionCount = 0

        for (chunkIndex in 1 until levelMetadata.chunks.size) {
            val previousChunk = levelMetadata.chunks[chunkIndex - 1].chunk.name
            val currentChunk = levelMetadata.chunks[chunkIndex].chunk.name
            if (previousChunk == currentChunk) {
                currentRepetitionCount++
            } else if (currentRepetitionCount > 0) {
                if (currentRepetitionCount > 2) {
                    chunkRepetitions += 2.0.pow((currentRepetitionCount - 2)).toInt()
                }
                currentRepetitionCount = 0
            }
        }

        if (currentRepetitionCount > 2) {
            chunkRepetitions += 2.0.pow((currentRepetitionCount - 2)).toInt()
        }

        return if (chunkRepetitions > 0) (1 / chunkRepetitions) else 1f
    }

}