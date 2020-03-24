package cz.cuni.mff.aspect.evolution.levels.pmp

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics

typealias MetadataLevelsEvaluator<F> = (levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics) -> F

object PMPLevelEvaluators {

    fun marioDistance(levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics): Float =
        gameStatistic.finalMarioDistance

    fun marioDistanceAndLevelDiversity(levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics): Float {
        val maxObstacleOccurrences = maxOf(levelMetadata.holesCount, maxOf(levelMetadata.pipesCount, maxOf(levelMetadata.billsCount,
            maxOf(levelMetadata.boxPlatformsCount, levelMetadata.stoneColumnsCount))))

        val minObstacleOccurrences = minOf(levelMetadata.holesCount, minOf(levelMetadata.pipesCount, minOf(levelMetadata.billsCount,
            minOf(levelMetadata.boxPlatformsCount, levelMetadata.stoneColumnsCount))))

        val minMaxDifference = maxObstacleOccurrences - minObstacleOccurrences

        val obstaclesCount = levelMetadata.holesCount + levelMetadata.pipesCount + levelMetadata.billsCount
        val enemiesCount = levelMetadata.enemiesCount
        val diversityFactor: Float = if (minMaxDifference > 0) 1f / minMaxDifference else 1f
        val marioDistanceFactor = (gameStatistic.finalMarioDistance) / (levelMetadata.levelLength * 16f)

        return if (gameStatistic.levelFinished) {
            // println("${levelMetadata.holesCount} | ${levelMetadata.pipesCount} | ${levelMetadata.billsCount} | ${levelMetadata.boxPlatformsCount} | ${levelMetadata.stoneColumnsCount}")
            gameStatistic.finalMarioDistance * (1 + diversityFactor) + minObstacleOccurrences * 100
        } else {
            gameStatistic.finalMarioDistance
        }
    }

}
