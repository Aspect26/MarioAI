package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.MarioLevel
import kotlin.math.abs


class LinearityEvaluator : PCLevelEvaluator<Float> {

    override operator fun invoke(level: MarioLevel, chunkMetadata: ChunksLevelMetadata, gameStatistic: GameStatistics): Float {
        var totalHeightChange = 0
        var previousHeight = this.groundHeight(level.tiles[0])

        val startIndex = chunkMetadata.chunks.first().chunk.length
        val endIndex = level.tiles.size - chunkMetadata.chunks.last().chunk.length

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

}