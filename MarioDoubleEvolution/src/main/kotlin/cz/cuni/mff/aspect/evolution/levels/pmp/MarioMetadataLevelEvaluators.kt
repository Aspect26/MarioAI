package cz.cuni.mff.aspect.evolution.levels.pmp

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import kotlin.math.abs

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

        val enemyTypes: Int = levelMetadata.entities.filter { it > 0 }.distinct().size
        val enemiesCount = levelMetadata.enemiesCount
        val enemiesFactor = (enemiesCount / 10f).coerceAtMost(1.0f)
        val enemiesDiversityFactor = enemyTypes / 4f

        val nonLinearityFactor = (heightChangeProbability * 5f).coerceAtMost(1f)

        val featureDifferenceFactor = featureDifferenceFactor(levelMetadata.holesCount, levelMetadata.pipesCount) +
                featureDifferenceFactor(levelMetadata.holesCount, levelMetadata.billsCount) +
                featureDifferenceFactor(levelMetadata.holesCount, levelMetadata.boxPlatformsCount) +
                featureDifferenceFactor(levelMetadata.holesCount, levelMetadata.stoneColumnsCount) +
                featureDifferenceFactor(levelMetadata.pipesCount, levelMetadata.billsCount) +
                featureDifferenceFactor(levelMetadata.pipesCount, levelMetadata.boxPlatformsCount) +
                featureDifferenceFactor(levelMetadata.pipesCount, levelMetadata.stoneColumnsCount) +
                featureDifferenceFactor(levelMetadata.billsCount, levelMetadata.boxPlatformsCount) +
                featureDifferenceFactor(levelMetadata.billsCount, levelMetadata.stoneColumnsCount) +
                featureDifferenceFactor(levelMetadata.boxPlatformsCount, levelMetadata.stoneColumnsCount)

//        return distance + distance * (featureDifferenceFactor + nonLinearityFactor + enemiesDiversityFactor + enemiesFactor + jumpsFactor)
        return (distance) + distance * (featureDifferenceFactor + nonLinearityFactor + enemiesFactor)
    }

    fun featureDifferenceFactor(feature1Occurrence: Int, feature2Occurrence: Int): Float {
        val occurrenceDifference = abs(feature2Occurrence - feature1Occurrence)
        return when {
            feature1Occurrence == 0|| feature2Occurrence == 0 -> 0f
            occurrenceDifference > 0f -> 1f / occurrenceDifference
            else -> 1f
        }
    }

}
