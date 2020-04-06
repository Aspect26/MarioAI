package cz.cuni.mff.aspect.evolution.levels.chunks

import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.evolution.levels.levelDifficulty
import cz.cuni.mff.aspect.evolution.levels.pmp.LevelImageCompressor
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.MarioLevel
import kotlin.math.abs
import kotlin.math.pow

typealias ChunkedLevelEvaluator<F> = (level: MarioLevel, chunkMetadata: ChunksLevelMetadata, gameStatistic: GameStatistics) -> F

object PCLevelEvaluators {

    fun difficultyLinearityDiversity(level: MarioLevel, chunkMetadata: ChunksLevelMetadata, gameStatistic: GameStatistics): Float {
        val distance = gameStatistic.finalMarioDistance
        val maxDistance = level.pixelWidth

        // TODO: do not count the starting and ending blocks (all 3 below)
        val nonLinearityFactory = averageHeightChange(level.tiles)
        val difficultyFactor = (levelDifficulty(level) / (level.tiles.size)).coerceAtMost(1f)
//        val chunksDiversity = chunksDiversity(chunkMetadata)
//        val chunksRepetitionFactor = chunksRepetitionFactor(chunkMetadata)
        val compressionFactor = LevelImageCompressor.mediumPngSize(level).toFloat() / 200000

        return distance + (maxDistance * 1f) * (difficultyFactor + nonLinearityFactory + compressionFactor)
    }

    private fun averageHeightChange(tiles: Array<ByteArray>): Float {
        // TODO: do not use the first and last chunks
        var totalHeightChange = 0
        var previousHeight = groundHeight(tiles[0])
        for (columnIndex in 1 until tiles.size) {
            val currentHeight = groundHeight(tiles[columnIndex])
            if (currentHeight == tiles[columnIndex].size) continue
            val currentHeightChange = abs(currentHeight - previousHeight)
            totalHeightChange += currentHeightChange
            previousHeight = currentHeight
        }

        val levelLength = tiles.size
        return totalHeightChange.toFloat() / levelLength
    }

    private fun chunksDiversity(chunkMetadata: ChunksLevelMetadata): Float {
        val differentChunksUsed = chunkMetadata.chunks.map { it.chunk.name }.distinct().size
        return differentChunksUsed.toFloat() / PCLevelGenerator.DEFAULT_CHUNK_TYPES_COUNT
    }

    fun chunksRepetitionFactor(chunkMetadata: ChunksLevelMetadata): Float {
        var chunkRepetitions = 0f
        var currentRepetitionCount = 0

        for (chunkIndex in 1 until chunkMetadata.chunks.size) {
            val previousChunk = chunkMetadata.chunks[chunkIndex - 1].chunk.name
            val currentChunk = chunkMetadata.chunks[chunkIndex].chunk.name
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

    private fun groundHeight(tilesColumn: ByteArray): Int {
        for (tileIndex in tilesColumn.indices)
            if (tilesColumn[(tilesColumn.size - 1) - tileIndex] == Tiles.GRASS_TOP)
                return tileIndex

        return tilesColumn.size
    }

}
