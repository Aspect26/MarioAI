package cz.cuni.mff.aspect.evolution.levels.pmp

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics

typealias MetadataLevelsEvaluator<F> = (levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics, heightChangeProbability: Float) -> F

object PMPLevelEvaluators {

    fun marioDistance(levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics, heightChangeProbability: Float): Float =
        gameStatistic.finalMarioDistance

    fun distanceDiversityEnemiesLinearity(levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics, heightChangeProbability: Float): Float {
        val distance = gameStatistic.finalMarioDistance

        val featuresUsed = levelMetadata.holesCount.coerceAtMost(1) + levelMetadata.pipesCount.coerceAtMost(1)
            + levelMetadata.billsCount.coerceAtMost(1) + levelMetadata.boxPlatformsCount.coerceAtMost(1) + levelMetadata.stoneColumnsCount.coerceAtMost(1)
        val diversityFactor = featuresUsed / 5f

        val maxFeatureOccurrences = maxOf(levelMetadata.holesCount, maxOf(levelMetadata.pipesCount, maxOf(levelMetadata.billsCount,
            maxOf(levelMetadata.boxPlatformsCount, levelMetadata.stoneColumnsCount))))
        val minFeatureOccurrences = minOf(levelMetadata.holesCount, minOf(levelMetadata.pipesCount, minOf(levelMetadata.billsCount,
            minOf(levelMetadata.boxPlatformsCount, levelMetadata.stoneColumnsCount))))
        val minMaxDifference = maxFeatureOccurrences - minFeatureOccurrences

        val featureUsageFactor = when {
            minFeatureOccurrences == 0 -> 0f
            minMaxDifference > 0f -> 1f / minMaxDifference
            else -> 1f
        }

        val jumpsFactor = (gameStatistic.jumps / 40.0f).coerceAtMost(1.0f)

        val enemiesCount = levelMetadata.enemiesCount
        val enemiesFactor = (enemiesCount / 10f).coerceAtMost(1.0f)

        val linearityFactor = heightChangeProbability / 5f

        println("$maxFeatureOccurrences : $minFeatureOccurrences : $featureUsageFactor")

        return distance * (3 + featureUsageFactor + enemiesFactor + linearityFactor)
    }

}
