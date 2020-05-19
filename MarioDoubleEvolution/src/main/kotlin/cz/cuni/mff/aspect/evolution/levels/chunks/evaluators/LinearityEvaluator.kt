package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.MarioLevel
import io.jenetics.Optimize
import kotlin.math.abs

/**
 * Probabilistic Chunks level generator evaluator returning linearity metric. It is computed as an average height change
 * in the level.
 */
class LinearityEvaluator : SummingEvaluator() {

    override fun evaluateOne(level: MarioLevel, levelMetadata: ChunksLevelMetadata, gameStatistics: GameStatistics): Float {
        var totalHeightChange = 0
        var previousHeight = this.groundHeight(level.tiles[0])

        val startIndex = levelMetadata.chunks.first().chunk.length
        val endIndex = level.tiles.size - levelMetadata.chunks.last().chunk.length

        for (columnIndex in startIndex .. endIndex) {
            val currentHeight = this.groundHeight(level.tiles[columnIndex])
            if (currentHeight == level.tiles[columnIndex].size) continue
            val currentHeightChange = abs(currentHeight - previousHeight)
            totalHeightChange += currentHeightChange
            previousHeight = currentHeight
        }

        val levelLength = level.tiles.size
        return totalHeightChange.toFloat() / levelLength
    }

    private fun groundHeight(tilesColumn: ByteArray): Int {
        for (tileIndex in tilesColumn.indices)
            if (tilesColumn[(tilesColumn.size - 1) - tileIndex] == Tiles.GRASS_TOP)
                return tileIndex

        return tilesColumn.size
    }

    override val optimize: Optimize get() = Optimize.MAXIMUM

}