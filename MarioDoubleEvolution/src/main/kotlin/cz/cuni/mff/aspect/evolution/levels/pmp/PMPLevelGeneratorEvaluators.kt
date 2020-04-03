package cz.cuni.mff.aspect.evolution.levels.pmp

import cz.cuni.mff.aspect.evolution.levels.levelDifficulty
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.utils.sumByFloat
import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.utils.min
import kotlin.math.abs
import kotlin.math.pow

typealias MetadataLevelsEvaluator<F> = (level: MarioLevel, levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics) -> F

object PMPLevelEvaluators {

    fun distanceDiversityEnemiesLinearity(level: MarioLevel, levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics): Float {
        val distance = gameStatistic.finalMarioDistance

        val featuresUsed = levelMetadata.holesCount.coerceAtMost(1) + levelMetadata.pipesCount.coerceAtMost(1)
            + levelMetadata.billsCount.coerceAtMost(1) + levelMetadata.boxPlatformsCount.coerceAtMost(1) + levelMetadata.stoneColumnsCount.coerceAtMost(1)
        val diversityFactor = featuresUsed / 5f

        val maxFeatureOccurrences = maxOf(levelMetadata.holesCount, maxOf(levelMetadata.pipesCount, maxOf(levelMetadata.billsCount,
            maxOf(levelMetadata.boxPlatformsCount, levelMetadata.stoneColumnsCount))))
        val minFeatureOccurrences = min(listOf(levelMetadata.holesCount, levelMetadata.pipesCount, levelMetadata.billsCount,
            levelMetadata.boxPlatformsCount, levelMetadata.stoneColumnsCount))
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

        val nonLinearityFactor = (averageHeightChange(levelMetadata)).coerceAtMost(1f)
        val difficultyFactor = (levelDifficulty(level) / (levelMetadata.levelLength - 2 * PMPLevelGenerator.SAFE_ZONE_LENGTH)).coerceAtMost(1f)

//        val featuresDifference = featureDifferenceFactor(levelMetadata.holesCount, levelMetadata.pipesCount) +
//                featureDifferenceFactor(levelMetadata.holesCount, levelMetadata.billsCount) +
//                featureDifferenceFactor(levelMetadata.holesCount, levelMetadata.boxPlatformsCount) +
//                featureDifferenceFactor(levelMetadata.holesCount, levelMetadata.stoneColumnsCount) +
//                featureDifferenceFactor(levelMetadata.pipesCount, levelMetadata.billsCount) +
//                featureDifferenceFactor(levelMetadata.pipesCount, levelMetadata.boxPlatformsCount) +
//                featureDifferenceFactor(levelMetadata.pipesCount, levelMetadata.stoneColumnsCount) +
//                featureDifferenceFactor(levelMetadata.billsCount, levelMetadata.boxPlatformsCount) +
//                featureDifferenceFactor(levelMetadata.billsCount, levelMetadata.stoneColumnsCount) +
//                featureDifferenceFactor(levelMetadata.boxPlatformsCount, levelMetadata.stoneColumnsCount)
//        val featureDifferencesFactor = featuresDifference / 10f



//        val featuresDifference = featureDifferenceFactor(levelMetadata.holesCount, levelMetadata.pipesCount) +
//                featureDifferenceFactor(levelMetadata.pipesCount, levelMetadata.billsCount) +
//                featureDifferenceFactor(levelMetadata.billsCount, levelMetadata.boxPlatformsCount) +
//                featureDifferenceFactor(levelMetadata.boxPlatformsCount, levelMetadata.stoneColumnsCount)
//        val featureDifferencesFactor = featuresDifference / 4f

//        val avg = (levelMetadata.holesCount + levelMetadata.pipesCount + levelMetadata.billsCount + levelMetadata.boxPlatformsCount +
//                levelMetadata.stoneColumnsCount) / 5f
//        val differenceFromAvg =
//            (abs(levelMetadata.holesCount - avg) - 3).coerceAtLeast(0f).pow(2) +
//                    (abs(levelMetadata.pipesCount - avg) - 3).coerceAtLeast(0f).pow(2) +
//                    (abs(levelMetadata.billsCount - avg) - 3).coerceAtLeast(0f).pow(2) +
//                    (abs(levelMetadata.boxPlatformsCount - avg) - 3).coerceAtLeast(0f).pow(2) +
//                    (abs(levelMetadata.stoneColumnsCount * avg) - 3).coerceAtLeast(0f).pow(2)
//
//        val divF = (if (differenceFromAvg > 1) 1f / differenceFromAvg else 1f) * (if (minFeatureOccurrences == 0) 0f else 1f)
//
//        return divF
//
//        val allFactors = listOf(nonLinearityFactor, difficultyFactor)
        val allFactors = listOf(discretize(nonLinearityFactor, floatArrayOf(0f, 0.1f, 0.3f, 0.6f, 1f)), discretize(difficultyFactor, floatArrayOf(0f, 0.1f, 0.3f, 0.6f, 1f)))
        val minFactor = min(allFactors)
//
//        return minFactor
//
//        // 3200 is max
//        val cappedDistance = distance.coerceAtMost(3200f)
//        return cappedDistance * (1 + minFactor)

//        println("$nonLinearityFactor | $difficultyFactor")
        return distance * (1 + nonLinearityFactor + difficultyFactor)
    }

    fun featureDifferenceFactor(feature1Occurrence: Int, feature2Occurrence: Int): Float {
        val occurrenceDifference = abs(feature2Occurrence - feature1Occurrence)
        return when {
            feature1Occurrence == 0 || feature2Occurrence == 0 -> 0f
            occurrenceDifference <= 5 -> 1f
            occurrenceDifference > 0f -> 1f / (occurrenceDifference - 5)
            else -> 1f
        }
    }

    private fun averageHeightChange(levelMetadata: MarioLevelMetadata): Float {
        var totalHeightChange = 0
        for (i in PMPLevelGenerator.SAFE_ZONE_LENGTH until levelMetadata.groundHeight.size - PMPLevelGenerator.SAFE_ZONE_LENGTH step 2) {
            totalHeightChange += abs(levelMetadata.groundHeight[i] - levelMetadata.groundHeight[i - 2])
        }

        return totalHeightChange.toFloat() / (levelMetadata.groundHeight.size - 2 * PMPLevelGenerator.SAFE_ZONE_LENGTH)
    }


//    fun discretize(value: Float, availableValues: FloatArray): Float {
//        val result = discretize2(value, availableValues)
//
//        println("$value -> $result")
//
//        return result
//    }

    private fun discretize(value: Float, availableValues: FloatArray): Float {
        for (availableValue in availableValues.reversed()) if (value >= availableValue) return availableValue
        return availableValues.last()
    }

}
