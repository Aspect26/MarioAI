package cz.cuni.mff.aspect.evolution.levels.chunks

import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.MarioLevel
import kotlin.math.abs
import kotlin.math.pow

typealias ChunkedLevelEvaluator<F> = (level: MarioLevel, chunkMetadata: ChunksLevelMetadata, gameStatistic: GameStatistics, heightChangeProbability: Float) -> F

object PCLevelEvaluators {

    fun distanceDiversityEnemiesLinearity(level: MarioLevel, chunkMetadata: ChunksLevelMetadata, gameStatistic: GameStatistics, heightChangeProbability: Float): Float {
        val distance = gameStatistic.finalMarioDistance

        val chunksUsed: List<String> = chunkMetadata.chunks.map { it.chunk.name }.distinct()
        val chunksUsedCount = chunksUsed.size
        val diversityFactor = chunksUsedCount / (ProbabilisticChunksLevelGenerator.DEFAULT_CHUNKS_COUNT.toFloat() / 3)

        val chunkUsage = chunksUsed.map { currentChunk -> Pair(currentChunk, chunkMetadata.chunks.filter { it.chunk.name == currentChunk}.size) }.toList()
        val minChunkUsage: Int = chunkUsage.minBy { it.second }!!.second
        val maxChunkUsage: Int = chunkUsage.maxBy { it.second }!!.second
        val minMaxDifference = maxChunkUsage - minChunkUsage
        val chunkUsageFactor = if (minMaxDifference > 0f) 1f / minMaxDifference else 1f

        val jumpsFactor = (gameStatistic.jumps / 40.0f).coerceAtMost(1.0f)

        val enemyTypes: Int = level.entities.flatten().filter { it > 0 }.distinct().size
        val enemiesCount = level.entities.flatten().filter { it > 0 }.size
        val enemiesFactor = (enemiesCount / 20f).coerceAtMost(2.0f)
        val enemiesDiversityFactor = enemyTypes / ProbabilisticChunksLevelGenerator.ENEMY_TYPES_COUNT.toFloat()

        val linearityFactor = heightChangeProbability

//        println("$enemiesFactor : $diversityFactor : $jumpsFactor - ${levelChunks.joinToString(", ")}")

        return (distance / 5) + distance * chunkUsageFactor * diversityFactor * linearityFactor * enemiesFactor * enemiesDiversityFactor
//        return distance * (5 + diversityFactor + chunkUsageFactor + enemiesFactor + linearityFactor + jumpsFactor)
    }

    fun newest(level: MarioLevel, chunkMetadata: ChunksLevelMetadata, gameStatistic: GameStatistics, heightChangeProbability: Float): Float {
        val distance = gameStatistic.finalMarioDistance
        val maxDistance = level.pixelWidth

        // TODO: do not count the starting and ending blocks (all 3 below)
        val nonLinearityFactory = averageHeightChange(level.tiles)
        val difficultyFactor = (difficulty(level) / (level.tiles.size)).coerceAtMost(1f)
        val chunksDiversity = chunksDiversity(chunkMetadata)
        val chunksRepetitionFactor = chunksRepetitionFactor(chunkMetadata)

//        return chunksRepetitionFactor
        return distance + (maxDistance * 1f) * (difficultyFactor + nonLinearityFactory + chunksDiversity + chunksRepetitionFactor)
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

    // TODO: zjednotit s tym druhym v PMP Evaluators (after merge)
    // TODO: unit test me
    fun difficulty(level: MarioLevel): Float {
        val flatEntities = level.entities.flatten()

        val enemiesDifficulty = flatEntities.sumBy {
            when(it) {
                Entities.Goomba.NORMAL -> 1
                Entities.Koopa.GREEN -> 2
                Entities.Koopa.GREEN_WINGED -> 4
                Entities.Koopa.RED -> 2
                Entities.Spiky.NORMAL -> 3
                Entities.Flower.NORMAL -> 3
                else -> 0
            }
        }

        var currentHoleLength = 0
        var holesDifficulty = 0f
        for (tilesColumn in level.tiles) {
            if (tilesColumn[tilesColumn.size - 1] == Tiles.NOTHING) {
                currentHoleLength++
            } else if (currentHoleLength > 0) {
                holesDifficulty += when (currentHoleLength) {
                    0 -> 0.0f
                    1 -> 0.5f
                    2 -> 1.5f
                    3 -> 2.0f
                    4 -> 2.5f
                    else -> 3.0f
                }
                currentHoleLength = 0
            }
        }

        var billsDifficulty = 0f
        for (tilesColumn in level.tiles) {
            val billSize = tilesColumn.filter { it == Tiles.BULLET_BLASTER_TOP || it == Tiles.BULLET_BLASTER_MIDDLE || it == Tiles.BULLET_BLASTER_BOTTOM }.size
            billsDifficulty += when (billSize) {
                0 -> 0.0f
                1 -> 1.5f
                else -> 1.0f
            }
        }

//        println("${enemiesDifficulty + holesDifficulty + billsDifficulty} / ${level.tiles.size}")
        return enemiesDifficulty + holesDifficulty + billsDifficulty
    }

    private fun chunksDiversity(chunkMetadata: ChunksLevelMetadata): Float {
        val differentChunksUsed = chunkMetadata.chunks.map { it.chunk.name }.distinct().size
        return differentChunksUsed.toFloat() / ProbabilisticChunksLevelGenerator.DEFAULT_CHUNK_TYPES_COUNT
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
